package com.testerum.api.test_context.logger

import com.testerum.api.services.TesterumService

interface TesterumLogger : TesterumService {

    fun warn(message: String) = warn(message, null)
    fun warn(message: String, exception: Throwable?)

    fun info(message: String) = info(message, null)
    fun info(message: String, exception: Throwable?)

    fun debug(message: String) = info(message, null)
    fun debug(message: String, exception: Throwable?)

}
