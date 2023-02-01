package com.testerum.common_kotlin

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.charset.Charset

fun contentOfClasspathResourceAt(
    classpathLocation: String,
    charset: Charset = Charsets.UTF_8
): String {
    val classLoader = Thread.currentThread().contextClassLoader

    val resource: InputStream = classLoader.getResourceAsStream(classpathLocation)
        ?: throw IllegalArgumentException("the classpath resource at [$classpathLocation] cannot be found")

    val output = ByteArrayOutputStream()
    resource.use {
        resource.copyTo(output)
    }

    return String(
        output.toByteArray(),
        charset
    )
}
