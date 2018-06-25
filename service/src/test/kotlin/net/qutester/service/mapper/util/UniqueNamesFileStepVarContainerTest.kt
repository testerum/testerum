package net.qutester.service.mapper.util

import com.testerum.test_file_format.common.step_call.`var`.FileStepVar
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.Assert.assertThat
import org.junit.jupiter.api.Test

class UniqueNamesFileStepVarContainerTest {

    @Test
    fun test() {
        val container = UniqueNamesFileStepVarContainer().apply {
            add(FileStepVar("uniqueName", "uniqueValue"))

            add(FileStepVar("a", "a-one"))
            add(FileStepVar("a", "a-two"))

            add(FileStepVar("b", "b-one"))
            add(FileStepVar("b", "b-two"))
            add(FileStepVar("b", "b-three"))

            add(FileStepVar("c", "c-one"))
            add(FileStepVar("c", "c-two"))
            add(FileStepVar("c", "c-three"))
            add(FileStepVar("c", "c-four"))
        }

        val vars: List<FileStepVar> = container.getVars()

        assertThat(vars, hasSize(10))

        assertThat(vars[0], equalTo(FileStepVar("uniqueName", "uniqueValue")))

        assertThat(vars[1], equalTo(FileStepVar("a_1", "a-one")))
        assertThat(vars[2], equalTo(FileStepVar("a_2", "a-two")))

        assertThat(vars[3], equalTo(FileStepVar("b_1", "b-one")))
        assertThat(vars[4], equalTo(FileStepVar("b_2", "b-two")))
        assertThat(vars[5], equalTo(FileStepVar("b_3", "b-three")))

        assertThat(vars[6], equalTo(FileStepVar("c_1", "c-one")))
        assertThat(vars[7], equalTo(FileStepVar("c_2", "c-two")))
        assertThat(vars[8], equalTo(FileStepVar("c_3", "c-three")))
        assertThat(vars[9], equalTo(FileStepVar("c_4", "c-four")))
    }

}