public class MyColumn {
    private String columnName;
    //字段类型
    private String columnType;
    //字段能否为空，若能，则为1，若不能，则为0
    private String nullable;
    //描述
    private String remarks;

    public MyColumn() {
    }

    public MyColumn(String columnName, String columnType, String nullable, String remarks) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.nullable = nullable;
        this.remarks = remarks;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getNullable() {
        return nullable;
    }

    public void setNullable(String nullable) {
        this.nullable = nullable;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
