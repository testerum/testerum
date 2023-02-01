package http.response.verify.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class HttpResponseVerify  @JsonCreator constructor(
        @JsonProperty("expectedStatusCode") var expectedStatusCode: Int?,
        @JsonProperty("expectedHeaders") var expectedHeaders: List<HttpResponseHeaderVerify>? = emptyList(),
        @JsonProperty("expectedBody") var expectedBody: HttpResponseBodyVerify?
)