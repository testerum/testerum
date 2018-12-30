package com.testerum.runner.cmdline.output_format.builder

import com.testerum.runner.cmdline.output_format.builder.impl.ConsoleDebugOutputFormatBuilder
import com.testerum.runner.cmdline.output_format.builder.impl.CustomTemplateOutputFormatBuilder
import com.testerum.runner.cmdline.output_format.builder.impl.JsonEventsOutputFormatBuilder
import com.testerum.runner.cmdline.output_format.builder.impl.JsonModelOutputFormatBuilder
import com.testerum.runner.cmdline.output_format.builder.impl.JsonStatsOutputFormatBuilder
import com.testerum.runner.cmdline.output_format.builder.impl.PrettyOutputFormatBuilder

// facade to make it easy to access all builders using auto-completion
object OutputFormatBuilders {

    fun consoleDebug(body: ConsoleDebugOutputFormatBuilder.() -> Unit): String {
        val builder = ConsoleDebugOutputFormatBuilder()

        builder.body()

        return builder.build()
    }

    fun jsonEvents(body: JsonEventsOutputFormatBuilder.() -> Unit): String {
        val builder = JsonEventsOutputFormatBuilder()

        builder.body()

        return builder.build()
    }

    fun jsonModel(body: JsonModelOutputFormatBuilder.() -> Unit): String {
        val builder = JsonModelOutputFormatBuilder()

        builder.body()

        return builder.build()
    }

    fun jsonStats(body: JsonStatsOutputFormatBuilder.() -> Unit): String {
        val builder = JsonStatsOutputFormatBuilder()

        builder.body()

        return builder.build()
    }


    fun customTemplate(body: CustomTemplateOutputFormatBuilder.() -> Unit): String {
        val builder = CustomTemplateOutputFormatBuilder()

        builder.body()

        return builder.build()
    }

    fun pretty(body: PrettyOutputFormatBuilder.() -> Unit): String {
        val builder = PrettyOutputFormatBuilder()

        builder.body()

        return builder.build()
    }

}
