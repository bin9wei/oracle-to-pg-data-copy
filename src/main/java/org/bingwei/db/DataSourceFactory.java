package org.bingwei.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import oracle.jdbc.OracleConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.time.Duration;

public class DataSourceFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceFactory.class);
    private final HikariConfig config = new HikariConfig();
    private HikariDataSource ds;
    private final String jdbcUrl;
    private final String username;
    private final String password;

    private final Object lock = new Object();

    public DataSourceFactory(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        initConfig();
    }

    private void initConfig() {
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty(OracleConnection.CONNECTION_PROPERTY_IMPLICIT_STATEMENT_CACHE_SIZE, "100");
        config.addDataSourceProperty(OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH, "5000");
        config.addDataSourceProperty(OracleConnection.CONNECTION_PROPERTY_IMPLICIT_STATEMENT_CACHE_SIZE, "100");
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setMaxLifetime(Duration.ofMinutes(5).toMillis());
    }


    public DataSource getDataSource() {
        if (ds == null) {
            synchronized (lock) {
                if (ds == null) {
                    ds = new HikariDataSource(config);
                    LOGGER.info("Datasource created with url={}, user={}", jdbcUrl, username);
                }
            }
        }
        return ds;
    }
}