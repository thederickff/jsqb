package io.github.str4ng3r.ddl;

import java.util.List;
import java.util.stream.Collectors;


import io.github.str4ng3r.sql.Constants;
import io.github.str4ng3r.sql.Constants.Actions;
import io.github.str4ng3r.sql.Constants.SqlDialect;

final class CommonDDLUtil {

    Constants constants = new Constants();
    
    public CommonDDLUtil(){
    }

    public void setDialect(SqlDialect dialect){
        this.constants.setDialect(SqlDialect.Sql);
    }

    public String dropColumnsFromTable(String table, List<Column> columns) {
        return dropColumnsFromTables(table, columns.stream().map(c -> c.name).collect(Collectors.toList()));
    }

    public String dropColumnsFromTables(String table, List<String> columns) {
        String ddl = "DROP COLUMN" + columns.stream().map(e -> e).collect(Collectors.joining(",")) + " FROM " + table;
        return ddl;
    }

    public String alterColumns(String table, List<Column> columns) {
        String ddl = "ALTER TABLE " + table + generateColumns(columns);
        return ddl;
    }

    private String generateColumns(List<Column> columns) {
        String ddl = columns.stream().map(e -> {
            String column = e.name + " " + e.type;
            if (constants.getSqlDialect().equals(SqlDialect.Postgres.sqlDialect))
                column += " SET ";
            column += e.notNull ? " NOT NULL " : "";
            return column;
        }).collect(Collectors.joining(", "));
        ddl += generatePk(columns.parallelStream().filter(c -> c.isPrimary).collect(Collectors.toList()));
        return ddl;
    }

    public String createIndex(List<Index> indexs, String tableName) {
        return "CREATE INDEX " + indexs.stream().map(index -> {
            return index.column + index.type + index.name;
        }).collect(Collectors.joining(""));
    }

    public String dropIndex(List<Index> indexs, String tableName) {
        return "DROP INDEX " + indexs.stream().map(index -> {
            return index.column + index.type + index.name;
        }).collect(Collectors.joining("")) + ";";
    }

    public String dropTable(String tableName) {
        return "DROP TABLE " + tableName + ";";
    }

    public String dropTable(Table table) {
        return this.dropTable(table.name);
    }

    public String createTable(Table table, List<Column> columns) throws Exception {
        table.check();
        return "CREATE TABLE " + table.name + " ( " + generateColumns(columns) + " );";
    }
    
    public String createForeignKey(String table){
        return "";
    }

    private String generatePk(List<Column> columns) {
        if (columns.size() == 0)
            return "";
        List<String> values = columns.parallelStream().map(column -> column.name).collect(Collectors.toList());
        return constants.replaceValues(constants.getAction(Actions.CREATEPK), (String[]) values.toArray());
    }

}
