package com.testerum.step_rdbms_util.jdbc_transactions.exec

class ExceptionTrackingExecutorException(message: String,
                                         cause: Throwable,
                                         enableSuppression: Boolean,
                                         writableStackTrace: Boolean) : RuntimeException(message, cause, enableSuppression, writableStackTrace)
