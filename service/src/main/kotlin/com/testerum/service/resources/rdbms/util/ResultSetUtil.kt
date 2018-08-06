package com.testerum.service.resources.rdbms.util

import java.sql.ResultSet

fun ResultSet.readListOfString(): List<String> {
    val result = mutableListOf<String>()
    while (next()) {
        result.add(getString(1))
    }
    close()

    return result
}