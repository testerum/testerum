package com.testerum.web_backend.config.impl

import com.testerum.web_backend.config.TesterumWebBackendConfig

object DevTesterumWebBackendConfig : TesterumWebBackendConfig {

    override val cloudFunctionsBaseUrl: String = "http://localhost:8010/testerum-prod/europe-west1"

}
