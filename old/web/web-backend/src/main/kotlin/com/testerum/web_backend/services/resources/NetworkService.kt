package com.testerum.web_backend.services.resources

import java.net.InetSocketAddress
import java.net.Socket

class NetworkService {

    fun canConnect(host: String, port: Int): Boolean {
        var socket: Socket? = null
        try {
            val address = InetSocketAddress(host, port)

            // The InetSocketAddress tries to resolve the hostname using DNS.
            // If the resolving failed, there is no point in attempting to connect.
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
