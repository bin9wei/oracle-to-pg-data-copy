package org.bingwei.db;

public class Constraints {
    // Oracle and PG jdbc url and credentials, change if needed
    public class ORACLE {
        public static final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
        public static final String USERNAME = "system";
        public static final String PASSWORD = "Oracle123456789";
        public static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
        public static final String SQL_SCHEMA = "db/oracle/schema.sql";
        public static final String SQL_DATA = "db/oracle/data.sql";
    }

    public class PG {
        public static final String JDBC_URL = "jdbc:postgresql://localhost:15432/myapp";
        public static final String USERNAME = "myapp";
        public static final String PASSWORD = "dbpass";
        public static final String DRIVER = "org.postgresql.Driver";
        public static final String SQL_SCHEMA = "db/pg/schema.sql";
    }
}
