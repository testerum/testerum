package com.testerum.runner.cmdline.report_type.builder

object EventListenerProperties {

    object JsonEvents {

        /**
         * type     : java.nio.file.Path
         * required : no
         *
         * Optional property to write data to a file.
         * If missing, the data will be written to STDOUT.
         */
        const val DESTINATION_FILE_NAME = "destinationFileName"

        /**
         * type     : Boolean
         * required : no
         * default  : false
         *
         * Whether to add ``-->testerum\u0000-->`` before the JSON and
         * ``<--testerum <--`` after.
         *
         * Useful to distinguish the output of this event listener from other output.
         *
         * If missing, no prefix/postfix will be added.
         *
         */
        const val WRAP_JSON_WITH_PREFIX_AND_POSTFIX = "wrapJsonWithPrefixAndPostfix"
    }

    object JsonModel {
        const val DESTINATION_DIRECTORY = "destinationDirectory"
        const val FORMATTED = "formatted"
    }

    object JsonStats {
        const val DESTINATION_FILE_NAME = "destinationFileName"
    }

    object CustomTemplate {
        const val TEMPLATE_DIRECTORY = "templateDirectory"
    }

    object Pretty {
        const val DESTINATION_DIRECTORY = "destinationDirectory"
    }

    object RemoteServer {
        /**
         * type     : String
         * required : yes
         *
         * Required property that is an HTTP URL that points to the Reports Server.
         * If missing, an error is going to be triggered.
         */
        const val REPORT_SERVER_URL = "reportServerUrl"
    }

}
