package com.testerum.model.resources.http.mock.stub.enums

enum class HttpMockFaultResponse {
    NO_FAULT,
    CLOSE_SOCKET_WITH_NO_RESPONSE,
    SEND_BAD_HTTP_DATA_THEN_CLOSE_SOCKET,
    SEND_200_RESPONSE_THEN_BAD_HTTP_DATA_THEN_CLOSE_SOCKET,
    PEER_CONNECTION_RESET,
}