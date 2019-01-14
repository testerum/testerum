package com.testerum.common_stats

interface Reducible<T> where T : Reducible<T>,
                             T : Any {

    fun reduce(other: T): T

}
