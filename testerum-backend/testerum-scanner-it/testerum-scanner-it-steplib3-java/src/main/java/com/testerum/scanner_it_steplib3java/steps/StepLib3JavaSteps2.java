package com.testerum.scanner_it_steplib3java.steps;

import com.testerum.api.annotations.settings.annotation.DeclareSetting;
import com.testerum.api.test_context.settings.model.SettingType;

@DeclareSetting(key = "stepLib3Java.param3", type = SettingType.NUMBER, defaultValue = "13", description = "param3 description", category = "param3 category")
@DeclareSetting(key = "stepLib3Java.param4", type = SettingType.TEXT  , defaultValue = "aa", description = "param4 description", category = "param4 category")
public final class StepLib3JavaSteps2 {

}
