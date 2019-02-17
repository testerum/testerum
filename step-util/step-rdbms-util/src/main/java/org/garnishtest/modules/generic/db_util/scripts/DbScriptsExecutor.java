package org.garnishtest.modules.generic.db_util.scripts;

import org.garnishtest.modules.generic.db_util.jdbc_transactions.DatabaseAction;
import org.garnishtest.modules.generic.db_util.jdbc_transactions.SimpleTransactionTemplate;
import org.garnishtest.modules.generic.variables_resolver.VariablesResolver;
import org.garnishtest.modules.generic.variables_resolver.impl.NoOpVariablesResolver;
import org.garnishtest.modules.generic.variables_resolver.impl.escape.impl.ValueEscapers;
import lombok.NonNull;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.DbSupportFactory;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.SqlScript;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Types;
import java.util.List;

public class DbScriptsExecutor {

    @NonNull private final SimpleTransactionTemplate transactionTemplate;

    public DbScriptsExecutor(@NonNull final DataSource dataSource) {
        this.transactionTemplate = new SimpleTransactionTemplate(dataSource);
    }

    public void executeScripts(@NonNull final List<? extends Resource> sqlScriptResources) throws DbScriptsExecutorException {
        executeScripts(NoOpVariablesResolver.instance(), sqlScriptResources);
    }

    public void executeScripts(@NonNull final VariablesResolver variablesResolver,
                               @NonNull final List<? extends Resource> sqlScriptResources) throws DbScriptsExecutorException {
        try {
            this.transactionTemplate.executeInTransaction(new DatabaseAction() {
                @Override
                public void doInTransaction(final Connection connection) {
                    DbScriptsExecutor.this.executeScripts(connection, variablesResolver, sqlScriptResources);
                }
            });
        } catch (final Exception e) {
            throw new DbScriptsExecutorException(
                    "failed to execute SQL scripts [" + sqlScriptResources + "]" +
                    " with variablesResolver=[" + variablesResolver + "]",
                    e
            );
        }
    }

    public void executeScripts(@NonNull final VariablesResolver variablesResolver,
                               @NonNull final String sqlScript) throws DbScriptsExecutorException {
        this.transactionTemplate.executeInTransaction(new DatabaseAction() {
            @Override
            public void doInTransaction(final Connection connection) {
                DbScriptsExecutor.this.executeScripts(connection, variablesResolver, sqlScript);
            }
        });
    }

    private void executeScripts(@NonNull final Connection connection,
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
            throw new DbScriptsExecutorException("failed to execute SQL script [" + scriptContent + "]", e);
        }
    }

    private void executeScripts(@NonNull final Connection connection,
                                @NonNull final VariablesResolver variablesResolver,
                                @NonNull final List<? extends Resource> sqlScriptResources) {
        final DbSupport dbSupport = createDbSupport(connection);

        for (final Resource sqlScriptResource : sqlScriptResources) {
            executeScript(variablesResolver, sqlScriptResource, dbSupport, connection);
        }
    }

    private DbSupport createDbSupport(final Connection connection) {
        final boolean printInfo = true;

        return DbSupportFactory.createDbSupport(connection, printInfo);
    }

    private void executeScript(@NonNull final VariablesResolver variablesResolver,
                               @NonNull final Resource sqlScriptResource,
                               @NonNull final DbSupport dbSupport,
                               @NonNull final Connection connection) {
        String scriptContentWithVariablesReplaced = null;
        try {
            final String scriptContent = loadScriptContent(sqlScriptResource);

            scriptContentWithVariablesReplaced = variablesResolver.resolveVariablesInText(scriptContent, ValueEscapers.none());

            final SqlScript sqlScript = new SqlScript(scriptContentWithVariablesReplaced, dbSupport);

            sqlScript.execute(new JdbcTemplate(connection, Types.NULL));
        } catch (final Exception e) {
            String scriptContent = (scriptContentWithVariablesReplaced != null) ? scriptContentWithVariablesReplaced : "";
            throw new DbScriptsExecutorException("failed to execute SQL script [" + scriptContent + "]", e);
        }
    }

    private String loadScriptContent(@NonNull final Resource sqlScriptResource) throws DbScriptsExecutorException {
        final InputStream inputStream;
        try {
            inputStream = sqlScriptResource.getInputStream();
        } catch (final Exception e) {
            throw new DbScriptsExecutorException(
                    "failed to open stream to read content of resource [" + sqlScriptResource + "]",
                    e
            );
        }

        final AutoCloseInputStream autoCloseInputStream = new AutoCloseInputStream(inputStream);

        try {
            return IOUtils.toString(autoCloseInputStream, "UTF-8");
        } catch (final Exception e) {
            throw new DbScriptsExecutorException("failed to read content of resource [" + sqlScriptResource + "]", e);
        }
    }

}
