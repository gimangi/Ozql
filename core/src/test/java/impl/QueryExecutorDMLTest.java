package impl;

import exceptions.OzqlException;
import model.PhysicalColumn;
import org.junit.jupiter.api.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class QueryExecutorDMLTest extends BaseQueryExecutorTest {

    public static final String TABLE_NAME = "dml_test_table";

    @BeforeEach
    public void createTable() {
        PhysicalColumn[] columns = {
                new PhysicalColumn("id", "varchar(10)", false, true),
                new PhysicalColumn("name", "varchar(20)", false, false),
                new PhysicalColumn("price", "int", true, false)
        };

        queryExecutor.create(TABLE_NAME, columns);
    }

    @AfterEach
    public void dropTable() {
        queryExecutor.drop(TABLE_NAME);
    }

    @DisplayName("레코드 삽입 및 조회")
    @Test
    public void insertAndSelectTest() throws SQLException {
        // given
        String[] columns = {"id", "name", "price"};
        Object[] values = {"root1234", "Robin", 1234};

        // when
        queryExecutor.insert(TABLE_NAME, columns, values);

        ResultSet rs = queryExecutor.select(TABLE_NAME, "*", "1 = 1");
        rs.next();
        String id = rs.getString(1);
        String name = rs.getString(2);
        int price = rs.getInt(3);

        // then
        assertEquals(values[0], id);
        assertEquals(values[1], name);
        assertEquals(values[2], price);
    }

    @DisplayName("nullable 필드 제외 레코드 삽입 및 조회")
    @Test
    public void insertNullableAndSelectTest() throws OzqlException, SQLException {
        // given
        String[] columns = {"id", "name"};
        Object[] values = {"root1234", "Robin"};

        // when
        queryExecutor.insert(TABLE_NAME, columns, values);

        ResultSet rs = queryExecutor.select(TABLE_NAME, "*", "1 = 1");
        rs.next();
        String id = rs.getString(1);
        String name = rs.getString(2);
        int price = rs.getInt(3);

        // then
        assertEquals(values[0], id);
        assertEquals(values[1], name);
        assertEquals(0, price);
        assertTrue(rs.wasNull());
    }

    @DisplayName("not null 제외 필드 레코드 삽입 시 예외")
    @Test
    public void insertNotNullAndSelectTest() {
        // given
        String[] columns = {"id"};
        Object[] values = {"root1234"};

        // when, then
        assertThrows(OzqlException.class, () -> {
            queryExecutor.insert(TABLE_NAME, columns, values);
        });
    }

    @DisplayName("레코드 삽입 시 컬럼과 값의 길이가 다른 경우 실패")
    @Test
    public void insertFailTest() {
        // given
        String[] columns = {"id", "name", "price"};
        String[] values = {"root1234", "Robin"};

        // when, then
        assertThrows(OzqlException.class, () -> {
            queryExecutor.insert(TABLE_NAME, columns, values);
        });
    }

}
