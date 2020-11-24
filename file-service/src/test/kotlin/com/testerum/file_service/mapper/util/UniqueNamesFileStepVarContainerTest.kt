package com.testerum.file_service.mapper.util

import com.testerum.test_file_format.common.step_call.`var`.FileStepVar
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UniqueNamesFileStepVarContainerTest {

    @Test
    fun `creates variables without duplicates`() {
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

        assertThat(vars).hasSize(10)

        assertThat(vars[0]).isEqualTo(FileStepVar("uniqueName", "uniqueValue"))

        assertThat(vars[1]).isEqualTo(FileStepVar("a", "a-one"))
        assertThat(vars[2]).isEqualTo(FileStepVar("a_2", "a-two"))

        assertThat(vars[3]).isEqualTo(FileStepVar("b", "b-one"))
        assertThat(vars[4]).isEqualTo(FileStepVar("b_2", "b-two"))
        assertThat(vars[5]).isEqualTo(FileStepVar("b_3", "b-three"))

        assertThat(vars[6]).isEqualTo(FileStepVar("c", "c-one"))
        assertThat(vars[7]).isEqualTo(FileStepVar("c_2", "c-two"))
        assertThat(vars[8]).isEqualTo(FileStepVar("c_3", "c-three"))
        assertThat(vars[9]).isEqualTo(FileStepVar("c_4", "c-four"))
    }

    @Test
    fun `new name doesn't change of there is no duplicate`() {
        val container = UniqueNamesFileStepVarContainer()

        assertThat(container.addAndReturnNewName("a", "a-one"))
            .isEqualTo("a")
        assertThat(container.addAndReturnNewName("b", "b-one"))
            .isEqualTo("b")
        assertThat(container.addAndReturnNewName("c", "c-one"))
            .isEqualTo("c")
    }

    @Test
    fun `new name changes when there are duplicates`() {
        val container = UniqueNamesFileStepVarContainer()

        assertThat(container.addAndReturnNewName("a", "a-one"))
            .isEqualTo("a")
        assertThat(container.addAndReturnNewName("a", "a-one"))
            .isEqualTo("a_2")
        assertThat(container.addAndReturnNewName("a", "a-one"))
            .isEqualTo("a_3")
    }

}
