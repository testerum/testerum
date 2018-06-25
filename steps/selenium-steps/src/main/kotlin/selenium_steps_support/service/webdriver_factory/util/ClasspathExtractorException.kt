package selenium_steps_support.service.webdriver_factory.util

class ClasspathExtractorException : RuntimeException {

    constructor() {}

    constructor(message: String) : super(message) {}

    constructor(message: String, cause: Throwable) : super(message, cause) {}

    constructor(cause: Throwable) : super(cause) {}

}
