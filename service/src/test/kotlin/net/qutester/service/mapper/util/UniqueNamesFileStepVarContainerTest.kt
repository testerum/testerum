package net.qutester.service.mapper.util

import com.testerum.test_file_format.common.step_call.`var`.FileStepVar
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

class UniqueNamesFileStepVarContainerTest {

    @Test
    fun test() {
        val container = UniqueNamesFileStepVarContainer().apply {
            addAndReturnNewName("uniqueName", "uniqueValue")

            addAndReturnNewName("a", "a-one")
            addAndReturnNewName("a", "a-two")

            addAndReturnNewName("b", "b-one")
            addAndReturnNewName("b", "b-two")
            addAndReturnNewName("b", "b-three")

            addAndReturnNewName("c", "c-one")
            addAndReturnNewName("c", "c-two")
            addAndReturnNewName("c", "c-three")
            addAndReturnNewName("c", "c-four")
        }

        val vars: List<FileStepVar> = container.getVars()

        assertThat(vars, hasSize(10))

        assertThat(vars[0], equalTo(FileStepVar("uniqueName", "uniqueValue")))

        assertThat(vars[1], equalTo(FileStepVar("a", "a-one")))
        assertThat(vars[2], equalTo(FileStepVar("a_2", "a-two")))

        assertThat(vars[3], equalTo(FileStepVar("b", "b-one")))
        assertThat(vars[4], equalTo(FileStepVar("b_2", "b-two")))
        assertThat(vars[5], equalTo(FileStepVar("b_3", "b-three")))

        assertThat(vars[6], equalTo(FileStepVar("c", "c-one")))
        assertThat(vars[7], equalTo(FileStepVar("c_2", "c-two")))
        assertThat(vars[8], equalTo(FileStepVar("c_3", "c-three")))
        assertThat(vars[9], equalTo(FileStepVar("c_4", "c-four")))
    }

    @Test
    fun `new name doesn't change of there is no duplicate`() {
        val container = UniqueNamesFileStepVarContainer()

        assertThat(
                container.addAndReturnNewName("a", "a-one"),
                equalTo("a")
        )
        assertThat(
                container.addAndReturnNewName("b", "b-one"),
                equalTo("b")
        )
        assertThat(
                container.addAndReturnNewName("c", "c-one"),
                equalTo("c")
        )
    }

    @Test
    fun `new name changes when there are duplicates`() {
        val container = UniqueNamesFileStepVarContainer()

        assertThat(
                container.addAndReturnNewName("a", "a-one"),
                equalTo("a")
        )
        assertThat(
                container.addAndReturnNewName("a", "a-one"),
                equalTo("a_2")
        )
        assertThat(
                container.addAndReturnNewName("a", "a-one"),
                equalTo("a_3")
        )
    }

}