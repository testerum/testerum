package com.testerum.common_httpclient.util

object MediaTypeUtils {

    fun isJsonMediaType(mediaType: String): Boolean {
        return mediaType == "application/json"
                || mediaType == "application/x-json"
                || mediaType.endsWith("+json")
    }

}
