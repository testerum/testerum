package com.testerum.runner.events.execution_listeners

import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.ansi
import org.fusesource.jansi.AnsiConsole
import java.util.*
import javax.annotation.concurrent.NotThreadSafe

@NotThreadSafe
private class Spinner {

    companion object {
        private val GLYPHS = listOf("-", "\\", "|", "/")
    }

    private var index = 0

    fun currentValue(): String {
        val result = GLYPHS[index]

        index++
        if (index == GLYPHS.size) {
            index = 0
        }

        return result
    }

    fun reset() {
        index = 0
    }
}

private val RANDOM = Random()

fun main(args: Array<String>) {
    AnsiConsole.systemInstall()

    // todo: see also: https://gist.github.com/lovromazgon/9c801554ceb56157de30
    // todo: configurable colors (to match with the terminal colors)

    val spinner = Spinner()

    val testsCount = RANDOM.nextInt(5) + 10
    val stepsCount = RANDOM.nextInt(5) + 10

    for (testNumber in 1..testsCount) {
        print("     [ ] Test number $testNumber")

        for (stepNumber in 1..(stepsCount)) {
            Thread.sleep(100)

            print("\r${percentage((testNumber - 1) * stepsCount + stepNumber, testsCount * stepsCount)} ${ansi().fgBrightYellow()}[${ansi().fgDefault()}${ansi().fgBrightBlue()}${spinner.currentValue()}${ansi().fgDefault()}${ansi().fgBrightYellow()}]${ansi().fgDefault()} Test number $testNumber")
        }

        if (RANDOM.nextBoolean()) {
            println("\r${ansi().fgBrightYellow()}[ OK ]${ansi().fgDefault()} Test number $testNumber                   ")
        } else {
            println("\r${ansi().fgBrightRed()}${ansi().a(Ansi.Attribute.INTENSITY_BOLD)}[FAIL]${ansi().a(Ansi.Attribute.INTENSITY_BOLD_OFF)} Test number $testNumber                  ${ansi().fgDefault()}")
        }
    }
}

private fun percentage(current: Int, max: Int) = String.format("%3s%%", current * 100 / max)