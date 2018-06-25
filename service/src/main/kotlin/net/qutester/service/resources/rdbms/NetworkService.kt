package net.qutester.service.resources.rdbms

import com.fasterxml.jackson.databind.ObjectMapper
import java.net.InetSocketAddress
import java.net.Socket

open class NetworkService(val jsonObjectMapper: ObjectMapper) {

    fun respondsToPing(host: String, port: Int): Boolean {

        var socket: Socket? = null;

        try {
            val address = InetSocketAddress(host, port)

            if (address.isUnresolved) {
                return false;
            }

            socket = Socket()
            socket.connect(address, 3000)
        } catch (ex: Exception) {
            return false;
        } finally {
            if (socket != null) {
                socket.close()
            }
        }

        return true;
    }
}
