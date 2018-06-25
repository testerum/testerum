package net.qutester.model.config

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.infrastructure.path.Path

data class Setup @JsonCreator constructor(
        @JsonProperty("repositoryPath") val repositoryPath: Path
)