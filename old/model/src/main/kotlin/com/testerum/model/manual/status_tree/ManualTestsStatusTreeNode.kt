package com.testerum.model.manual.status_tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.enums.ManualTestStatus

data class ManualTestsStatusTreeNode @JsonCreator constructor(@JsonProperty("path") override val path: Path,
                                                              @JsonProperty("name") override val name: String,
                                                              @JsonProperty("status") override val status: ManualTestStatus
): ManualTestsStatusTreeBase
