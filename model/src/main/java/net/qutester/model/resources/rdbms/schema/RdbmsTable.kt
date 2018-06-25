package net.qutester.model.resources.rdbms.schema

data class RdbmsTable (val name: String,
                       val type: RdbmsTableType,
                       val comment: String?,
                       val fields: List<RdbmsField>) {

    /**
     * The type of the table in database
     */
    enum class RdbmsTableType {
        PERMANENT,
        SYSTEM_TABLE,
        GLOBAL_TEMPORARY,
        LOCAL_TEMPORARY,
        VIEW;


        companion object {

            fun decode(string: String): RdbmsTableType {
                if ("VIEW".equals(string, ignoreCase = true)) return VIEW
                if ("GLOBAL TEMPORARY".equals(string, ignoreCase = true)) return GLOBAL_TEMPORARY
                if ("LOCAL TEMPORARY".equals(string, ignoreCase = true)) return GLOBAL_TEMPORARY
                if ("SYSTEM TABLE".equals(string, ignoreCase = true)) GLOBAL_TEMPORARY
                return PERMANENT
            }
        }
    }

}

