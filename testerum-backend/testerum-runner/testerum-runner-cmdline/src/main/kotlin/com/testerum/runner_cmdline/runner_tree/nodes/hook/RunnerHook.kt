package com.testerum.runner_cmdline.runner_tree.nodes.hook

import com.testerum.api.test_context.ExecutionStatus
import com.testerum.common_kotlin.indent
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class RunnerHook(private val hookDef: HookDef) {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(RunnerHook::class.java)
    }

    fun run(context: RunnerContext): ExecutionStatus {
        LOG.info("start executing hook $hookDef")
        try {
            return doRun(context)
        } finally {
            LOG.info("done executing hook $hookDef")
        }
    }

    private fun doRun(context: RunnerContext): ExecutionStatus {
        val hookClass: Class<*> = try {
            context.stepsClassLoader.loadClass(hookDef.className)
        } catch (e: Exception) {
            throw RuntimeException("failed to load glue class [${hookDef.className}]", e)
        }

        val hookMethod: Method = try {
            hookClass.getMethod(hookDef.methodName)
        } catch (e: Exception) {
            throw RuntimeException("could not find method without parameters [${hookDef.methodName}] in the class [${hookDef.className}]", e)
        }

        val hookInstance: Any = context.glueObjectFactory.getInstance(hookClass)
        try {
            hookMethod.invoke(hookInstance)

            return ExecutionStatus.PASSED
        } catch (e: InvocationTargetException) {
            throw e.cause!!
        }
    }

    fun skip() {
        LOG.info("skipping hook $hookDef")
    }

    fun getGlueClass(context: RunnerContext): Class<*>
            = try {
                context.stepsClassLoader.loadClass(hookDef.className)
            } catch (e: ClassNotFoundException) {
                throw RuntimeException("failed to load glue class [${hookDef.className}]", e)
            }

    override fun toString(): String = buildString { addToString(this, 0) }

    fun addToString(destination: StringBuilder, indentLevel: Int) {
        destination.indent(indentLevel).append("hook ").append(hookDef.phase).append(": ").append(hookDef.className).append(".").append(hookDef.methodName).append("()\n")
    }

}
