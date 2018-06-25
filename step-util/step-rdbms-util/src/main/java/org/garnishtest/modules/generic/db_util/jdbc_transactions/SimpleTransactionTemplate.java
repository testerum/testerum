package org.garnishtest.modules.generic.db_util.jdbc_transactions;

import org.garnishtest.modules.generic.exception_utils.suppressed_ex.ExceptionTrackingExecutor;
import org.garnishtest.modules.generic.exception_utils.suppressed_ex.action.NoResultRiskyAction;
import org.garnishtest.modules.generic.exception_utils.suppressed_ex.action.RiskyAction;
import org.garnishtest.modules.generic.exception_utils.suppressed_ex.action.RiskyActionExecutionResult;
import lombok.NonNull;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class SimpleTransactionTemplate {

    @NonNull
    private final DataSource dataSource;

    public SimpleTransactionTemplate(@NonNull final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void executeInTransaction(@NonNull final DatabaseAction action) throws SimpleTransactionTemplateException {
        try {
            tryToExecuteInTransaction(action);
        } catch (final Exception e) {
            throw new SimpleTransactionTemplateException("failed to execute in transaction", e);
        }
    }

    private void tryToExecuteInTransaction(final DatabaseAction action) {
        final Connection connection = acquireDatabaseConnection();

        final ExceptionTrackingExecutor executor = new ExceptionTrackingExecutor();

        final RiskyAction<Void> databaseAction = databaseAction(action, connection);
        final RiskyAction<Void> commit = commitAction(connection);
        final RiskyAction<Void> rollback = rollbackAction(connection);
        final RiskyAction<Void> close = closeAction(connection);

        final RiskyActionExecutionResult<Void> dbActionResult = executor.execute(databaseAction);
        if (dbActionResult.succeeded()) {
            executor.execute(commit);
        } else {
            executor.execute(rollback);
        }
        executor.execute(close);

        executor.throwIfNeeded();
    }

    private Connection acquireDatabaseConnection() {
        try {
            final Connection connection = this.dataSource.getConnection();

            connection.setAutoCommit(false);

            return connection;
        } catch (final Exception e) {
            throw new SimpleTransactionTemplateException("failed acquire database transaction from data source", e);
        }
    }

    private RiskyAction<Void> databaseAction(@NonNull final DatabaseAction action,
                                             @NonNull final Connection connection) {
        return new NoResultRiskyAction() {
            @Override
            protected void executeWithoutResult() throws Exception {
                action.doInTransaction(connection);
            }
        };
    }

    private RiskyAction<Void> commitAction(@NonNull final Connection connection) {
        return new NoResultRiskyAction() {
            @Override
            protected void executeWithoutResult() throws SQLException {
                connection.commit();
            }
        };
    }

    private RiskyAction<Void> rollbackAction(@NonNull final Connection connection) {
        return new NoResultRiskyAction() {
            @Override
            protected void executeWithoutResult() throws SQLException {
                connection.rollback();
            }
        };
    }

    private RiskyAction<Void> closeAction(@NonNull final Connection connection) {
        return new NoResultRiskyAction() {
            @Override
            protected void executeWithoutResult() throws SQLException {
                connection.close();
            }
        };
    }

}
