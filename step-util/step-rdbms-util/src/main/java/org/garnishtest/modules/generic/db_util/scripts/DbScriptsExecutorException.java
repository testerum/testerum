package org.garnishtest.modules.generic.db_util.scripts;

public class DbScriptsExecutorException extends RuntimeException {

    public DbScriptsExecutorException() { }

    public DbScriptsExecutorException(final String message) {
        super(message);
    }

    public DbScriptsExecutorException(final Throwable cause) {
        super(cause);
    }

    public DbScriptsExecutorException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
