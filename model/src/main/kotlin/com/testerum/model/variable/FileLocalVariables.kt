package com.testerum.model.variable

import java.util.*

data class FileLocalVariables(val projectLocalVariables: TreeMap<String, FileProjectLocalVariables>) {

    companion object {
        val EMPTY = FileLocalVariables(TreeMap())
    }

    fun getFileProjectLocalVariables(projectId: String): FileProjectLocalVariables? = projectLocalVariables[projectId]

}
