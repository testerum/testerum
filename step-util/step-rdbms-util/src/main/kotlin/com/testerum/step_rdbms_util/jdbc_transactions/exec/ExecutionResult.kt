package com.testerum.step_rdbms_util.jdbc_transactions.exec

import com.testerum.common_jdk.toStringWithStacktrace
import javax.annotation.concurrent.Immutable

@Immutable
class ExecutionResult<out R> private constructor(
    private val succeeded: Boolean,
    val result: R?,
    val exception: Exception?
) {

    companion object {
        fun <R> success(): ExecutionResult<R> {
            return ExecutionResult(succeeded = true, result = null, exception = null)
        }

        fun <R> success(result: R?): ExecutionResult<R> {
            return ExecutionResult(succeeded = true, result = result, exception = null)
        }

        fun <R> failure(exception: Exception): ExecutionResult<R> {
            return ExecutionResult(succeeded = false, result = null, exception = exception)
        }
    }

    fun succeeded(): Boolean {
        return this.succeeded
    }

    fun failed(): Boolean {
        return !succeeded()
    }

    override fun toString(): String {
        return "RiskyActionExecutionResult{" +
            "result=$result" +
            ", succeeded=$succeeded" +
            ", exception=${this.exception.toStringWithStacktrace()}" +
            "}"
    }

}
