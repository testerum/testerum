package org.garnishtest.modules.generic.db_util.datasource;

import lombok.NonNull;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

/**
 * <p>
 *     Applies database migrations and then returns a data source that can be safely used, since the database has
 *     been migrated to the latest version.
 * </p>
 * <p>
 *     In case the migration fails, the {@link #create()}} method throws an exception. This will prevent the
 *     Spring context from starting, which is what we want, since in cases like this, probably a manual database
 *     rollback is needed.
 * </p>
 */
public final class FlywayMigrationDataSourceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlywayMigrationDataSourceFactory.class);

    private static final String FLYWAY_IGNORE_MIGRATION_ERRORS = "flyway.ignoreMigrationErrors";

    @Nonnull
    private final DataSource dataSource;

    public FlywayMigrationDataSourceFactory(@NonNull final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource create() {
        try {
            applyMigrations();
        } catch (final Exception e) {
            final String propertyValue = System.getProperty(FLYWAY_IGNORE_MIGRATION_ERRORS);
            final boolean ignore = "TRUE".equalsIgnoreCase(propertyValue);

            if (ignore) {
                LOGGER.warn(
                        "IGNORING the flyway migration errors because property [{}] is set to [{}]",
                        FLYWAY_IGNORE_MIGRATION_ERRORS,
                        propertyValue
                );
                LOGGER.warn("ERROR that will be ignored", e);
            } else {
                LOGGER.warn(
                        "NOT ignoring the flyway migration errors; failing. Because property [{}] is set to [{}]",
                        FLYWAY_IGNORE_MIGRATION_ERRORS,
                        propertyValue
                );

                throw e;
            }
        }

        return this.dataSource;
    }

    private void applyMigrations() throws FlywayException {
        final Flyway flyway = new Flyway();
        flyway.setDataSource(this.dataSource);

        final int numberOfAppliedMigrations = flyway.migrate();

        if (numberOfAppliedMigrations == 0) {
            LOGGER.info("no migrations have been applied - the database is up-to-date");
        } else {
            LOGGER.info("{} migrations have been successfully applied", numberOfAppliedMigrations);
        }
    }
}
