package org.bingwei.db;

import org.bingwei.db.util.DbUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static final List<String> TABLES = List.of("students", "attachment", "description");

    public static void main(String[] args) throws Exception {
        DataSource oracle = new DataSourceFactory(Constraints.ORACLE.JDBC_URL, Constraints.ORACLE.USERNAME, Constraints.ORACLE.PASSWORD).getDataSource();
        DataSource pg = new DataSourceFactory(Constraints.PG.JDBC_URL, Constraints.PG.USERNAME, Constraints.PG.PASSWORD).getDataSource();
        DAO oracleDao = new DAO(oracle);
        DAO pgDao = new DAO(pg);
        DataCopyService dataCopyService = new DataCopyService(oracleDao, pgDao);

        initOracleSchemaAndData();
        initPgSchema();

        TABLES.stream().forEach(t -> {
            try {
                LOGGER.info("{} table copy started", t);
                dataCopyService.copy(t);
                LOGGER.info("{} table copy completed", t);
            } catch (SQLException e) {
                LOGGER.error("{} table copy failed", t, e);
            }
        });

        TABLES.stream().forEach(t-> {
            try {
                dataCopyService.verify(t);
            } catch (SQLException e) {
                LOGGER.error("{} table verify failed", t, e);
            }
        });
    }

    private static void initOracleSchemaAndData() throws Exception {
        // init schema and sample data in oracle
        DbUtil.runSQL(Constraints.ORACLE.JDBC_URL, Constraints.ORACLE.USERNAME, Constraints.ORACLE.PASSWORD, Constraints.ORACLE.DRIVER, Constraints.ORACLE.SQL_SCHEMA);
        DbUtil.runSQL(Constraints.ORACLE.JDBC_URL, Constraints.ORACLE.USERNAME, Constraints.ORACLE.PASSWORD, Constraints.ORACLE.DRIVER, Constraints.ORACLE.SQL_DATA);
    }

    private static void initPgSchema() throws Exception {
        // init schema in PG
        DbUtil.runSQL(Constraints.PG.JDBC_URL, Constraints.PG.USERNAME, Constraints.PG.PASSWORD, Constraints.PG.DRIVER, Constraints.PG.SQL_SCHEMA);
    }

}
