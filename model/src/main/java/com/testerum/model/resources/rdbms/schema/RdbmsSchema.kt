package com.testerum.model.resources.rdbms.schema

data class RdbmsSchema(val name: String,
                       val tables:List<RdbmsTable>)
