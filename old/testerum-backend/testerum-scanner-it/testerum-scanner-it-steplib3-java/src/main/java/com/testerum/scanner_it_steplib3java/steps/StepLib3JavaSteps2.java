package com.testerum.scanner_it_steplib3java.steps;

import com.testerum_api.testerum_steps_api.annotations.settings.DeclareSetting;
import com.testerum_api.testerum_steps_api.test_context.settings.model.SettingType;

@DeclareSetting(
        key = "stepLib3Java.param3",
        label = "3-Param 3",
        type = SettingType.NUMBER,
        defaultValue = "13",
        description = "param3 description",
        category = "param3 category"
)
@DeclareSetting(
        key = "stepLib3Java.param4",
        label = "3-Param 4",
        type = SettingType.TEXT,
        defaultValue = "aa",
        description = "param4 description",
        category = "param4 category"
)
public final class StepLib3JavaSteps2 {

}
