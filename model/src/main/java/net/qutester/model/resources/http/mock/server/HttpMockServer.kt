package net.qutester.model.resources.http.mock.server

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class HttpMockServer @JsonCreator constructor(
        @JsonProperty("port") var port: Int
)