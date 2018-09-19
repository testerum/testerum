package com.testerum.file_service.mapper.util

import com.testerum.test_file_format.common.step_call.`var`.FileStepVar
import javax.annotation.concurrent.NotThreadSafe

@NotThreadSafe
class UniqueNamesFileStepVarContainer {

    private val countByOldName = mutableMapOf<String, Int>()
    private val vars = mutableListOf<FileStepVar>()

    fun addAndReturnNewName(varName: String, varValue: String): String {
        val oldNameCount: Int = countByOldName[varName] ?: 0

        val newName: String = if (oldNameCount == 0) {
            varName
        } else {
            "${varName}_${oldNameCount + 1}"
        }

        countByOldName[varName] = oldNameCount + 1

        vars += FileStepVar(newName, varValue)

        return newName
    }

    fun getVars(): List<FileStepVar> = vars

}
