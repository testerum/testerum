package com.testerum.model.manual.enums

enum class ManualTestStatus(
        /**
         * The lower this number, the more "important" this status is.
         * This is used to calculate the status of test containers in the tree of tests.
         */
        val priority: Int
) {
    NOT_EXECUTED    (1),
    IN_PROGRESS     (2),
    BLOCKED         (3),
    FAILED          (4),
    PASSED          (5),
    NOT_APPLICABLE  (6),
    ;
}