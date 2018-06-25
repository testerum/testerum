package com.testerum.runner.runner_tree.runner_context

import com.testerum.runner.events.EventsService
import com.testerum.runner.glue_object_factory.GlueObjectFactory
import com.testerum.runner.runner_tree.vars_context.TestVariablesImpl
import com.testerum.runner.test_context.TestContextImpl
import com.testerum.runner.transformer.TransformerFactory

data class RunnerContext(val eventsService: EventsService,
                         val stepsClassLoader: ClassLoader,
                         val glueObjectFactory: GlueObjectFactory,
                         val transformerFactory: TransformerFactory,
                         val testVariables: TestVariablesImpl,
                         val testContext: TestContextImpl)
