package org.garnishtest.modules.generic.db_util.jdbc_transactions;

public final class SimpleTransactionTemplateException extends RuntimeException {

    public SimpleTransactionTemplateException() {
    }

    public SimpleTransactionTemplateException(final String message) {
        super(message);
    }

    public SimpleTransactionTemplateException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SimpleTransactionTemplateException(final Throwable cause) {
        super(cause);
    }

}
