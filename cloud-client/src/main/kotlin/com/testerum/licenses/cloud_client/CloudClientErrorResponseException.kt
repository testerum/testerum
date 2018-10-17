package com.testerum.licenses.cloud_client

import org.apache.http.impl.EnglishReasonPhraseCatalog
import java.util.*

class CloudClientErrorResponseException(val errorResponse: ErrorCloudResponse) : RuntimeException("HTTP status [${errorResponse.error.code} ${errorResponse.error.code.reasonPhrase}], error message [${errorResponse.error.message}]")

private val Int.reasonPhrase: String
        get() = EnglishReasonPhraseCatalog.INSTANCE.getReason(this, Locale.US)
