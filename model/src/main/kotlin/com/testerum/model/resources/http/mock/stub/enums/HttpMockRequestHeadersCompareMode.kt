package com.testerum.model.resources.http.mock.stub.enums

enum class HttpMockRequestHeadersCompareMode {
    EXACT_MATCH,
    CONTAINS,
    REGEX_MATCH,
    ABSENT,
    DOES_NOT_MATCH,
}