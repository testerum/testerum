package com.testerum.service.resources.rdbms

import java.net.InetSocketAddress
import java.net.Socket

open class NetworkService {

    fun respondsToPing(host: String, port: Int): Boolean {
        var socket: Socket? = null
        try {
            val address = InetSocketAddress(host, port)

            if (address.isUnresolved) {
                return false
            }

            socket = Socket()
            socket.connect(address, 3000)
        } catch (ex: Exception) {
            return false
        } finally {
            socket?.close()
        }

        return true
    }

}
