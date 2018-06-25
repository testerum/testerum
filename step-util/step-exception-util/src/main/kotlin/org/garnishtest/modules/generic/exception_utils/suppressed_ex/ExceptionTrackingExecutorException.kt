package org.garnishtest.modules.generic.exception_utils.suppressed_ex

class ExceptionTrackingExecutorException(message: String,
                                         cause: Throwable,
                                         enableSuppression: Boolean,
                                         writableStackTrace: Boolean) : RuntimeException(message, cause, enableSuppression, writableStackTrace)
