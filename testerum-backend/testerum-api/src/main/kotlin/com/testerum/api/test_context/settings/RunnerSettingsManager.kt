package com.testerum.api.test_context.settings

import com.testerum.api.services.TesterumService
import com.testerum.api.test_context.settings.model.Setting

interface RunnerSettingsManager : TesterumService {

    // todo: good API (check how it's used)

    fun getSetting(key: String): Setting?

    fun getRequiredSetting(key: String): Setting

}
