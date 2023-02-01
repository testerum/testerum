package com.testerum.step_rdbms_util.jdbc_transactions.exec

sealed class ExecutionResult<out R>

data class SuccessfulExecutionResult<out R>(
    val result: R
) : ExecutionResult<R>()

data class FailureExecutionResult<out R>(
    val exception: Exception
) : ExecutionResult<R>()
