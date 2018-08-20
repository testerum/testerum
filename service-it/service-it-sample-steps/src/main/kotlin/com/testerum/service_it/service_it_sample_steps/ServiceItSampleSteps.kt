package com.testerum.service_it.service_it_sample_steps

import com.testerum.api.annotations.steps.Given
import com.testerum.api.annotations.steps.Then
import com.testerum.api.annotations.steps.When

class ServiceItSampleSteps {

    @Given("some precondition")
    fun givenSomePrecondition() {}

    @When("I do some action")
    fun whenIDoSomeAction() {}

    @Then("some stuff is expected")
    fun thenSomeStuffIsExpected() {}

}
