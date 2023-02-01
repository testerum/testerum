package com.testerum.model.variable

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class ProjectVariables @JsonCreator constructor(@JsonProperty("defaultVariables") val defaultVariables: TreeMap<String, String>,
                                                     @JsonProperty("environments")     val environments: TreeMap<String, TreeMap<String, String>>) {

    companion object {
        val EMPTY = ProjectVariables(
                defaultVariables = TreeMap(),
                environments = TreeMap()
        )
    }

}
