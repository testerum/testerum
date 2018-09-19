package com.testerum.common_kotlin

import java.util.stream.Stream

fun <T> Stream<T>.isNotEmpty(): Boolean = findAny().isPresent
