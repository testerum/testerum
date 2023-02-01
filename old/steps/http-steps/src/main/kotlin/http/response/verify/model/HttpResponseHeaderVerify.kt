package http.response.verify.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class HttpResponseHeaderVerify @JsonCreator constructor(
        @JsonProperty("key") var key: String?,
        @JsonProperty("compareMode") var compareMode: HttpResponseVerifyHeadersCompareMode?,
        @JsonProperty("value") var value: String?
)