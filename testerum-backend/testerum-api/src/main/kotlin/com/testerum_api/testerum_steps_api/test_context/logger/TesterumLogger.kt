package com.testerum_api.testerum_steps_api.test_context.logger

import com.testerum_api.testerum_steps_api.services.TesterumService

interface TesterumLogger : TesterumService {

    fun error(message: String) = error(message, null)
    fun error(message: String, exception: Throwable?)

    fun warn(message: String) = warn(message, null)
    fun warn(message: String, exception: Throwable?)

    fun info(message: String) = info(message, null)
    fun info(message: String, exception: Throwable?)

    fun debug(message: String) = debug(message, null)
    fun debug(message: String, exception: Throwable?)

}
