package com.testerum.test_file_format.common.step_call.phase

enum class FileStepPhase(val code: String) {

    GIVEN("Given"),
    WHEN("When"),
    THEN("Then"),
    ;

    companion object {
        private val codeToInstanceMap = mapOf(
                "Given" to FileStepPhase.GIVEN,
                "When" to FileStepPhase.WHEN,
                "Then" to FileStepPhase.THEN
        )

        val codes = codeToInstanceMap.keys

        fun getByCode(code: String): FileStepPhase
                = codeToInstanceMap[code]
                ?: throw IllegalArgumentException("invalid phase [$code]; should be one of $codes]")
    }

}