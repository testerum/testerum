package net.qutester.model.resources.rdbms.schema

import net.qutester.model.enums.ParamTypeEnum

data class RdbmsField (val name: String,
                       val paramType: ParamTypeEnum,
                       val comment: String,
                       val isMandatory: Boolean){
}