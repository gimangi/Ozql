package impl;

import exceptions.OzqlException;
import model.PhysicalColumn;

import java.sql.*;

public class QueryExecutor implements AutoCloseable {

    private static final String BASE_SELECT = "SELECT %s FROM %s WHERE %s";
    private static final String BASE_SELECT_ORDER_BY = "SELECT %s FROM %s WHERE %s ORDER BY %s";
    private static final String BASE_CREATE = "CREATE TABLE %s (%s)";
    private static final String BASE_DROP = "DROP TABLE %s";
    private static final String BASE_INSERT = "INSERT INTO %s (%s) VALUES (%s)";

    private final Connection connection;

    private QueryExecutor(Connection connection) {
        this.connection = connection;
    }

    public static QueryExecutor of(String url, String user, String password) throws OzqlException {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            return new QueryExecutor(conn);
        } catch (SQLException e) {
            throw new OzqlException(String.format("Can not connect DB to %s; user = %s; password = %s. Inner cause is %s", url, user, password, e), e);
        }
    }

    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new OzqlException("fail to disconnect.", e);
        }
    }

    public ResultSet select(String table, String project, String filter) {
        Statement stmt;

        try {
            stmt = connection.createStatement();
            String query = String.format(BASE_SELECT, project, table, filter);

            return stmt.executeQuery(query);
        } catch (SQLException e) {
            throw queryException(e);
        }
    }

    public ResultSet select(String table, String project, String filter, String orderBy) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            String query = String.format(BASE_SELECT_ORDER_BY, project, table, filter, orderBy);

            return stmt.executeQuery(query);
        } catch (SQLException e) {
            throw queryException(e);
        }
    }

    public void create(String table, PhysicalColumn[] columns) {
        Statement stmt = null;
        String columnInfo = arrayToSql(columns, PhysicalColumn::toSql);

        try {
            stmt = connection.createStatement();
            String query = String.format(BASE_CREATE, table, columnInfo);

            stmt.execute(query);
        } catch (SQLException e) {
            throw queryException(e);
        }
    }

    public void drop(String table) {
        Statement stmt = null;

        try {
            stmt = connection.createStatement();
            String query = String.format(BASE_DROP, table);

            stmt.execute(query);
        } catch (SQLException e) {
            throw queryException(e);
        }
    }

    public int insert(String table, String[] columns, Object[] values) {
        if (columns.length == 0 || columns.length != values.length)
            throw new OzqlException("Invalid columns and values length");

        Statement stmt = null;

        String columnInfo = arrayToSql(columns, String::toString);
        String valueInfo = arrayToSql(values, (o) -> {
            if (o instanceof String) {
                return "'" + o + "'";
            }
            return o.toString();
        });

        try {
            stmt = connection.createStatement();
            String query = String.format(BASE_INSERT, table, columnInfo, valueInfo);

            return stmt.executeUpdate(query);
        } catch (SQLException e) {
            throw queryException(e);
        }
    }

    private static <T> String arrayToSql(T[] values, SqlConvertMethod<T> method) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            sb.append(method.convert(values[i]));

            if (i < values.length - 1)
                sb.append(", ");
        }

        return sb.toString();
    }

    @FunctionalInterface
    private interface SqlConvertMethod<T> {
        String convert(T obj);
    }

    private static OzqlException queryException(Throwable e) {
        return new OzqlException("Query can not execute.", e);
    }

    @Override
    public void close() throws Exception {
        disconnect();
    }
}
