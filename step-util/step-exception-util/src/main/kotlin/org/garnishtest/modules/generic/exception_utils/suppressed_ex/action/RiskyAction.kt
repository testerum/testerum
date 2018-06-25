package org.garnishtest.modules.generic.exception_utils.suppressed_ex.action

interface RiskyAction<R> {

    @Throws(Exception::class)
    fun execute(): R

}