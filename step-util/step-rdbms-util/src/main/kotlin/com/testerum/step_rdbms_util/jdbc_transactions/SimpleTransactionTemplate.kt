package com.testerum.step_rdbms_util.jdbc_transactions

import com.testerum.step_rdbms_util.jdbc_transactions.exec.ExceptionTrackingExecutor
import java.sql.Connection
import javax.sql.DataSource

class SimpleTransactionTemplate(private val dataSource: DataSource) {

    fun executeInTransaction(action: (Connection) -> Unit) {
        try {
            tryToExecuteInTransaction(action)
        } catch (e: Exception) {
            throw RuntimeException("failed to execute in transaction", e)
        }
    }

    private fun tryToExecuteInTransaction(action: (Connection) -> Unit) {
        val connection = acquireDatabaseConnection()
        val executor = ExceptionTrackingExecutor()

        val databaseAction = { action(connection) }
        val commit = { connection.commit() }
        val rollback = { connection.rollback() }
        val close = { connection.close() }

        val dbActionResult = executor.execute(databaseAction)
        if (dbActionResult.succeeded()) {
            executor.execute(commit)
        } else {
            executor.execute(rollback)
        }
        executor.execute(close)
        executor.throwIfNeeded()
    }

    private fun acquireDatabaseConnection(): Connection {
        return try {
            val connection = dataSource.connection
            connection.autoCommit = false
            connection
        } catch (e: Exception) {
            throw RuntimeException("failed acquire database transaction from data source", e)
        }
    }

}
