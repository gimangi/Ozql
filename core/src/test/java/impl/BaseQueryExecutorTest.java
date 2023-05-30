package impl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.sql.SQLException;

public class BaseQueryExecutorTest {

    protected static QueryExecutor queryExecutor;

    @BeforeAll
    public static void connect() throws SQLException {
        queryExecutor = QueryExecutor.of("jdbc:mysql://localhost:3306/test_db", "root", "123456");
    }

    @AfterAll
    public static void disconnect() {
        queryExecutor.disconnect();
    }
}
