package org.garnishtest.modules.generic.db_util.scripts;

import java.sql.Connection;
import java.sql.Types;
import javax.sql.DataSource;
import lombok.NonNull;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.DbSupportFactory;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.SqlScript;
import org.garnishtest.modules.generic.db_util.jdbc_transactions.SimpleTransactionTemplate;

import static kotlin.collections.CollectionsKt.joinToString;
import static kotlin.text.StringsKt.lines;

public class DbScriptsExecutor {

    @NonNull
    private final SimpleTransactionTemplate transactionTemplate;

    public DbScriptsExecutor(@NonNull final DataSource dataSource) {
        this.transactionTemplate = new SimpleTransactionTemplate(dataSource);
    }

    public void executeScript(@NonNull final String sqlScript) throws DbScriptsExecutorException {
        this.transactionTemplate.executeInTransaction(connection ->
                DbScriptsExecutor.this.executeScript(connection, sqlScript)
        );
    }

    private void executeScript(@NonNull final Connection connection,
                               @NonNull final String sqlScriptAsString) {
        try {
            final DbSupport dbSupport = createDbSupport(connection);

            final SqlScript sqlScript = new SqlScript(sqlScriptAsString, dbSupport);

            sqlScript.execute(new JdbcTemplate(connection, Types.NULL));
        } catch (final Exception e) {
            String indentScriptLines = joinToString(
                    lines(sqlScriptAsString),
                    "\n",
                    "",
                    "",
                    -1,
                    "...",
                    line -> "\t\t" + line
            );
            throw new DbScriptsExecutorException("failed to execute SQL script\n" + indentScriptLines, e);
        }
    }

    private DbSupport createDbSupport(final Connection connection) {
        final boolean printInfo = true;

        return DbSupportFactory.createDbSupport(connection, printInfo);
    }

}
