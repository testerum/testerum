package com.testerum.api.test_context.logger

import com.testerum.api.services.TesterumService

interface TesterumLogger : TesterumService {

    fun logWarning(message: String)

    fun logInfo(message: String)

    fun logDebug(message: String)

}