package database.relational.connection_manager.serializer.model

data class RdbmsDataSchema constructor(
        val tables: List<RdbmsDataTable>
)