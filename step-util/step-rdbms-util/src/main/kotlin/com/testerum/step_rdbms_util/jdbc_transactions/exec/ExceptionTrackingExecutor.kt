@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package com.testerum.step_rdbms_util.jdbc_transactions.exec

import javax.annotation.concurrent.NotThreadSafe

@NotThreadSafe
class ExceptionTrackingExecutor {

    private val actionFailures = mutableListOf<FailureExecutionResult<*>>()

    fun <R> execute(action: () -> R): ExecutionResult<R> {
        return try {
            val result = action()

            SuccessfulExecutionResult(result)
        } catch (e: Exception) {
            val failure = FailureExecutionResult<R>(e)
            actionFailures.add(failure)

            failure
        }
    }

    fun throwMultiException(): Nothing {
        val message = ""
        val cause = actionFailures[0].exception
        val enableSuppression = true
        val writableStackTrace = false

        val exceptionToThrow = ExceptionTrackingExecutorException(
            message,
            cause,
            enableSuppression,
            writableStackTrace
        )

        if (actionFailures.size > 1) {
            actionFailures.subList(1, actionFailures.size - 1).forEach { failure  ->
                exceptionToThrow.addSuppressed(failure.exception)
            }
        }

        throw exceptionToThrow
    }

}
