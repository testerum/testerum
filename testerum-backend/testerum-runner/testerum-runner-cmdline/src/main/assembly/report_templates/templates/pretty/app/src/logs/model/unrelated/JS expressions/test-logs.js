receiveModel([{"time":"2018-12-27T23:44:40.895","logLevel":"INFO","message":"Started executing test [JS expressions] at [unrelated/JS expressions.test]"},{"time":"2018-12-27T23:44:40.896","logLevel":"INFO","message":"23:44:40.895 [main] INFO com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook - start executing hook HookDef(phase=BEFORE_EACH_TEST, className=http.mock.HttpMockSteps, methodName=beforeTest, order=1000000, description=null)"},{"time":"2018-12-27T23:44:40.896","logLevel":"INFO","message":"23:44:40.896 [main] INFO com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook - done executing hook HookDef(phase=BEFORE_EACH_TEST, className=http.mock.HttpMockSteps, methodName=beforeTest, order=1000000, description=null)"},{"time":"2018-12-27T23:44:40.896","logLevel":"INFO","message":""},{"time":"2018-12-27T23:44:40.898","logLevel":"INFO","message":"Started executing step [COMPOSED: GIVEN step 3]"},{"time":"2018-12-27T23:44:40.9","logLevel":"INFO","message":"Started executing step [BASIC: GIVEN the variable <<name=today>> with value <<value={{new Date().toISOString().substr(0, 10)...>>]"},{"time":"2018-12-27T23:44:41.019","logLevel":"INFO","message":"Finished executing step [BASIC: GIVEN the variable <<name=today>> with value <<value={{new Date().toISOString().substr(0, 10)...>>]; status: [PASSED]"},{"time":"2018-12-27T23:44:41.025","logLevel":"INFO","message":"Started executing step [BASIC: THEN <<actualValue={{today}}>> is equal to <<expectedValue=2018-11-29>>]"},{"time":"2018-12-27T23:44:41.149","logLevel":"WARNING","message":"Finished executing step [BASIC: THEN <<actualValue={{today}}>> is equal to <<expectedValue=2018-11-29>>]; status: [FAILED]","exceptionDetail":{"exceptionClassName":"java.lang.AssertionError","message":"expected [1979] to be equal to [2018-11-29], but was not","stackTrace":[{"className":"assertions.AssertionsSteps","methodName":"assertEqualValues","fileName":"AssertionsSteps.kt","lineNumber":12,"nativeMethod":false},{"className":"sun.reflect.NativeMethodAccessorImpl","methodName":"invoke0","fileName":"NativeMethodAccessorImpl.java","lineNumber":-2,"nativeMethod":true},{"className":"sun.reflect.NativeMethodAccessorImpl","methodName":"invoke","fileName":"NativeMethodAccessorImpl.java","lineNumber":62,"nativeMethod":false},{"className":"sun.reflect.DelegatingMethodAccessorImpl","methodName":"invoke","fileName":"DelegatingMethodAccessorImpl.java","lineNumber":43,"nativeMethod":false},{"className":"java.lang.reflect.Method","methodName":"invoke","fileName":"Method.java","lineNumber":498,"nativeMethod":false},{"className":"com.testerum.runner_cmdline.runner_tree.nodes.step.impl.RunnerBasicStep","methodName":"doRun","fileName":"RunnerBasicStep.kt","lineNumber":53,"nativeMethod":false},{"className":"com.testerum.runner_cmdline.runner_tree.nodes.step.RunnerStep","methodName":"run","fileName":"RunnerStep.kt","lineNumber":32,"nativeMethod":false},{"className":"com.testerum.runner_cmdline.runner_tree.nodes.step.impl.RunnerComposedStep","methodName":"doRun","fileName":"RunnerComposedStep.kt","lineNumber":40,"nativeMethod":false},{"className":"com.testerum.runner_cmdline.runner_tree.nodes.step.RunnerStep","methodName":"run","fileName":"RunnerStep.kt","lineNumber":32,"nativeMethod":false},{"className":"com.testerum.runner_cmdline.runner_tree.nodes.test.RunnerTest","methodName":"tryToRun","fileName":"RunnerTest.kt","lineNumber":109,"nativeMethod":false},{"className":"com.testerum.runner_cmdline.runner_tree.nodes.test.RunnerTest","methodName":"run","fileName":"RunnerTest.kt","lineNumber":62,"nativeMethod":false},{"className":"com.testerum.runner_cmdline.runner_tree.nodes.feature.RunnerFeature","methodName":"tryToRun","fileName":"RunnerFeature.kt","lineNumber":62,"nativeMethod":false},{"className":"com.testerum.runner_cmdline.runner_tree.nodes.feature.RunnerFeature","methodName":"run","fileName":"RunnerFeature.kt","lineNumber":47,"nativeMethod":false},{"className":"com.testerum.runner_cmdline.runner_tree.nodes.suite.RunnerSuite","methodName":"runTests","fileName":"RunnerSuite.kt","lineNumber":113,"nativeMethod":false},{"className":"com.testerum.runner_cmdline.runner_tree.nodes.suite.RunnerSuite","methodName":"run","fileName":"RunnerSuite.kt","lineNumber":67,"nativeMethod":false},{"className":"com.testerum.runner_cmdline.RunnerApplication$tryToExecute$executionStatus$1","methodName":"invoke","fileName":"RunnerApplication.kt","lineNumber":116,"nativeMethod":false},{"className":"com.testerum.runner_cmdline.RunnerApplication$tryToExecute$executionStatus$1","methodName":"invoke","fileName":"RunnerApplication.kt","lineNumber":41,"nativeMethod":false},{"className":"com.testerum.common_kotlin.Classloader_utilsKt","methodName":"runWithThreadContextClassLoader","fileName":"classloader_utils.kt","lineNumber":8,"nativeMethod":false},{"className":"com.testerum.runner_cmdline.RunnerApplication","methodName":"tryToExecute","fileName":"RunnerApplication.kt","lineNumber":115,"nativeMethod":false},{"className":"com.testerum.runner_cmdline.RunnerApplication","methodName":"execute","fileName":"RunnerApplication.kt","lineNumber":60,"nativeMethod":false},{"className":"com.testerum.runner_cmdline.TesterumRunner","methodName":"main","fileName":"TesterumRunner.kt","lineNumber":44,"nativeMethod":false}],"suppressed":[],"asString":"java.lang.AssertionError: expected [1979] to be equal to [2018-11-29], but was not","asDetailedString":"java.lang.AssertionError: expected [1979] to be equal to [2018-11-29], but was not\n\tat assertions.AssertionsSteps.assertEqualValues(AssertionsSteps.kt:12)\n\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n\tat java.lang.reflect.Method.invoke(Method.java:498)\n\tat com.testerum.runner_cmdline.runner_tree.nodes.step.impl.RunnerBasicStep.doRun(RunnerBasicStep.kt:53)\n\tat com.testerum.runner_cmdline.runner_tree.nodes.step.RunnerStep.run(RunnerStep.kt:32)\n\tat com.testerum.runner_cmdline.runner_tree.nodes.step.impl.RunnerComposedStep.doRun(RunnerComposedStep.kt:40)\n\tat com.testerum.runner_cmdline.runner_tree.nodes.step.RunnerStep.run(RunnerStep.kt:32)\n\tat com.testerum.runner_cmdline.runner_tree.nodes.test.RunnerTest.tryToRun(RunnerTest.kt:109)\n\tat com.testerum.runner_cmdline.runner_tree.nodes.test.RunnerTest.run(RunnerTest.kt:62)\n\tat com.testerum.runner_cmdline.runner_tree.nodes.feature.RunnerFeature.tryToRun(RunnerFeature.kt:62)\n\tat com.testerum.runner_cmdline.runner_tree.nodes.feature.RunnerFeature.run(RunnerFeature.kt:47)\n\tat com.testerum.runner_cmdline.runner_tree.nodes.suite.RunnerSuite.runTests(RunnerSuite.kt:113)\n\tat com.testerum.runner_cmdline.runner_tree.nodes.suite.RunnerSuite.run(RunnerSuite.kt:67)\n\tat com.testerum.runner_cmdline.RunnerApplication$tryToExecute$executionStatus$1.invoke(RunnerApplication.kt:116)\n\tat com.testerum.runner_cmdline.RunnerApplication$tryToExecute$executionStatus$1.invoke(RunnerApplication.kt:41)\n\tat com.testerum.common_kotlin.Classloader_utilsKt.runWithThreadContextClassLoader(classloader_utils.kt:8)\n\tat com.testerum.runner_cmdline.RunnerApplication.tryToExecute(RunnerApplication.kt:115)\n\tat com.testerum.runner_cmdline.RunnerApplication.execute(RunnerApplication.kt:60)\n\tat com.testerum.runner_cmdline.TesterumRunner.main(TesterumRunner.kt:44)\n"}},{"time":"2018-12-27T23:44:41.174","logLevel":"INFO","message":"Started executing step [BASIC: GIVEN the variable <<name=tenDaysFromToday>> with value <<value={{(function(){var futureDate = new Date(...>>]"},{"time":"2018-12-27T23:44:41.175","logLevel":"INFO","message":"Finished executing step [BASIC: GIVEN the variable <<name=tenDaysFromToday>> with value <<value={{(function(){var futureDate = new Date(...>>]; status: [SKIPPED]"},{"time":"2018-12-27T23:44:41.178","logLevel":"INFO","message":"Started executing step [BASIC: THEN <<actualValue={{tenDaysFromToday}}>> is equal to <<expectedValue=2018-12-09>>]"},{"time":"2018-12-27T23:44:41.179","logLevel":"INFO","message":"Finished executing step [BASIC: THEN <<actualValue={{tenDaysFromToday}}>> is equal to <<expectedValue=2018-12-09>>]; status: [SKIPPED]"},{"time":"2018-12-27T23:44:41.18","logLevel":"INFO","message":"Finished executing step [COMPOSED: GIVEN step 3]; status: [FAILED]"},{"time":"2018-12-27T23:44:41.182","logLevel":"INFO","message":"23:44:41.182 [main] INFO com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook - start executing hook HookDef(phase=AFTER_EACH_TEST, className=selenium.actions.hooks.WebDriverShutdownHook, methodName=destroyWebDriver, order=2147483647, description=null)"},{"time":"2018-12-27T23:44:41.182","logLevel":"INFO","message":"23:44:41.182 [main] INFO com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook - done executing hook HookDef(phase=AFTER_EACH_TEST, className=selenium.actions.hooks.WebDriverShutdownHook, methodName=destroyWebDriver, order=2147483647, description=null)"},{"time":"2018-12-27T23:44:41.182","logLevel":"INFO","message":""},{"time":"2018-12-27T23:44:41.182","logLevel":"INFO","message":"Finished executing test [JS expressions] at [unrelated/JS expressions.test]; status: [FAILED]"},]);