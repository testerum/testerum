package database.relational.connection_manager.model

import java.io.PrintWriter
import java.sql.Connection
import java.sql.Driver
import java.util.*
import java.util.logging.Logger
import javax.sql.DataSource

class SingleConnectionDataSource(private val driver: Driver,
                                 private val url: String,
                                 private val username: String?,
                                 private val password: String?) : DataSource {

    override fun getConnection(): Connection = getConnection(username, password)

    override fun getConnection(username: String?, password: String?): Connection {
        val properties = Properties().apply {
            username?.let { this["user"] = it }
            password?.let { this["password"] = it }
        }

        return driver.connect(url, properties)
    }

    override fun setLogWriter(out: PrintWriter?) = throw UnsupportedOperationException("not implemented")

    override fun getParentLogger(): Logger = throw UnsupportedOperationException("not implemented")

    override fun setLoginTimeout(seconds: Int) = throw UnsupportedOperationException("not implemented")

    override fun isWrapperFor(iface: Class<*>?): Boolean = throw UnsupportedOperationException("not implemented")

    override fun getLogWriter(): PrintWriter = throw UnsupportedOperationException("not implemented")

    override fun <T : Any?> unwrap(iface: Class<T>?): T = throw UnsupportedOperationException("not implemented")

    override fun getLoginTimeout(): Int = throw UnsupportedOperationException("not implemented")

}