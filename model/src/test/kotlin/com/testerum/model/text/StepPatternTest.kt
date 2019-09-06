package com.testerum.model.text

import com.testerum.model.text.parts.ParamStepPatternPart
import com.testerum.model.text.parts.TextStepPatternPart
import com.testerum.model.text.parts.param_meta.StringTypeMeta
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.hamcrest.Matchers.`is` as Is

class StepPatternTest {

    @Test
    fun `append to empty pattern`() {
        val stepPattern = StepPattern(
                patternParts = emptyList()
        )

        assertThat(
                stepPattern.appendPart(
                        TextStepPatternPart("text")
                ),
                Is(equalTo(
                        StepPattern(
                                patternParts = listOf(
                                        TextStepPatternPart("text")
                                )
                        )
                ))
        )
    }

    @Test
    fun `append without merging`() {
        val stepPattern = StepPattern(
                patternParts = listOf(
                        TextStepPatternPart("t-1"),
                        ParamStepPatternPart(name = "p-1", typeMeta = StringTypeMeta())
                )
        )

        assertThat(
                stepPattern.appendPart(
                        TextStepPatternPart("text")
                ),
                Is(equalTo(
                        StepPattern(
                                patternParts = listOf(
                                        TextStepPatternPart("t-1"),
                                        ParamStepPatternPart(name = "p-1", typeMeta = StringTypeMeta()),
                                        TextStepPatternPart("text")
                                )
                        )
                ))
        )
    }

    @Test
    fun `append merging into last text part`() {
        val stepPattern = StepPattern(
                patternParts = listOf(
                        TextStepPatternPart("t-1"),
                        ParamStepPatternPart(name = "p-1", typeMeta = StringTypeMeta()),
                        TextStepPatternPart("t-2")
                )
        )

        assertThat(
                stepPattern.appendPart(
                        TextStepPatternPart("text")
                ),
                Is(equalTo(
                        StepPattern(
                                patternParts = listOf(
                                        TextStepPatternPart("t-1"),
                                        ParamStepPatternPart(name = "p-1", typeMeta = StringTypeMeta()),
                                        TextStepPatternPart("t-2text")
                                )
                        )
                ))
        )
    }

}
