package impl;

import model.PhysicalColumn;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QueryExecutorDDLTest extends BaseQueryExecutorTest {

    public static final String TABLE_NAME = "ddl_test_table";

    @DisplayName("테이블 생성")
    @Test
    @Order(1)
    public void createTable() {
        // given
        PhysicalColumn[] columns = {
                new PhysicalColumn("id", "varchar(10)", false, true),
                new PhysicalColumn("name", "varchar(20)", false, false),
                new PhysicalColumn("price", "int", true, false)
        };

        // when
        queryExecutor.create(TABLE_NAME, columns);
    }

    @DisplayName("테이블 삭제")
    @Test
    @Order(2)
    public void dropTable() {
        // when
        queryExecutor.drop(TABLE_NAME);
    }


}
