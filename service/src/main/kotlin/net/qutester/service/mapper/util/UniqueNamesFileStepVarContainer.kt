package net.qutester.service.mapper.util

import com.testerum.test_file_format.common.step_call.`var`.FileStepVar
import javax.annotation.concurrent.NotThreadSafe

@NotThreadSafe
class UniqueNamesFileStepVarContainer {

    private val varsByName = mutableMapOf<String, MutableList<FileStepVar>>()

    fun add(variable: FileStepVar) {
        val vars: MutableList<FileStepVar> = varsByName.getOrPut(variable.name, ::ArrayList)

        vars.add(variable)
    }

    fun getVars(): List<FileStepVar> {
        val result = mutableListOf<FileStepVar>()

        for ((_: String, vars: List<FileStepVar>) in varsByName) {
            if (vars.size == 1) {
                result.add(vars.first())
            } else {
                // variables with duplicate names ==> rename them
                for ((i: Int, variable: FileStepVar) in vars.withIndex()) {
                    result.add(
                            variable.copy(name = "${variable.name}_${i + 1}")
                    )
                }
            }
        }

        return result
    }

}
