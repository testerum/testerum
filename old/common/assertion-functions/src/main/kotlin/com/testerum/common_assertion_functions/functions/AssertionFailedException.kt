package com.testerum.common_assertion_functions.functions

class AssertionFailedException : RuntimeException {

    constructor()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)

}
