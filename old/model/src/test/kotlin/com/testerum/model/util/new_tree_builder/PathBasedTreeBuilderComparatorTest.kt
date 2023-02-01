package com.testerum.model.util.new_tree_builder

import com.testerum.model.infrastructure.path.HasPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.util.new_tree_builder.PathBasedTreeBuilder.Companion.NODES_COMPARATOR
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PathBasedTreeBuilderComparatorTest {

    @Test
    fun `should put root before directory`() {
        assertThat(
            sortItemsList(
                "",
                "dir",
            )
        ).isEqualTo(
            listOf(
                "",
                "dir",
            )
        )
    }

    @Test
    fun `should put root before file`() {
        assertThat(
            sortItemsList(
                "",
                "file.test",
            )
        ).isEqualTo(
            listOf(
                "",
                "file.test",
            )
        )
    }

    @Test
    fun `should sort directories at the same level by name`() {
        assertThat(
            sortItemsList(
                "dir-2",
                "dir-1",
            )
        ).isEqualTo(
            listOf(
                "dir-1",
                "dir-2",
            )
        )
    }

    @Test
    fun `should sort files at the same level by name`() {
        assertThat(
            sortItemsList(
                "file-2.test",
                "file-1.test",
            )
        ).isEqualTo(
            listOf(
                "file-1.test",
                "file-2.test",
            )
        )
    }

    @Test
    fun `folder should have priority over test`() {
        assertThat(
            sortItemsList(
                "Check SuperMan.test",
                "MyFolder/Wiremock.test",
            )
        ).isEqualTo(
            listOf(
                "MyFolder/Wiremock.test",
                "Check SuperMan.test",
            )
        )
    }

    @Test
    fun `should sort directories and files at the same level by name, directories first`() {
        assertThat(
            sortItemsList(
                "file-2.test",
                "file-1.test",
                "dir-2",
                "dir-1",
            )
        ).isEqualTo(
            listOf(
                "dir-1",
                "dir-2",
                "file-1.test",
                "file-2.test",
            )
        )
    }

    @Test
    fun `should sort shortest first, if it's a directory`() {
        assertThat(
            sortItemsList(
                "a/b/x/y/z",
                "a/b/c",
            )
        ).isEqualTo(
            listOf(
                "a/b/c",
                "a/b/x/y/z",
            )
        )
    }


    @Test
    fun `should sort longest first, if it's a file`() {
        assertThat(
            sortItemsList(
                "a/b/c.test",
                "a/b/x/y/z",
            )
        ).isEqualTo(
            listOf(
                "a/b/x/y/z",
                "a/b/c.test",
            )
        )
    }

    @Test
    fun `should sort shortest subsequence first`() {
        assertThat(
            sortItemsList(
                "a/b/c/d",
                "a/b/c",
            )
        ).isEqualTo(
            listOf(
                "a/b/c",
                "a/b/c/d",
            )
        )
    }

    @Test
    fun `empty path should be first compared with a dir`() {
        assertThat(
            sortItemsList(
                "a",
                "",
            )
        ).isEqualTo(
            listOf(
                "",
                "a",
            )
        )
    }

    @Test
    fun `empty path should be first compared with a test`() {
        assertThat(
            sortItemsList(
                "",
                "owners/create/ui/t.test",
            )
        ).isEqualTo(
            listOf(
                "",
                "owners/create/ui/t.test",
            )
        )
    }

    @Test
    fun `empty path should be first compared with a test 1`() {
        assertThat(
            sortItemsList(
                "owners/create/ui/t.test",
                "",
            )
        ).isEqualTo(
            listOf(
                "",
                "owners/create/ui/t.test",
            )
        )
    }

    @Test
    fun `complex case`() {
        assertThat(
            sortItemsList(
                "",
                "owners/create/ui/t.test",
                "owners/create/rest",
                "root-test.text",
                "owners/create/ui/t add.test",
                "owners",
                "owners/create/ui/t zoo.test",
                "owners/create",
            )
        ).isEqualTo(
            listOf(
                "",
                "owners",
                "owners/create",
                "owners/create/rest",
                "owners/create/ui/t.test",
                "owners/create/ui/t add.test",
                "owners/create/ui/t zoo.test",
                "root-test.text",
            )
        )
    }

    private fun sortItemsList(vararg itemPaths: String): List<String> {
        return itemList(*itemPaths)
            .sortedWith(NODES_COMPARATOR)
            .map { it.path.toString() }
    }

    private fun itemList(vararg texts: String): List<HasPath> = texts.map { item(it) }

    private fun item(text: String): HasPath = ItemWithPath(Path.createInstance(text))

    private data class ItemWithPath(override val path: Path) : HasPath

}
