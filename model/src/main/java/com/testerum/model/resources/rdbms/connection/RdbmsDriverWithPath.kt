package com.testerum.model.resources.rdbms.connection

import java.nio.file.Path as JavaPath

data class RdbmsDriverWithPath(val rdbmsDriver: RdbmsDriver,
                               val path: JavaPath)
