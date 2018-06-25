package net.qutester.model.resources.rdbms.schema

data class RdbmsSchema(val name: String,
                       val tables:List<RdbmsTable>) {
}