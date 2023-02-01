package com.testerum.common.parsing

import org.jparsec.Parser

interface ParserFactory<T> {

    fun createParser(): Parser<T>

}