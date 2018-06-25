package net.qutester.model.resources

import net.qutester.model.infrastructure.path.Path
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class PathTest {

    private val fieldPath = Path(listOf("rootDir", "firstDir", "secondDir"), "fileName", "ext")

    @Test
    fun testToString1() {
        assertThat(fieldPath.toString(), equalTo("rootDir/firstDir/secondDir/fileName.ext"))
    }

    @Test
    fun testToString2() {
        val path = Path(listOf(), "fileName", "ext")

        assertThat(path.toString(), equalTo("fileName.ext"))
    }

    @Test
    fun createInstance_1() {
        val path = Path.createInstance("rootDir/firstDir/secondDir/fileName.ext")

        assertThat(path, equalTo(fieldPath))
    }

    @Test
    fun createInstance_2() {
        val path = Path.createInstance("fileName.ext.bat")

        assertThat(
                path,
                equalTo(
                        Path(listOf(), "fileName", "ext.bat")
                )
        )
    }

    @Test
    fun createInstance_3() {
        val path = Path.createInstance("rootDir/firstDir/secondDir/fileName.ext.bat")

        assertThat(
                path,
                equalTo(
                        Path(listOf("rootDir", "firstDir", "secondDir"), "fileName", "ext.bat")
                )
        )
    }
}