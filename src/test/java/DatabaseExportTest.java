import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;



class DatabaseExportTest {
    private static String driver = "com.mysql.cj.jdbc.Driver";
    private static String host = "localhost";
    private static String port = "3306";
    private static String database = "idoc_mysql";
    private static String user = "root";
    private static String password = "123456";
    private static String url = String.format("jdbc:mysql://%s:%s/%s?charset=utf8&nullCatalogMeansCurrent=true", host, port, database);

    private static DatabaseExport databaseExport;

    @BeforeAll
    public static void setup() throws Exception {
        databaseExport = new DatabaseExport();
    }

    @AfterAll
    public static void cleanup() throws Exception {
        // 删除生成的文件
        Files.deleteIfExists(Paths.get("E://数据库表.docx"));
        //Files.deleteIfExists(Paths.get("E://表.docx"));
    }

    @Test
    @DisplayName("测试 testJdbcConnect 方法")
    public void testJdbcConnect() throws Exception {
        // 测试数据库连接
        Connection conn = DriverManager.getConnection(url, user, password);
        assertNotNull(conn);
        conn.close();
    }

    @Nested
    @DisplayName("测试 showAllColumn 方法")
    class ShowAllColumnTest {

        @Test
        @DisplayName("当 metaData 中不存在表名时，showAllColumn 方法应该抛出 NullPointerException 异常")
        void testShowAllColumnWhenTableNotExists() {
            assertThrows(NullPointerException.class, () -> databaseExport.showAllColumn(null, "nonexistent_table"));
        }

        @Test
        @DisplayName("当 metaData 中存在表名时，showAllColumn 方法应该返回该表的所有列")
        void testShowAllColumnWhenTableExists() throws Exception {
            String expected = "id:INT, name:VARCHAR, ";

            String tableName = "auth_group";
            String url = String.format("jdbc:mysql://%s:%s/%s?charset=utf8&nullCatalogMeansCurrent=true", host, port, database);
            Connection conn = DriverManager.getConnection(url, user, password);
            DatabaseMetaData metaData = conn.getMetaData();
            String result = databaseExport.showAllColumn(metaData, tableName);
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("测试 showAllColumn 方法，获取所有列")
        public void testShowAllColumn() throws Exception {
            // 测试获取所有列
            Connection conn = DriverManager.getConnection(url, user, password);
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tableResultSet = metaData.getTables(null, null, null, null);
            while (tableResultSet.next()) {
                String tableName = tableResultSet.getString("TABLE_NAME");
                System.out.println("table:" + tableName);
                databaseExport.showAllColumn(metaData, tableName);
            }
            conn.close();
        }
    }



    @Test
    @DisplayName("测试合并文档")
    public void testAddDocument() throws Exception {
        // 测试合并文档
        String mainFile = "E://数据库表.docx";
        String addFile = "E://表.docx";
        // 创建空的主文件
        Files.createFile(Paths.get(mainFile));
        // 将一个空的文件添加到主文件中
        Files.copy(Paths.get(addFile), new FileOutputStream(mainFile));
        databaseExport.addDocument();
        assertTrue(new File(mainFile).length() > 0);
    }

}