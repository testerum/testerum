package com.testerum.scanner_it_steplib3java.steps;

import com.testerum.api.annotations.hooks.BeforeEachTest;
import com.testerum.api.annotations.settings.DeclareSetting;
import com.testerum.api.annotations.settings.DeclareSettings;
import com.testerum.api.test_context.settings.model.SettingType;
import com.testerum.api.annotations.steps.Given;

@DeclareSettings({
        @DeclareSetting(key = "stepLib3Java.param1", type = SettingType.NUMBER, defaultValue = "10"    , description = "param1 description", category = "param1 category"),
        @DeclareSetting(key = "stepLib3Java.param2", type = SettingType.TEXT  , defaultValue = "defVal", description = "param2 description", category = "param2 category")
})
public final class StepLib3JavaSteps {

    @Given("a step")
    public void aStep() {}

    @BeforeEachTest
    public void beforeEachTest() {}

}
