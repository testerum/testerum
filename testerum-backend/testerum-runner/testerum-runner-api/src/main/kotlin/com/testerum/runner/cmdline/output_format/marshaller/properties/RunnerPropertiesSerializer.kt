package com.testerum.runner.cmdline.output_format.marshaller.properties

object RunnerPropertiesSerializer {

    fun serialize(properties: Map<String, String>): String {
        val result = StringBuilder()

        var first = true
        for ((key, value) in properties) {
            if (first) {
                first = false
            } else {
                result.append(",")
            }

            result.append(key.escape())

            if (value.isNotEmpty()) {
                result.append("=")
                result.append(value.escape())
            }
        }


        return result.toString()
    }

    private fun String.escape(): String {
        return this.replace("=", "\\=")
                .replace(",", "\\,")
    }

}
