package com.testerum.scanner_it_steplib1.steps

import com.testerum_api.testerum_steps_api.annotations.settings.DeclareSetting
import com.testerum_api.testerum_steps_api.annotations.steps.Given
import com.testerum_api.testerum_steps_api.test_context.settings.model.SettingType

@DeclareSetting(
        key = "stepLib1.other",
        label = "1-Other",
        type = SettingType.TEXT,
        defaultValue = "yep",
        description = "other description",
        category = "other category"
)
class StepLib1MyOtherSteps {

    @Given(value = "another step")
    fun givenAnotherStep() { }

}
