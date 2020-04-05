@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package com.testerum.step_rdbms_util.jdbc_transactions.exec

import lombok.NonNull
import javax.annotation.concurrent.NotThreadSafe

@NotThreadSafe
class ExceptionTrackingExecutor {

    private val actionFailures = mutableListOf<ExecutionResult<*>>()

    fun <R> execute(@NonNull action: () -> R): ExecutionResult<R> {
        return try {
            val result = action()

            ExecutionResult.success(result)
        } catch (e: Exception) {
            val failure = ExecutionResult.failure<R>(e)
            actionFailures.add(failure)

            failure
        }
    }

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
