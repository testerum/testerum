package selenium_steps_support.service.descriptions

object SeleniumSharedDescriptions {

    const val ELEMENT_LOCATOR_DESCRIPTION =
            "An expression used to locate an HTML element on the page.\n" +
            "The expression has the following syntax:\n" +
            "```\nelementLocatorType=elementLocatorExpression\n```\n" +
            "where ``elementLocatorType`` can be one of:\n" +
            "  - ``id`` - used to find elements by ID. For example, ``id=loginButton`` will match the elements like ``<button id=\"loginButton\">``.\n" +
            "  - ``name`` - used to find elements by their ``name`` attribute, useful for forms. For example, ``name=username`` will match elements like ``<input type=\"text\" name=\"username\">``.\n" +
            "  - ``css`` - used to find elements using a CSS selector expression. For example, ``css=p.main`` will match ``p`` elements with the CSS class ``main``.\n" +
            "  - ``linkText`` - used to find anchor elements by their text. For example, ``linkText=Login`` will match elements like ``<a href=\"...\">Login</a>``.\n" +
            "  - ``linkTextContains`` - like ``linkText``, but you don't have to specify the link's full text. For example, ``linkTextContains=Login`` will match elements like ``<a href=\"...\">Click here to Login</a>``.\n" +
            "  - ``xpath`` - used to find elements using an XPath expression. For example, ``xpath=//form[action='/login']`` will match elements like ``<form action=\"/login\" method=\"post\">``.\n" +
            "  - ``js`` - used to find elements using JavaScript. For example, ``document.getElementById('username')`` is identical to ``id=username``, but obviously more complex JavaScript expressions can be used.\n" +
            "\n" +
            "The part ``elementLocatorType=`` is optional.\n" +
            "If it's missing, the locator type will be CSS. For example, ``nav > .home`` will match elements with the CSS class ``home`` that are direct children of a ``nav`` element.\n" +
            "If multiple elements match, a step will choose only the first one, unless noted otherwise in the step's description.\n"

    const val TEXT_MATCH_EXPRESSION_DESCRIPTION =
            "An expression used to match text.\n" +
            "The expression has the following syntax:\n" +
            "```\nexpressionType=matchValue\n```\n" +
            "where ``expressionType`` can be one of:\n" +
            "  - ``exact`` - used to match text exactly as-is. For example, ``exact=some text`` will only match ``some text``.\n" +
            "  - ``caseInsensitive`` - like ``exact``, but ignoring character case. For example, ``caseInsensitive=some text`` will match ``some text``, but also ``sOMe TeXT``.\n" +
            "  - ``contains`` - used to match match parts of the text. For example, ``contains=e big`` will match ``some big text``.\n" +
            "  - ``containsCaseInsensitive`` - like ``contains``, but ignoring character case. For example, ``containsCaseInsensitive=e big`` will match ``some big text``, but also ``somE BiG text``\n" +
            "  - ``regex`` - used to match text against a [Java regular expression](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html).\n" +
            "\n" +
            "Note that the part ``expressionType=`` is optional.\n" +
            "If it's missing, the expression type will be ``exact``. For example ``some text`` will only match ``some text``.\n"

}
