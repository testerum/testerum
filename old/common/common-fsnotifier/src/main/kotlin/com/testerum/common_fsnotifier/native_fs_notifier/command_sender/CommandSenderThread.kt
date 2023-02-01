package com.testerum.common_fsnotifier.native_fs_notifier.command_sender

import org.slf4j.LoggerFactory
import java.io.OutputStream
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.CountDownLatch

typealias CommandSenderAction = (commandSender: FsNotifierCommandSender) -> Unit

class CommandSenderThread(outputStream: OutputStream,
                          private val startCountDownLatch: CountDownLatch) : Thread() {

    //
    // The PipedInputStream/PipedOutputStream combination expects 2 threads:
    // - one that reads from the input stream
    // - another that writes into the input stream
    //
    // We use this class to make sure we are writing to the PipeOutputStream from a single thread.
    //

    companion object {
        private val LOG = LoggerFactory.getLogger(CommandSenderThread::class.java)
    }

    private val commandSender = FsNotifierCommandSender(outputStream)

    private val actionQueue: BlockingQueue<CommandSenderAction> = ArrayBlockingQueue(1000, true)

    private val shouldStopLock = Object()
    private var shouldStop = false

    override fun run() {
        startCountDownLatch.countDown()

        while (!shouldStop()) {
            try {
                val action: CommandSenderAction = actionQueue.take()

                action(commandSender)
            } catch (e: Exception) {
                LOG.error("failed to execute command sender action", e)
            }
        }
    }

    private fun shouldStop(): Boolean = synchronized(shouldStopLock) { shouldStop }

    fun shutdown() = synchronized(shouldStopLock) {
        shouldStop = true

        commandSender.exit()

        // make sure we are not blocked in the "actionQueue.take()" method,
        // and we can end this thread successfully
        Thread.currentThread().interrupt()
    }

    fun addAction(action: CommandSenderAction) {
        actionQueue.put(action)
    }

}
