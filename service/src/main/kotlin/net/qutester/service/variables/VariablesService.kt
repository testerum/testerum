package net.qutester.service.variables

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.repository.enums.FileType
import net.qutester.model.variable.Variable
import net.testerum.db_file.FileRepositoryService
import net.testerum.db_file.model.KnownPath
import net.testerum.db_file.model.RepositoryFile
import net.testerum.db_file.model.RepositoryFileChange

class VariablesService(val fileRepositoryService: FileRepositoryService,
                       val jsonObjectMapper: ObjectMapper) {

    companion object {
        private val VARIABLES_FILE_PATH: Path = Path.createInstance("variables.json")
    }

    fun save(variables: List<Variable>): List<Variable> {

        val varMap = variables.map { it.key to it.value }.toMap()

        fileRepositoryService.delete(KnownPath(
                VARIABLES_FILE_PATH,
                FileType.VARIABLES
        ))
        fileRepositoryService.create(
                RepositoryFileChange(
                        null,
                        RepositoryFile(
                                KnownPath(
                                        VARIABLES_FILE_PATH,
                                        FileType.VARIABLES
                                ),
                                jsonObjectMapper.writeValueAsString(varMap)
                        )
                )
        )
        return variables
    }

    fun getVariables(): List<Variable> {
        val variablesAsMap = getVariablesAsMap()

        return variablesAsMap.map { Variable(it.key, it.value) }
    }

    fun getVariablesAsMap(): Map<String, String> {
        val variablesRepositoryFile = fileRepositoryService.getByPath(KnownPath(
                VARIABLES_FILE_PATH,
                FileType.VARIABLES
        ))?: return emptyMap()

        val variablesAsString = variablesRepositoryFile.body

        return jsonObjectMapper.readValue(variablesAsString)
    }
}
