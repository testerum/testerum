package database.relational.connection_manager.serializer.model

data class RdbmsDataTableRow constructor(
        val dataFields: List<RdbmsDataField>
)