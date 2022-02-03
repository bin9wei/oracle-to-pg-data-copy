package org.bingwei.db;

import org.bingwei.db.model.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.stream.Collectors;

public class DataCopyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataCopyService.class);
    private final DAO oracleDao;
    private final DAO pgDao;

    public DataCopyService(DAO oracleDao, DAO pgDao) {
        this.oracleDao = oracleDao;
        this.pgDao = pgDao;
    }

    public boolean verify(String table) throws SQLException {
        String sql = "select count(1) as row_count from " + table;
        int rowCountOracle = oracleDao.queryForObject(sql,
                ps -> {},
                rs -> rs.getInt("row_count"));
        int rowCountPg = pgDao.queryForObject(sql,
                ps -> {},
                rs -> rs.getInt("row_count"));
        LOGGER.info("table={}, rowCountInOracle={}, rowCountInPg={}, isRowCountSame={}",
                table, rowCountOracle, rowCountPg, rowCountOracle == rowCountPg);
        return rowCountOracle == rowCountPg;
    }

    public boolean copy(String table) throws SQLException {
        long start = System.currentTimeMillis();
        List<Column> oracleColumns = getOracleColumns(table);
        List<Column> pgColumns = getPgColumns(table);
        String selectSql = constructSelectSql(table, oracleColumns);
        String insertSql = constructInsertSql(table, pgColumns);
        LOGGER.info("Going to run select sql: {}", selectSql);
        oracleDao.query(selectSql, DAO.ProcessPreparedStatement.noop(), rs -> {
            pgDao.modify(insertSql, ps -> {
                try {
                    for (int i = 1; i <= oracleColumns.size(); i++) {
                        Column column = oracleColumns.get(i - 1);
                        if (column.getDataType().contains("NUMBER")) {
                            String value = rs.getString(column.getColumnName());
                            if (rs.wasNull()) {
                                ps.setNull(i, Types.NUMERIC);
                            } else if (value.contains(".")) {
                                ps.setDouble(i, Double.parseDouble(value));
                            } else {
                                ps.setLong(i, Long.parseLong(value));
                            }
                        } else if (column.getDataType().contains("VARCHAR")) {
                            ps.setString(i, rs.getString(column.getColumnName()));
                        } else if (column.getDataType().contains("TIMESTAMP")) {
                            ps.setTimestamp(i, rs.getTimestamp(column.getColumnName()));
                        } else if (column.getDataType().contains("DATE")) {
                            ps.setDate(i, rs.getDate(column.getColumnName()));
                        } else if (column.getDataType().contains("BLOB")) {
                            if (null != rs.getBlob(column.getColumnName())) {
                                ps.setBinaryStream(i, rs.getBlob(column.getColumnName()).getBinaryStream(), rs.getBlob(column.getColumnName()).length());
                            } else {
                                ps.setNull(i, Types.BINARY);
                            }
                        } else if (column.getDataType().contains("CLOB")) {
                            String data;
                            if (null != rs.getClob(column.getColumnName())) {
                                Clob clob = rs.getClob(column.getColumnName());
                                Reader r = clob.getCharacterStream();
                                StringBuffer buffer = new StringBuffer();
                                int ch;
                                while (true) {
                                    if (!((ch = r.read()) != -1)) {
                                        break;
                                    }
                                    buffer.append("" + (char) ch);
                                }
                                data = buffer.toString();
                            } else {
                                data = "";
                            }
                            ps.setString(i, data);
                        } else {
                            ps.setString(i, rs.getString(column.getColumnName()));
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            return null;
        });
        LOGGER.info("Complete data copy, table={}, timeCost={}ms", table, System.currentTimeMillis() - start);
        return true;
    }

    private String constructSelectSql(String table, List<Column> columns) {
        return String.format("select %s from %s", columns.stream().map(Column::getColumnName).collect(Collectors.joining(",")), table);
    }

    private String constructInsertSql(String table, List<Column> columns) {
        String columnNames = columns.stream().map(Column::getColumnName).collect(Collectors.joining(","));
        String placeHolders = columns.stream().map(it -> "?").collect(Collectors.joining(","));
        return String.format("insert into %s (%s) values (%s)", table, columnNames, placeHolders);
    }

    private List<Column> getOracleColumns(String tableName) throws SQLException {
        String sql = "select * from ALL_TAB_COLUMNS where TABLE_NAME=? order by COLUMN_ID";
        return oracleDao.query(sql,
                ps -> ps.setString(1, tableName.toUpperCase()),
                rs -> Column.builder()
                        .columnName(rs.getString("COLUMN_NAME").toUpperCase())
                        .dataType(rs.getString("DATA_TYPE"))
                        .nullable("Y".equalsIgnoreCase(rs.getString("NULLABLE")))
                        .columnId(rs.getInt("COLUMN_ID"))
                        .build());
    }

    private List<Column> getPgColumns(String tableName) throws SQLException {
        String sql = "select * from information_schema.columns where table_name=? order by ordinal_position";
        return pgDao.query(sql,
                ps -> ps.setString(1, tableName.toLowerCase()),
                rs -> Column.builder()
                        .columnName(rs.getString("column_name").toUpperCase())
                        .dataType(rs.getString("data_type"))
                        .nullable("YES".equalsIgnoreCase(rs.getString("is_nullable")))
                        .columnId(rs.getInt("ordinal_position"))
                        .build());
    }

}
