package com.testerum.common_fsnotifier.native_fs_notifier

import com.testerum.common_fsnotifier.native_fs_notifier.command_sender.CommandSenderThread
import com.testerum.common_fsnotifier.native_fs_notifier.event_interpreter.FsNotifierEventInterpreter
import com.testerum.common_fsnotifier.native_fs_notifier.event_interpreter.FsNotifierEventListener
import com.testerum.common_jdk.OsUtils
import com.testerum.common_kotlin.doesNotExist
import org.slf4j.LoggerFactory
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.listener.ProcessListener
import org.zeroturnaround.exec.stream.LogOutputStream
import org.zeroturnaround.process.ProcessUtil
import org.zeroturnaround.process.Processes
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import java.nio.file.Path as JavaPath

class NativeFsNotifier(fsNotifierBinariesDir: JavaPath) {

    companion object {
        private val LOG = LoggerFactory.getLogger(NativeFsNotifier::class.java)

        private const val MAX_TIME_TO_WAIT_FOR_PROCESS_TO_START_IN_SECONDS = 30L
        private const val MAX_TIME_TO_WAIT_FOR_COMMAND_SENDER_THREAD_TO_START_IN_SECONDS = 30L
    }

    private val fsNotifierBinariesDir: JavaPath = fsNotifierBinariesDir.toAbsolutePath().normalize()

    private val startLock = ReentrantReadWriteLock()
    private var started = false

    private val processStartCountDownLatch = CountDownLatch(1)
    private val commandSenderThreadStartCountDownLatch = CountDownLatch(1)

    private val eventInterpreter = FsNotifierEventInterpreter()
    private val pipedOutputStream = PipedOutputStream()
    private val inputStream = PipedInputStream(pipedOutputStream)
    private val commandSenderThread = CommandSenderThread(pipedOutputStream, commandSenderThreadStartCountDownLatch)

    private val processLock = ReentrantReadWriteLock()
    private var process: Process? = null
    private var failedToStart = false

    fun addListener(listener: FsNotifierEventListener) = eventInterpreter.addListener(listener)

    fun setRoots(recursiveRoots: List<JavaPath>,
                 flatRoots: List<JavaPath>) {
        startLock.read {
            if (failedToStart) {
                return
            }
            if (!started) {
                throw IllegalArgumentException("not started")
            }

            val absoluteRecursiveRoots = recursiveRoots.map { it.toAbsolutePath().normalize() }
            val absoluteFlatRoots = flatRoots.map { it.toAbsolutePath().normalize() }

            LOG.debug("watching recursiveRoots=$absoluteRecursiveRoots, flatRoots=$absoluteFlatRoots")
            commandSenderThread.addAction { commandSender ->
                commandSender.setRoots(absoluteRecursiveRoots, absoluteFlatRoots)
            }

            eventInterpreter.setRoots(absoluteRecursiveRoots, absoluteFlatRoots)
        }
    }

