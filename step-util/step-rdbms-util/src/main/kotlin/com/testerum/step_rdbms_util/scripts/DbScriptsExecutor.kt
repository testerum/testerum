package com.testerum.step_rdbms_util.scripts

import com.testerum.step_rdbms_util.jdbc_transactions.SimpleTransactionTemplate
import org.flywaydb.core.internal.dbsupport.DbSupport
import org.flywaydb.core.internal.dbsupport.DbSupportFactory
import org.flywaydb.core.internal.dbsupport.JdbcTemplate
import org.flywaydb.core.internal.dbsupport.SqlScript
import java.sql.Connection
import java.sql.Types
import javax.sql.DataSource

class DbScriptsExecutor(dataSource: DataSource) {

    private val transactionTemplate: SimpleTransactionTemplate = SimpleTransactionTemplate(dataSource)

    fun executeScript(sqlScriptAsString: String) {
        transactionTemplate.executeInTransaction { connection: Connection ->
            try {
                val dbSupport = createDbSupport(connection)
                val sqlScript = SqlScript(sqlScriptAsString, dbSupport)
                sqlScript.execute(JdbcTemplate(connection, Types.NULL))
            } catch (e: Exception) {
                val indentScriptLines = sqlScriptAsString.lines().joinToString("\n", "", "", -1, "...") { line: String -> "\t\t" + line }
                throw RuntimeException("failed to execute SQL script\n$indentScriptLines", e)
            }
        }
    }


    private fun createDbSupport(connection: Connection): DbSupport {
        val printInfo = true
        return DbSupportFactory.createDbSupport(connection, printInfo)
    }

}
