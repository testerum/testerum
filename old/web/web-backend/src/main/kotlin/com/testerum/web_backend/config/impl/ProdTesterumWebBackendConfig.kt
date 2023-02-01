package com.testerum.web_backend.config.impl

import com.testerum.web_backend.config.TesterumWebBackendConfig

object ProdTesterumWebBackendConfig : TesterumWebBackendConfig {

    override val cloudFunctionsBaseUrl: String = "https://europe-west1-testerum-prod.cloudfunctions.net"

}
