package net.qutester.model.resources

import net.qutester.model.infrastructure.path.Path
import org.assertj.core.api.Assertions
import org.junit.Test

class PathTest {
    val fieldPath = Path(listOf("rootDir", "firstDir", "secondDir"), "fileName", "ext")

    @Test
    public fun testToString1() {
        Assertions.assertThat(fieldPath.toString()).isEqualTo("rootDir/firstDir/secondDir/fileName.ext")
    }

    @Test
    public fun testToString2() {
        val path = Path(listOf(), "fileName", "ext")
        Assertions.assertThat(path.toString()).isEqualTo("fileName.ext")
    }

    @Test
    public fun createInstance_1() {
        val path = Path.createInstance("rootDir/firstDir/secondDir/fileName.ext")
        Assertions.assertThat(path).isEqualTo(fieldPath)
    }

    @Test
    public fun createInstance_2() {
        val path = Path.createInstance("fileName.ext.bat")
        Assertions.assertThat(path)
                .isEqualTo(
                        Path(listOf(), "fileName", "ext.bat")
                )
    }

    @Test
    public fun createInstance_3() {
        val path = Path.createInstance("rootDir/firstDir/secondDir/fileName.ext.bat")
        Assertions.assertThat(path)
                .isEqualTo(
                        Path(listOf("rootDir", "firstDir", "secondDir"), "fileName", "ext.bat")
                )
    }
}