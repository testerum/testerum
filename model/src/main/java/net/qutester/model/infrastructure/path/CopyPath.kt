package net.qutester.model.infrastructure.path

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class CopyPath @JsonCreator constructor(
        @JsonProperty("copyPath") val copyPath: Path,
        @JsonProperty("destinationPath") val destinationPath: Path
)