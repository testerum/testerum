package com.somecompany.testerum_custom_steps;

import com.testerum_api.testerum_steps_api.annotations.steps.When;

public class MyCustomSteps {

    // todo: test all other annotations
    // todo: test that not all classes are listed (e.g. classes without annotations)

    @When(value = "I say hello to <<personName>>")
    public void sayHello(String personName) {
        System.out.println("Hello, " + personName);
    }

}
