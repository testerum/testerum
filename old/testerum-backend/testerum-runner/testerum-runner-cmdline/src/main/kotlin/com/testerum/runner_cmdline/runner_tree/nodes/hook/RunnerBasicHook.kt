package com.testerum.runner_cmdline.runner_tree.nodes.hook

import com.testerum.common_kotlin.indent
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class RunnerBasicHook(private val hookDef: HookDef) : RunnerHook {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(RunnerBasicHook::class.java)
    }

    override val source: HookSource = BasicHookSource(
        className = hookDef.className,
        methodName = hookDef.methodName
    )

    override fun execute(context: RunnerContext): ExecutionStatus {
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

    override fun skip(context: RunnerContext) {
        LOG.info("skipping hook $hookDef")
    }

    override fun toString(): String = buildString { addToString(this, 0) }

    override fun addToString(destination: StringBuilder, indentLevel: Int) {
        destination.indent(indentLevel).append("basic-hook ").append(hookDef.phase)
        destination.append(" (source=").append(source).append(") ")
        destination.append("\n")
    }

}
