package com.testerum.common_stats

interface Aggregator<in E, out R> {

    fun aggregate(event: E)

    fun getResult(): R

}
