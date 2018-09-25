package com.testerum.model.infrastructure.path

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class PathTest {

    @Test
    fun `toString - full path`() {
        assertThat(
                Path(listOf("rootDir", "firstDir", "secondDir"), "fileName", "ext").toString(),
                equalTo("rootDir/firstDir/secondDir/fileName.ext")
        )
    }

    @Test
    fun `toString - only file name`() {
        val path = Path(listOf(), "fileName", "ext")

        assertThat(path.toString(), equalTo("fileName.ext"))
    }

    @Test
    fun `createInstance - empty path`() {
        assertThat(
                Path.createInstance(""),
                equalTo(Path(directories = emptyList(), fileName = null, fileExtension = null))
        )
    }

    @Test
    fun `createInstance - full path`() {
        assertThat(
                Path.createInstance("rootDir/firstDir/secondDir/fileName.ext"),
                equalTo(Path(listOf("rootDir", "firstDir", "secondDir"), "fileName", "ext"))
        )
    }

    @Test
    fun `createInstance - full path, leading slash is ignored`() {
        assertThat(
                Path.createInstance("/rootDir/firstDir/secondDir/fileName.ext"),
                equalTo(Path(listOf("rootDir", "firstDir", "secondDir"), "fileName", "ext"))
        )
    }

    @Test
    fun `createInstance - only file name`() {
        assertThat(
                Path.createInstance("fileName.ext.bat"),
                equalTo(
                        Path(emptyList(), "fileName", "ext.bat")
                )
        )
    }

    @Test
    fun `createInstance - composed extension`() {
        assertThat(
                Path.createInstance("fileName.ext.bat"),
                equalTo(
                        Path(emptyList(), "fileName", "ext.bat")
                )
        )
    }

    @Test
    fun `replaceDirs - all empty`() {
        assertThat(
                Path.createInstance("").replaceDirs(
                        oldPath = Path.createInstance(""),
                        newPath = Path.createInstance("")
                ),
                equalTo(
                        Path.createInstance("")
                )
        )
    }
    @Test
    fun `replaceDirs - not found`() {
        assertThat(
                Path(directories = listOf("a", "b", "c"), fileName = "filename", fileExtension = "extension").replaceDirs(
                        oldPath = Path.createInstance("/1/2/3"),
                        newPath = Path.createInstance("/x/y/z")
                ),
                equalTo(
                        Path(directories = listOf("a", "b", "c"), fileName = "filename", fileExtension = "extension")
                )
        )
    }

    @Test
    fun `replaceDirs - found at the beginning`() {
        assertThat(
                Path(directories = listOf("a", "b", "c", "d", "e", "f"), fileName = "filename", fileExtension = "extension").replaceDirs(
                        oldPath = Path.createInstance("/a/b/c"),
                        newPath = Path.createInstance("/1/2/3/4/5")
                ),
                equalTo(
                        Path(directories = listOf("1", "2", "3", "4", "5", "d", "e", "f"), fileName = "filename", fileExtension = "extension")
                )
        )
    }

    @Test
    fun `replaceDirs - found in the middle`() {
        assertThat(
                Path(directories = listOf("a", "b", "c", "d", "e", "f"), fileName = "filename", fileExtension = "extension").replaceDirs(
                        oldPath = Path.createInstance("/c/d"),
                        newPath = Path.createInstance("/1/2/3/4/5")
                ),
                equalTo(
                        Path(directories = listOf("a", "b", "1", "2", "3", "4", "5", "e", "f"), fileName = "filename", fileExtension = "extension")
                )
        )
    }

    @Test
    fun `replaceDirs - found at the end`() {
        assertThat(
                Path(directories = listOf("a", "b", "c", "d", "e", "f"), fileName = "filename", fileExtension = "extension").replaceDirs(
                        oldPath = Path.createInstance("/d/e/f"),
                        newPath = Path.createInstance("/1/2/3/4/5")
                ),
                equalTo(
                        Path(directories = listOf("a", "b", "c", "1", "2", "3", "4", "5"), fileName = "filename", fileExtension = "extension")
                )
        )
    }

    @Test
    fun `isChildOrSelf - empty parent`() {
        val parent = Path.createInstance("")
        val child = Path.createInstance("/a/b/c")

        assertThat(
                child.isChildOrSelf(parent),
                equalTo(true)
        )
    }

    @Test
    fun `isChildOrSelf - empty child`() {
        val parent = Path.createInstance("/a/b/c")
        val child = Path.createInstance("")

        assertThat(
                child.isChildOrSelf(parent),
                equalTo(false)
        )
    }

    @Test
    fun `isChildOrSelf - completely different`() {
        val parent = Path.createInstance("/a/b/c/d")
        val child = Path.createInstance("/e/f/g")

        assertThat(
                child.isChildOrSelf(parent),
                equalTo(false)
        )
    }

    @Test
    fun `isChildOrSelf - starts the same, ends differently`() {
        val parent = Path.createInstance("/a/b/c")
        val child = Path.createInstance("/a/b/z")

        assertThat(
                child.isChildOrSelf(parent),
                equalTo(false)
        )
    }

    @Test
    fun `isChildOrSelf - self`() {
        val parent = Path.createInstance("/a/b/c")
        val child = Path.createInstance("/a/b/c")

        assertThat(
                child.isChildOrSelf(parent),
                equalTo(true)
        )
    }

    @Test
    fun `isChildOrSelf - child`() {
        val parent = Path.createInstance("/a/b/c")
        val child = Path.createInstance("/a/b/c/d/e")

        assertThat(
                child.isChildOrSelf(parent),
                equalTo(true)
        )
    }

}
