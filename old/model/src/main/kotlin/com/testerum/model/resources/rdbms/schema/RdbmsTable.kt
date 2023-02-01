package com.testerum.model.resources.rdbms.schema

data class RdbmsTable (val name: String,
                       val type: RdbmsTableType,
                       val comment: String?,
                       val fields: List<RdbmsField>) {

    enum class RdbmsTableType {
        PERMANENT,
        SYSTEM_TABLE,
        GLOBAL_TEMPORARY,
        LOCAL_TEMPORARY,
        VIEW,
        ;

        companion object {
            fun decode(string: String): RdbmsTableType {
                return when {
                    "VIEW".equals(string, ignoreCase = true)             -> VIEW
                    "GLOBAL TEMPORARY".equals(string, ignoreCase = true) -> GLOBAL_TEMPORARY
                    "LOCAL TEMPORARY".equals(string, ignoreCase = true)  -> GLOBAL_TEMPORARY
                    "SYSTEM TABLE".equals(string, ignoreCase = true)     -> GLOBAL_TEMPORARY
                    else                                                 -> PERMANENT
                }
            }
        }
    }

}

