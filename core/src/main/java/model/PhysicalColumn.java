package model;

public record PhysicalColumn (
    String name, String type, boolean nullable, boolean primaryKey
) {

    public static String toSql(PhysicalColumn column) {
        String sql = column.name + " " + column.type +
                (column.nullable ? " NULL" : " NOT NULL");
        if (column.primaryKey)
            sql += " PRIMARY KEY";
        return sql;
    }
}
