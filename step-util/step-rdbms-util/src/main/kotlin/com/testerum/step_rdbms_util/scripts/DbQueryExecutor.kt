package com.testerum.step_rdbms_util.scripts

import com.testerum.step_rdbms_util.jdbc_transactions.SimpleTransactionTemplate
import com.testerum.step_rdbms_util.scripts.model.RdbmsResultSet
import java.sql.Connection
import java.sql.ResultSet
import java.util.TreeMap
import javax.sql.DataSource

class DbQueryExecutor(dataSource: DataSource) {

    private val transactionTemplate: SimpleTransactionTemplate = SimpleTransactionTemplate(dataSource)

    fun executeStatement(sqlScriptAsString: String): RdbmsResultSet {
        var rdbmsResultSet: RdbmsResultSet? = null
        transactionTemplate.executeInTransaction { connection: Connection ->
            try {
                val statement = connection.prepareStatement(sqlScriptAsString)
                val resultSet = statement.executeQuery()
                rdbmsResultSet = resultSetToOutput(resultSet)
            } catch (e: Exception) {
                val indentScriptLines = sqlScriptAsString.lines().joinToString("\n", "", "", -1, "...") { line: String -> "\t\t" + line }
                throw RuntimeException("failed to execute SQL script\n$indentScriptLines", e)
            }
        }
        return rdbmsResultSet ?: throw RuntimeException("this should not happen")
    }

    private fun resultSetToOutput(resultSet: ResultSet): RdbmsResultSet {
        val result = mutableListOf<MutableMap<String, Any?>>()

        while (resultSet.next()) {
            val row = TreeMap<String, Any?>()
            for (i in 0 until resultSet.metaData.columnCount)  {
                val columnName = resultSet.metaData.getColumnLabel(i)
                val value = resultSet.getValueAsJavaType(i)
                row.put(columnName, value)
            }

            result.add(row)
        }

        return RdbmsResultSet(result)
    }
}

private fun ResultSet.getValueAsJavaType(columnIndex: Int): Any? {
    val columnType = this.metaData.getColumnTypeName(columnIndex)
    return when (columnType) {
        "java.sql.Types.CHAR"        -> getString(columnIndex)
        "java.sql.Types.VARCHAR"     -> getString(columnIndex)
        "java.sql.Types.LONGVARCHAR" -> getString(columnIndex)
        "java.sql.Types.NUMERIC"     -> getBigDecimal(columnIndex)
        "java.sql.Types.DECIMAL"     -> getBigDecimal(columnIndex)
        "java.sql.Types.BIT"         -> getBoolean(columnIndex)
        "java.sql.Types.TINYINT"     -> getLong(columnIndex)
        "java.sql.Types.SMALLINT"    -> getLong(columnIndex)
        "java.sql.Types.INTEGER"     -> getLong(columnIndex)
        "java.sql.Types.BIGINT"      -> getLong(columnIndex)
        "java.sql.Types.REAL"        -> getDouble(columnIndex)
        "java.sql.Types.FLOAT"       -> getDouble(columnIndex)
        "java.sql.Types.DOUBLE"      -> getDouble(columnIndex)
        "java.sql.Types.DATE"        -> getDate(columnIndex)
        "java.sql.Types.TIME"        -> getDate(columnIndex)
        "java.sql.Types.TIMESTAMP"   -> getDate(columnIndex)
        else -> getString(columnIndex)
    }
}
