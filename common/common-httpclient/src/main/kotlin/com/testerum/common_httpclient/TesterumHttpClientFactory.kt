package com.testerum.common_httpclient

import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContexts
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext

object TesterumHttpClientFactory {

    fun createHttpClient(): CloseableHttpClient {
        val sslContext: SSLContext = SSLContexts.custom()
                .loadTrustMaterial(null) { _, _ -> true }
                .build()

        val sslConnectionSocketFactory = SSLConnectionSocketFactory(
                sslContext,
                HostnameVerifier { _, _ ->  true }
        )


        return HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .build()
    }

}
