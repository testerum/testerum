package net.qutester.model.resources.http.mock.stub.enums

enum class HttpMockRequestBodyMatchingType {
    JSON_VERIFY,
    EXACT_MATCH,
    CONTAINS,
    REGEX_MATCH,
    IS_EMPTY
}