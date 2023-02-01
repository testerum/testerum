package database.relational.util

import com.testerum.model.resources.rdbms.connection.RdbmsConnectionConfig

fun RdbmsConnectionConfig.prettyPrint() = buildString {
    append("driver name             : $driverName\n")
    append("driver jar              : $driverJar\n")
    append("driver class            : $driverClass\n")
    append("driver driverUrlPattern : $driverUrlPattern\n")

    host?.let {
        append("host                    : $it\n")
    }
    port?.let {
        append("port                    : $it\n")
    }

    append("use custom URL          : $useCustomUrl\n")
    if (useCustomUrl) {
        customUrl?.let {
            append("custom URL              : $it\n")
        }
    }

    user?.let {
        append("user                    : $it\n")
    }
    password?.let {
        append("password                : $it\n")
    }
    database?.let {
        append("database                : $it\n")
    }
}
