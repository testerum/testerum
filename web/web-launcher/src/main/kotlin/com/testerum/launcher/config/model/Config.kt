package com.testerum.launcher.config.model

data class Config(val port: Int) {

    companion object {
        val DEFAULT = Config(
                port = 9999
        )
    }

}