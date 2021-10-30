package com.testerum.step_rdbms_util.scripts

import com.testerum.step_rdbms_util.jdbc_transactions.SimpleTransactionTemplate
import com.testerum.step_rdbms_util.scripts.model.RdbmsResultSet
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Types
import java.util.TreeMap
import javax.sql.DataSource

class DbQueryExecutor(dataSource: DataSource) {

    private val transactionTemplate: SimpleTransactionTemplate = SimpleTransactionTemplate(dataSource)

    fun executeStatement(sqlScriptAsString: String): RdbmsResultSet {
        return transactionTemplate.executeInTransaction { connection: Connection ->
            try {
                val statement = connection.prepareStatement(sqlScriptAsString)
                val resultSet = statement.executeQuery()
                resultSetToOutput(resultSet)
            } catch (e: Exception) {
                val indentScriptLines = sqlScriptAsString.lines().joinToString("\n", "", "", -1, "...") { line: String -> "\t\t" + line }
                throw RuntimeException("failed to execute SQL script\n$indentScriptLines", e)
            }
        }
    }

    private fun resultSetToOutput(resultSet: ResultSet): RdbmsResultSet {
        val result = mutableListOf<MutableMap<String, Any?>>()

        while (resultSet.next()) {
            val row = TreeMap<String, Any?>()
            for (i in 0 until resultSet.metaData.columnCount)  {
                val columnName = resultSet.metaData.getColumnLabel(i)
                val value = resultSet.getValueAsJavaType(i)

                row[columnName] = value
            }

            result.add(row)
        }

        return RdbmsResultSet(result)
    }
}

private fun ResultSet.getValueAsJavaType(columnIndex: Int): Any? {
    // todo: check other types supported by JDBC
    // todo: what types should we return, so that the user can use them easily
    return when (metaData.getColumnType(columnIndex)) {
        Types.CHAR        -> getString(columnIndex)
        Types.VARCHAR     -> getString(columnIndex)
        Types.LONGVARCHAR -> getString(columnIndex)
        Types.NUMERIC     -> getBigDecimal(columnIndex)
        Types.DECIMAL     -> getBigDecimal(columnIndex)
        Types.BIT         -> getOrNull { getBoolean(columnIndex) }
        Types.TINYINT     -> getOrNull { getLong(columnIndex) }
        Types.SMALLINT    -> getOrNull { getLong(columnIndex) }
        Types.INTEGER     -> getOrNull { getLong(columnIndex) }
        Types.BIGINT      -> getOrNull { getLong(columnIndex) }
        Types.REAL        -> getOrNull { getDouble(columnIndex) }
        Types.FLOAT       -> getOrNull { getDouble(columnIndex) }
        Types.DOUBLE      -> getOrNull { getDouble(columnIndex) }
        Types.DATE        -> getDate(columnIndex).toLocalDate()
        Types.TIME        -> getTime(columnIndex).time
        Types.TIMESTAMP   -> getTimestamp(columnIndex).toLocalDateTime()
        else              -> getString(columnIndex)
    }
}

private fun <R> ResultSet.getOrNull(getValue: () -> R): R? {
    val result = getValue()

    return if (wasNull()) {
         null
    } else {
         result
    }
}
