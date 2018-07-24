package com.testerum.test_file_format.testdef.properties

data class FileTestDefProperties(val isManual: Boolean = false,
                                 val isDisabled: Boolean = false) {

    companion object {
        val DEFAULT = FileTestDefProperties(isManual = false, isDisabled = false)
    }

    fun isEmpty(): Boolean = !isManual && !isDisabled

}