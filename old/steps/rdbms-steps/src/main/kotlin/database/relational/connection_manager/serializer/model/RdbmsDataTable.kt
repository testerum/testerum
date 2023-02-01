package database.relational.connection_manager.serializer.model

data class RdbmsDataTable constructor(
        val tableName: String,
        val rows: List<RdbmsDataTableRow> = listOf())