package net.qutester.service.resources.rdbms

import com.testerum.api.test_context.settings.SettingsManager
import com.testerum.model.enums.ParamTypeEnum
import com.testerum.model.exception.ValidationException
import com.testerum.model.exception.model.ValidationModel
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.resources.rdbms.connection.RdbmsConnectionConfig
import com.testerum.model.resources.rdbms.connection.RdbmsDriver
import com.testerum.model.resources.rdbms.connection.RdbmsSchemasNames
import com.testerum.model.resources.rdbms.schema.RdbmsField
import com.testerum.model.resources.rdbms.schema.RdbmsSchema
import com.testerum.model.resources.rdbms.schema.RdbmsTable
import com.testerum.settings.SystemSettings
import net.qutester.service.resources.ResourcesService
import net.qutester.service.resources.rdbms.util.readListOfString
import net.qutester.service.resources.rdbms.util.resolveConnectionUrl
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.sql.*
import java.util.*

class RdbmsService(val settingsManager: SettingsManager,
                   private val resourcesService: ResourcesService,
                   private val rdbmsDriverConfigService: RdbmsDriverConfigService) {

    private val drivers: MutableMap<String, Driver> = mutableMapOf()

    fun getDriverInstance(rdbmsConnectionConfig: RdbmsConnectionConfig): Driver {
        val driverKey = rdbmsConnectionConfig.driverJar
        val driver = drivers[driverKey]
        if (driver != null) {
            return driver
        }

        val jdbcDriverDirectory = settingsManager.getSettingValue(SystemSettings.JDBC_DRIVERS_DIRECTORY)

        val driverUrl = URL("file:" + jdbcDriverDirectory + File.separator + rdbmsConnectionConfig.driverJar)
        val driverJarClassLoader = URLClassLoader(arrayOf(driverUrl), this.javaClass.classLoader)
        val driverClass = Class.forName(rdbmsConnectionConfig.driverClass, true, driverJarClassLoader)
        val driverInstance: Driver = driverClass.newInstance() as Driver
        drivers[driverKey] = driverInstance

        return driverInstance
    }

    fun getSchemas(rdbmsConnectionConfig: RdbmsConnectionConfig): RdbmsSchemasNames {
        return try {
            val dbConnectionWithoutDatabase = rdbmsConnectionConfig.copy(database = null)

            val driverConfig: RdbmsDriver? = getDriverConfigByDriverName(rdbmsConnectionConfig.driverName)

            if (driverConfig?.listSchemasQuery != null) {
                readSchemasFromQuery(dbConnectionWithoutDatabase, driverConfig.listSchemasQuery!!)
            } else {
                readSchemasFromMetaData(dbConnectionWithoutDatabase)
            }
        } catch (e: Exception) {
            RdbmsSchemasNames(errorMessage = "" + e.message)
        }
    }

    private fun readSchemasFromQuery(rdbmsConnectionConfig: RdbmsConnectionConfig, listSchemasQuery: String): RdbmsSchemasNames {
        val schemas: List<String> = executeQuery(rdbmsConnectionConfig, listSchemasQuery) { resultSet ->
            resultSet.readListOfString()
        }

        return RdbmsSchemasNames(
                schemas.sorted()
        )
    }

    private fun <T> executeQuery(rdbmsConnectionConfig: RdbmsConnectionConfig, query: String, resultSetExtractor: (resultSet: ResultSet) -> T): T {
        var connection: Connection? = null
        var stmt: Statement? = null

        try {
            connection = getConnection(rdbmsConnectionConfig)

            stmt = connection.createStatement()
            val resultSet = stmt.executeQuery(query)
            return resultSetExtractor.invoke(resultSet)
        } finally {
            stmt?.close()
            connection?.close()
        }
    }

    private fun readSchemasFromMetaData(rdbmsConnectionConfig: RdbmsConnectionConfig): RdbmsSchemasNames {
        var connection: Connection? = null

        try {
            connection = getConnection(rdbmsConnectionConfig)

            val schemas = connection.metaData.schemas.readListOfString()
            val catalogs = connection.metaData.catalogs.readListOfString()

            val databases = schemas + catalogs
            return RdbmsSchemasNames(schemas = databases.filter { it.isNotBlank() }.sorted())

        } finally {
            connection?.close()
        }
    }

    private fun getConnection(rdbmsConnectionConfig: RdbmsConnectionConfig): Connection {
        val driver: Driver = getDriverInstance(rdbmsConnectionConfig)
        val properties = Properties().apply {
            if (rdbmsConnectionConfig.user != null) {
                this["user"] = rdbmsConnectionConfig.user
            }
            if (rdbmsConnectionConfig.password != null) {
                this["password"] = rdbmsConnectionConfig.password
            }
        }

        return driver.connect(rdbmsConnectionConfig.resolveConnectionUrl(), properties)
    }

    private fun getDriverConfigByDriverName(driverName: String): RdbmsDriver? {
        return rdbmsDriverConfigService.getDriversConfiguration().find { it.name == driverName }
    }

    fun getSchema(resourcePath: Path): RdbmsSchema {
        val connectionConfig = resourcesService.getResourceBodyAs(resourcePath, RdbmsConnectionConfig::class.java)

       return getSchema(connectionConfig)
    }

    private fun getSchema(rdbmsConnectionConfig: RdbmsConnectionConfig): RdbmsSchema {
        val connection: Connection
        try {
            connection = getConnection(rdbmsConnectionConfig)
        } catch (e: Exception) {
            var configAsString = rdbmsConnectionConfig.toString()
            configAsString = configAsString.replace(", ", "\n\t")
            configAsString = configAsString.substringAfter("(").substringBefore(")")

            throw ValidationException(
                    ValidationModel(
                            globalValidationMessage = "The database connection couldn't be established using the provided details",
                            globalValidationMessageDetails = "RDBMS Connection Config = [\n\t$configAsString\n]"
                    )
            )
        }

        return RdbmsSchema(
                rdbmsConnectionConfig.database ?: "",
                getRdbmsTables(connection, rdbmsConnectionConfig)
        )
    }

    private fun getRdbmsTables(connection: Connection, rdbmsConnectionConfig: RdbmsConnectionConfig): List<RdbmsTable> {
        val result: MutableList<RdbmsTable> = mutableListOf()

        val tablesResultSet: ResultSet = connection.metaData.getTables(
                rdbmsConnectionConfig.database,
                rdbmsConnectionConfig.database,
                null,
                arrayOf("TABLE", "VIEW")
        )

        tablesResultSet.use { 
            while (tablesResultSet.next()) {
                val tableName = tablesResultSet.getString(3)
                if (tableName == null || tableName.startsWith("BIN$")) {
                    // FOR ORACLE IGNORE RECYCLE BIN BIN$ TABLES
                    continue
                }
                val table = RdbmsTable(
                        name = tableName,
                        type = RdbmsTable.RdbmsTableType.decode(tablesResultSet.getString(4)),
                        comment = tablesResultSet.getString(5),
                        fields = getRdbmsTableFields(connection, rdbmsConnectionConfig, tableName)
                )
                result.add(
                        table
                )
            }
        }

        return result
    }

    private fun getRdbmsTableFields(connection: Connection,
                                    rdbmsConnectionConfig: RdbmsConnectionConfig,
                                    tableName: String): List<RdbmsField> {
        val result = mutableListOf<RdbmsField>()
        val columnsResultSet: ResultSet = connection.metaData.getColumns(
                rdbmsConnectionConfig.database,
                rdbmsConnectionConfig.database,
                tableName,
                null
        )

        while (columnsResultSet.next()) {
            val intoTableName = columnsResultSet.getString(3)

            if (intoTableName != null) {
                val columnName = columnsResultSet.getString(4)
                val paramType: ParamTypeEnum = getColumnType(columnsResultSet)
                val comment = columnsResultSet.getString(12)
                val isMandatory = DatabaseMetaData.columnNoNulls == columnsResultSet.getInt(11)

                val field = RdbmsField(
                        name = columnName,
                        paramType = paramType,
                        comment = comment,
                        isMandatory = isMandatory
                )
                result.add(
                        field
                )
            }
        }

        return result
    }

    private fun getColumnType(columnsResultSet: ResultSet): ParamTypeEnum {
        val jdbcColumnType = columnsResultSet.getInt(5)

        when (jdbcColumnType) {
            Types.CHAR,
            Types.VARCHAR,
            Types.LONGNVARCHAR -> return ParamTypeEnum.TEXT

            Types.NUMERIC,
            Types.DECIMAL,
            Types.TINYINT,
            Types.SMALLINT,
            Types.INTEGER,
            Types.BIGINT,
            Types.REAL,
            Types.FLOAT,
            Types.DOUBLE -> return ParamTypeEnum.NUMBER

            Types.BIT -> return ParamTypeEnum.BOOLEAN

            Types.BINARY,
            Types.CLOB,
            Types.BLOB,
            Types.VARBINARY,
            Types.LONGVARBINARY -> return ParamTypeEnum.BINARY

            Types.DATE -> return ParamTypeEnum.DATE
            Types.TIME -> return ParamTypeEnum.TIME
            Types.TIMESTAMP -> return ParamTypeEnum.TIME

            else -> return ParamTypeEnum.UNKNOWN
        }
    }
}