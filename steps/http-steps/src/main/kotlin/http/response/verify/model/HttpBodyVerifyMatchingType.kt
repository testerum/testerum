package http.response.verify.model

enum class HttpBodyVerifyMatchingType {
    JSON_VERIFY,
    EXACT_MATCH,
    CONTAINS,
    REGEX_MATCH,
    IS_EMPTY,
}