package org.garnishtest.modules.generic.exception_utils.suppressed_ex.action

abstract class NoResultRiskyAction : RiskyAction<Void?> {

    @Throws(Exception::class)
    protected abstract fun executeWithoutResult()

    @Throws(Exception::class)
    override fun execute(): Void? {
        executeWithoutResult()

        return null
    }

}
