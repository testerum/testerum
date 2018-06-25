@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package org.garnishtest.modules.generic.exception_utils.suppressed_ex

import lombok.NonNull
import org.garnishtest.modules.generic.exception_utils.suppressed_ex.action.RiskyAction
import org.garnishtest.modules.generic.exception_utils.suppressed_ex.action.RiskyActionExecutionResult
import javax.annotation.concurrent.NotThreadSafe

@NotThreadSafe
class ExceptionTrackingExecutor {

    private val actionFailures = mutableListOf<RiskyActionExecutionResult<*>>()

    fun <R> execute(@NonNull action: RiskyAction<R>): RiskyActionExecutionResult<R> {
        try {
            val result = action.execute()

            return RiskyActionExecutionResult.success(result)
        } catch (e: Exception) {
            val failure = RiskyActionExecutionResult.failure<R>(e)
            actionFailures.add(failure)

            return failure
        }
    }

    @Throws(ExceptionTrackingExecutorException::class)
    fun throwIfNeeded() {
        if (actionFailures.size == 0) {
            return
        }

        val message = ""
        val cause = actionFailures[0].exception
        val enableSuppression = true
        val writableStackTrace = false

        val exceptionToThrow = ExceptionTrackingExecutorException(
                message,
                cause!!,
                enableSuppression,
                writableStackTrace)

        if (actionFailures.size > 1) {
            actionFailures.subList(1, actionFailures.size - 1).forEach { failure ->
                // cast to gain access to the "addSuppressed" method that is otherwise not supported by Kotlin (which currently - version 1.0.6 - only targets JDK 6)
                @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
                (exceptionToThrow as Throwable).addSuppressed(failure.exception)
            }
        }

        throw exceptionToThrow
    }

}
