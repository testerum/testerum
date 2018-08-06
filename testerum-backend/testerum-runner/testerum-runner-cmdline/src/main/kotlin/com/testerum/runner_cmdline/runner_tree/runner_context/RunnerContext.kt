package com.testerum.runner_cmdline.runner_tree.runner_context

import com.testerum.runner.glue_object_factory.GlueObjectFactory
import com.testerum.runner_cmdline.events.EventsService
import com.testerum.runner_cmdline.runner_tree.vars_context.TestVariablesImpl
import com.testerum.runner_cmdline.test_context.TestContextImpl
import com.testerum.runner_cmdline.transformer.TransformerFactory

data class RunnerContext(val eventsService: EventsService,
                         val stepsClassLoader: ClassLoader,
                         val glueObjectFactory: GlueObjectFactory,
                         val transformerFactory: TransformerFactory,
                         val testVariables: TestVariablesImpl,
                         val testContext: TestContextImpl)
