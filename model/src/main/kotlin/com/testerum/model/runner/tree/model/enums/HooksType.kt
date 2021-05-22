package com.testerum.model.runner.tree.model.enums

enum class HooksType (val idSuffix: String, val uiName: String) {
    BEFORE_ALL_HOOKS("BEFORE_ALL_HOOKS", "Before all hooks"),
    BEFORE_EACH_HOOKS("BEFORE_EACH_HOOKS", "Before each hooks"),
    AFTER_EACH_HOOKS("AFTER_EACH_HOOKS", "After each hooks"),
    AFTER_ALL_HOOKS("AFTER_ALL_HOOKS", "After all hooks"),
    AFTER_HOOKS("AFTER_HOOKS", "After hooks"),
}
