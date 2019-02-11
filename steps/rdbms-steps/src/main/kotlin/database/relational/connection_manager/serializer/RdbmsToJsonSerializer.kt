package database.relational.connection_manager.serializer

import com.fasterxml.jackson.databind.ObjectMapper
import database.relational.connection_manager.model.RdbmsConnection
import database.relational.connection_manager.serializer.model.RdbmsDataField
import database.relational.connection_manager.serializer.model.RdbmsDataSchema
import database.relational.connection_manager.serializer.model.RdbmsDataTable
import database.relational.connection_manager.serializer.model.RdbmsDataTableRow
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

object RdbmsToJsonSerializer {

     private val OBJECT_MAPPER = ObjectMapper()

    fun serializeSchemaAsJsonString(rdbmsConnection: RdbmsConnection): String {
        val rdbmsDataSchema = getRdbmsDataSchema(rdbmsConnection)

        val mappedModel = mapSchema(rdbmsDataSchema)

        return OBJECT_MAPPER.writeValueAsString(mappedModel)
    }

    private fun mapSchema(schema: RdbmsDataSchema): Map<String, List<Map<String, String?>>> {
        val result = mutableMapOf<String, List<Map<String, String?>>>()

        for (table in schema.tables) {
            result[table.tableName] = mapRows(table)
        }

        return result
    }

    private fun mapRows(table: RdbmsDataTable): MutableList<Map<String, String?>> {
        val mappedRows = mutableListOf<Map<String, String?>>()

        for (row in table.rows) {
            mappedRows.add(
                    mapRow(row)
            )
        }
        return mappedRows
    }

    private fun mapRow(row: RdbmsDataTableRow): Map<String, String?> {
        val mappedRow = mutableMapOf<String, String?>()

        for (column in row.dataFields) {
            mappedRow[column.fieldName] = column.fieldValue
        }

        return mappedRow
    }

    private fun getRdbmsDataSchema(rdbmsConnection: RdbmsConnection): RdbmsDataSchema {
        val openJdbcConnection = rdbmsConnection.openJdbcConnection()

        val tables: MutableList<RdbmsDataTable>
        try {
            tables = getRdbmsDataTables(openJdbcConnection, rdbmsConnection)
        } finally {
            openJdbcConnection.close()
        }

        return RdbmsDataSchema(tables)
    }

    private fun getRdbmsDataTables(jdbcConnection: Connection, rdbmsConnection: RdbmsConnection): MutableList<RdbmsDataTable> {
        val tablesName: MutableList<String> = mutableListOf()

        val tablesResultSet: ResultSet = jdbcConnection.metaData.getTables(
                rdbmsConnection.rdbmsConnectionConfig.database,
                rdbmsConnection.rdbmsConnectionConfig.database,
                null,
                arrayOf("TABLE", "VIEW"))

        try {
            while (tablesResultSet.next()) {
                val tableName = tablesResultSet.getString(3)
                if (tableName == null || tableName.startsWith("BIN$")) {
                    // FOR ORACLE IGNORE RECYCLE BIN BIN$ TABLES
                } else {
                    tablesName.add(
                            tableName
                    )
                }
            }
        } finally {
            tablesResultSet.close()
        }

        val result: MutableList<RdbmsDataTable> = mutableListOf()
        for (tableName in tablesName) {
            result.add(
                    getRdbmsDataTable(tableName, jdbcConnection)
            )
        }
        return result
    }

    private fun getRdbmsDataTable(tableName: String, jdbcConnection: Connection): RdbmsDataTable {
        val rows = mutableListOf<RdbmsDataTableRow>()

        var createStatement: Statement? = null
        var rs: ResultSet? = null
        try {
            createStatement = jdbcConnection.createStatement()
            rs = createStatement.executeQuery(
                    "SELECT * FROM ${tableName}"
            )

            while (rs.next()) {
                rows.add(
                        getRdbmsDataRow(rs)
                )
            }
        } finally {
            rs?.close()
            createStatement?.close()
        }



        return RdbmsDataTable(tableName, rows)
    }

    private fun getRdbmsDataRow(rs: ResultSet): RdbmsDataTableRow {
        val rowDataFields = mutableListOf<RdbmsDataField>()
        for (columnIndex in 1 .. rs.metaData.columnCount) {
            val columnName = rs.metaData.getColumnName(columnIndex)
            val fieldValue = rs.getString(columnIndex)

            rowDataFields.add(
                    RdbmsDataField(columnName, fieldValue)
            )
        }

        return RdbmsDataTableRow(rowDataFields)
    }
}