    fun start() {
        startLock.write {
            if (started) {
                return
            }

            val binaryFile: JavaPath = getBinaryFile()
            if (binaryFile.doesNotExist) {
                LOG.error("cannot start fsnotifier: the binary file [$binaryFile] does not exist; Testerum will not notice file changes")
                return
            }

            LOG.info("starting fsnotifier...")

            // start fsnotifier process in it's own thread (because "processExecutor.execute()" blocks until the process ends)
            var processStartException: Exception? = null
            Thread(Runnable {
                val processExecutor: ProcessExecutor = createProcessExecutor(binaryFile)

                try {
                    processExecutor.execute()
                } catch (e: Exception) {
                    processStartException = e
                } finally {
                    // make sure we can proceed, even if we can't start the process
                    processStartCountDownLatch.countDown()
                }
            }).start()

            // start command sender thread
            var commandSenderThreadStartException: Exception? = null
            try {
                commandSenderThread.start()
            } catch (e: Exception) {
                commandSenderThreadStartException = e
            } finally {
                // make sure we can proceed, even if we can't start the thread
                commandSenderThreadStartCountDownLatch.countDown()
            }

            // wait for the command sender thread to start
            val commandSenderThreadStartedInTime = commandSenderThreadStartCountDownLatch.await(MAX_TIME_TO_WAIT_FOR_COMMAND_SENDER_THREAD_TO_START_IN_SECONDS, TimeUnit.SECONDS)
            if (commandSenderThreadStartedInTime) {
                if (commandSenderThreadStartException != null) {
                    LOG.error("command sender thread failed to start; Testerum will not notice file changes", commandSenderThread)
                }
            } else {
                LOG.error("command sender thread failed to start in $MAX_TIME_TO_WAIT_FOR_COMMAND_SENDER_THREAD_TO_START_IN_SECONDS seconds; giving up")
                killProcess()
            }

            // wait for the fsnotifier process to start
            val processStartedInTime = processStartCountDownLatch.await(MAX_TIME_TO_WAIT_FOR_PROCESS_TO_START_IN_SECONDS, TimeUnit.SECONDS)
            if (processStartedInTime) {
                if (processStartException != null) {
                    LOG.error("...fsnotifier failed to start; Testerum will not notice file changes", processStartException)
                } else {
                    LOG.info("...fsnotifier started")
                }
            } else {
                LOG.error("native fsnotifier process failed to start in $MAX_TIME_TO_WAIT_FOR_PROCESS_TO_START_IN_SECONDS seconds; giving up")
                killProcess()
            }

            if (commandSenderThreadStartException == null && processStartException == null) {
                started = true
            }

            failedToStart = !started
        }
    }

    private fun createProcessExecutor(binaryFile: java.nio.file.Path): ProcessExecutor {
        return ProcessExecutor()
                .command(listOf(
                        binaryFile.toString()
                ))
                .redirectOutput(object : LogOutputStream() {
                    override fun processLine(line: String) {
                        LOG.debug("[fsnotifier-output] $line")

                        eventInterpreter.onLineReceived(line)
                    }
                })
                .redirectError(object : LogOutputStream() {
                    override fun processLine(line: String) {
                        LOG.warn("[fsnotifier] $line")
                    }
                })
                .redirectInput(inputStream)
                .listener(object : ProcessListener() {
                    override fun afterStart(process: Process, executor: ProcessExecutor) {
                        processLock.write {
                            this@NativeFsNotifier.process = process
                        }

                        processStartCountDownLatch.countDown()
                    }

                    override fun afterStop(process: Process) {
                        // make sure we don't get locked if the process starts and then dies
                        processStartCountDownLatch.countDown()
                    }
                })
    }

    fun shutdown() {
        startLock.read {
            if (!started) {
                return
            }

            LOG.info("shutting down...")

            // send a command to the process to stop
            commandSenderThread.shutdown()

            // kill process if it doesn't stop in time
            Thread.sleep(2000)

            killProcess()
        }
    }

    private fun killProcess() {
        processLock.read {
            val process = process
            if (process != null) {
                ProcessUtil.destroyGracefullyOrForcefullyAndWait(
                        Processes.newStandardProcess(process),
                        2, TimeUnit.SECONDS,
                        5, TimeUnit.SECONDS
                )
            }
        }
    }

    private fun getBinaryFile(): JavaPath {
        if (!OsUtils.IS_64BIT_ARCHITECTURE) {
            throw IllegalArgumentException("only 64bit is supported, but the architecture of this system is [${OsUtils.OS_ARCHITECTURE}]")
        }

        val binaryFile = when {
            OsUtils.IS_WINDOWS -> fsNotifierBinariesDir.resolve("windows").resolve("fsnotifier64.exe")
            OsUtils.IS_MAC     -> fsNotifierBinariesDir.resolve("mac").resolve("fsnotifier")
            OsUtils.IS_LINUX   -> fsNotifierBinariesDir.resolve("linux").resolve("fsnotifier64")
            else               -> throw IllegalArgumentException("only Windows, Mac, and Linux (all only 64bit) are supported, but the current OS is [${OsUtils.OS_NAME}]")
        }

        return binaryFile.toAbsolutePath().normalize()
    }
}
