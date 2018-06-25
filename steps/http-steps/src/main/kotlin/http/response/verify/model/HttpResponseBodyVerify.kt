package http.response.verify.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class HttpResponseBodyVerify @JsonCreator constructor(
        @JsonProperty("httpBodyVerifyMatchingType") var httpBodyVerifyMatchingType: HttpBodyVerifyMatchingType,
        @JsonProperty("bodyVerify") var bodyVerify: String?
)