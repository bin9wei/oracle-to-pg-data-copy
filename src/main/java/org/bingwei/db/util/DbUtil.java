package org.bingwei.db.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DbUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(DbUtil.class.getName());

    public static void runSQL(String url, String userName, String pwd, String driver, String sqlFile) throws Exception {

        Exception error = null;
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, pwd);
            conn.setAutoCommit(false);
            ScriptRunner runner = new ScriptRunner(conn);
            runner.setAutoCommit(false);
            runner.setStopOnError(true);
            runner.setSendFullScript(false);
            runner.setDelimiter(";");
            runner.setFullLineDelimiter(false);
            InputStream is = DbUtil.class.getClassLoader().getResourceAsStream(sqlFile);
            runner.runScript(new InputStreamReader(is, StandardCharsets.UTF_8));
            conn.commit();
        } catch (Exception e) {
            LOGGER.info("Failed to run sql", e);
            error = e;
        } finally {
            close(conn);
        }
        if (error != null) {
            throw error;
        }
    }

    private static void close(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to close db connection", e);
            if (conn != null) {
                conn = null;
            }
        }
    }
}
