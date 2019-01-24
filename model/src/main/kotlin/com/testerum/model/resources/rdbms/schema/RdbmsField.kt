package com.testerum.model.resources.rdbms.schema

import com.testerum.model.enums.ParamTypeEnum

data class RdbmsField (val name: String,
                       val paramType: ParamTypeEnum,
                       val comment: String,
                       val isMandatory: Boolean)
