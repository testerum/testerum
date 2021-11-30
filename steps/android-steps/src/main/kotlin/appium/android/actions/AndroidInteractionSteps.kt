package appium.android.actions

import appium.AppiumServiceLocator
import appium.android.AndroidStepsUtils
import appium.model.AndroidElementLocatorType
import appium.model.AppiumDriverType
import com.testerum_api.testerum_steps_api.annotations.steps.Given
import com.testerum_api.testerum_steps_api.annotations.steps.Param
import com.testerum_api.testerum_steps_api.annotations.steps.When
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import io.appium.java_client.android.Activity
import io.appium.java_client.android.AndroidDriver

class AndroidInteractionSteps {
    private val logger = TesterumServiceLocator.getTesterumLogger()

    private val driver = AppiumServiceLocator.driverManager.driver(AppiumDriverType.ANDROID) as AndroidDriver<*>

    @Given(
        value = "I start the activity <<activityName>> from package <<packagePath>>",
        description = "" //TODO: implement
    )
    fun startActivity(
        @Param(
            description = "" //TODO: implement
        )
        activityName: String,
        @Param(
            description = "" //TODO: implement
        )
        packagePath: String
    ) {
        driver.startActivity(Activity(packagePath, activityName))
    }

    @When(
        value = "I type <<text>> into the field identified by <<elementLocatorType>> <<elementLocator>>",
        description = "" //TODO: implement
    )
    fun typeTextIntoField(
        @Param(
            required = false,
            description = "" //TODO: implement
        )
        text: String?,
        @Param(
            description = "" //TODO: implement
        )
        elementLocatorType: AndroidElementLocatorType,
        @Param(
            description = "" //TODO: implement
        )
        elementLocator: String
    ) {
        val element = AndroidStepsUtils.findElement(elementLocatorType, elementLocator)
        element.sendKeys(text)
    }

    @When(
        value = "I click the element identified by <<elementLocatorType>> <<elementLocator>>",
        description = "" //TODO: implement
    )
    fun click(
        @Param(
            description = "" //TODO: implement
        )
        elementLocatorType: AndroidElementLocatorType,
        @Param(
            description = "" //TODO: implement
        )
        elementLocator: String
    ) {
        val element = AndroidStepsUtils.findElement(elementLocatorType, elementLocator)
        element.click()
    }

}
