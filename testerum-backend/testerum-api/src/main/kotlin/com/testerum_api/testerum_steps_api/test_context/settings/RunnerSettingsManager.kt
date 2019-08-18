package com.testerum_api.testerum_steps_api.test_context.settings

import com.testerum_api.testerum_steps_api.services.TesterumService
import com.testerum_api.testerum_steps_api.test_context.settings.model.Setting

interface RunnerSettingsManager : TesterumService {

    // todo: good API (check how it's used)

    fun getSetting(key: String): Setting?

    fun getRequiredSetting(key: String): Setting

}
