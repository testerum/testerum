package dummy_steps

import com.testerum.api.annotations.steps.When
import java.util.*

class SimpleSteps {

    companion object {
        private val RANDOM = Random()
    }

    private val stepState = RANDOM.nextInt()

    @When("I print the step state")
    fun printStepState() {
        println("====================> step state: $stepState")
    }

    @When("I execute a step without parameters")
    fun stepWithoutParameters() {
        println("====================> Executing step without parameters")
    }

    @When("I fail")
    fun fail() {
        throw AssertionError("assertion failed: expected something, but found something else :)")
    }

    @When("I throw an exception")
    fun throwAnException() {
        throw RuntimeException("throwing exception just because I can :D")
    }

    @When("simple - I type <<text>> into <<elementLocator>>")
    fun type(text: String,
             elementLocator: String) {
        println("====================> typing [$text] into [$elementLocator]")
    }

}