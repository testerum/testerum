package org.garnishtest.modules.generic.db_util.scripts;

import lombok.NonNull;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.DbSupportFactory;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.SqlScript;
import org.garnishtest.modules.generic.db_util.jdbc_transactions.SimpleTransactionTemplate;
import org.garnishtest.modules.generic.variables_resolver.VariablesResolver;
import org.garnishtest.modules.generic.variables_resolver.impl.escape.impl.ValueEscapers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Types;

import static kotlin.collections.CollectionsKt.joinToString;
import static kotlin.text.StringsKt.lines;

public class DbScriptsExecutor {

    @NonNull
    private final SimpleTransactionTemplate transactionTemplate;

    public DbScriptsExecutor(@NonNull final DataSource dataSource) {
        this.transactionTemplate = new SimpleTransactionTemplate(dataSource);
    }

    public void executeScript(@NonNull final VariablesResolver variablesResolver,
                              @NonNull final String sqlScript) throws DbScriptsExecutorException {
        this.transactionTemplate.executeInTransaction(connection ->
                DbScriptsExecutor.this.executeScript(connection, variablesResolver, sqlScript)
        );
    }

    private void executeScript(@NonNull final Connection connection,
                               @NonNull final VariablesResolver variablesResolver,
                               @NonNull final String sqlScriptAsString) {
        String scriptContentWithVariablesReplaced = null;
        try {
            final DbSupport dbSupport = createDbSupport(connection);

            scriptContentWithVariablesReplaced = variablesResolver.resolveVariablesInText(sqlScriptAsString, ValueEscapers.none());

            final SqlScript sqlScript = new SqlScript(scriptContentWithVariablesReplaced, dbSupport);

            sqlScript.execute(new JdbcTemplate(connection, Types.NULL));
        } catch (final Exception e) {
            String scriptContent = (scriptContentWithVariablesReplaced != null) ? scriptContentWithVariablesReplaced : "";
            String indentScriptLines = joinToString(
                    lines(scriptContent),
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
