import com.spire.doc.DocumentObject;
import com.spire.doc.Section;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.spire.doc.Document;

public class DatabaseExport {

    public static void main(String[] args) throws Exception {

        String driver = "com.mysql.cj.jdbc.Driver";
        String host = "192.168.194.31";
        String port = "3306";
        String database = "renshi_test";
        String user = "root";
        String password = "root";
        //&nullCatalogMeansCurrent=true参数确保返回指定库涉及表，否则会返回数据库的所有表
        String url = String.format("jdbc:mysql://%s:%s/%s?charset=utf8&nullCatalogMeansCurrent=true",host,port,database);
        DatabaseExport de = new DatabaseExport();

        de.jdbcConnect(driver,url,user,password);
    }

    public void jdbcConnect(String driver,String url,String user,String password) throws Exception{
        Class.forName(driver);
        Connection connection = DriverManager.getConnection(url,user,password);
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            //获取所有表
            ResultSet tableResultSet = metaData.getTables(null,null,null,null);
            while(tableResultSet.next()){
                //获取Word模板，模板存放路径在项目的resources目录下，转换成流操作
                InputStream temp = this.getClass().getResourceAsStream("/temp.docx");
                //注册xdocreport实例并加载FreeMarker模板引擎
                IXDocReport report = XDocReportRegistry.getRegistry().loadReport(temp,TemplateEngineKind.Freemarker);
                //创建xdocreport上下文对象
                IContext context = report.createContext();
                String tableName = tableResultSet.getString("TABLE_NAME");
                context.put("tableName",tableName);
                List<MyColumn> columnList = new ArrayList<MyColumn>();
                System.out.println("table:"+tableName);


                //获取此表的所有列
                ResultSet columnResultSet = metaData.getColumns(null,null,tableName,null);
                while (columnResultSet.next()){
                    MyColumn column = new MyColumn();
                    //字段名称
                    String columnName = columnResultSet.getString("COLUMN_NAME");
                    //字段类型
                    String columnType = columnResultSet.getString("TYPE_NAME");
                    //字段能否为空，若能，则为1，若不能，则为0
                    String nullable = columnResultSet.getInt("NULLABLE")==1?"是":"否";
                    //描述
                    String remarks = columnResultSet.getString("REMARKS");
                    column.setColumnName(columnName);
                    column.setColumnType(columnType);
                    column.setNullable(nullable);
                    column.setRemarks(remarks);
                    columnList.add(column);
                    System.out.println("columnName:"+columnName+" columnType:"+columnType+" nullable:"+nullable+" remarks:"+remarks);
                }
                context.put("column",columnList);
                //创建字段元数据
                FieldsMetadata fm = report.createFieldsMetadata();
                //Word模板中的表格数据对应的集合类型
                fm.load("column", MyColumn.class, true);
                //输出到本地目录
                FileOutputStream out = new FileOutputStream(new File("E://表.docx"));
                report.process(context, out);
                //将每个表的文件合并到大文件中
                addDocument();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String showAllColumn(DatabaseMetaData dbmd, String tableName) throws SQLException {
        ResultSet rs = dbmd.getColumns(null, null, tableName, null);
        StringBuilder sb = new StringBuilder();
        while (rs.next()) {
            String columnName = rs.getString("COLUMN_NAME");
            String datatype = rs.getString("TYPE_NAME");
            sb.append(columnName).append(":").append(datatype).append(", ");
        }
        return sb.toString();
    }



    public void addDocument(){
        //加载主文件
        Document maindoc = new Document();
        maindoc.loadFromFile("E://数据库表.docx");
        //加载要追加的文件
        Document adddoc = new Document();
        adddoc.loadFromFile("E://表.docx");
        //获取主文件的最后一个section
        Section lastSection = maindoc.getLastSection();
        //将追加文档的段落当做新的段落添加到主文档的最后一个section中
        for(Section section:(Iterable<Section> )adddoc.getSections()){
            for(DocumentObject obj:(Iterable<DocumentObject> )section.getBody().getChildObjects()){
                lastSection.getBody().getChildObjects().add(obj.deepClone());
            }
        }
        maindoc.saveToFile("E://数据库表.docx");

    }

}

