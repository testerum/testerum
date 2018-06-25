package org.garnishtest.modules.generic.exception_utils.suppressed_ex.action

import org.garnishtest.modules.generic.exception_utils.toStringWithStacktrace
import lombok.NonNull
import javax.annotation.concurrent.Immutable

@Immutable
class RiskyActionExecutionResult<out R> private constructor(private val succeeded: Boolean,
                                                            val result: R?,
                                                            val exception: Exception?) {

    companion object {
        fun <R> success(): RiskyActionExecutionResult<R> {
            return RiskyActionExecutionResult(succeeded = true, result = null, exception = null)
        }

        fun <R> success(result: R?): RiskyActionExecutionResult<R> {
            return RiskyActionExecutionResult(succeeded = true, result = result, exception = null)
        }

        fun <R> failure(@NonNull exception: Exception): RiskyActionExecutionResult<R> {
            return RiskyActionExecutionResult(succeeded = false, result = null, exception = exception)
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
