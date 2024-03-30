package io.github.str4ng3r.ddl;

import java.util.List;

public class Table {
    List<Column> columns;   
    String name;  
    String engine;
    
    public Table(){}
    
    protected void check() throws Exception{
        if (this.name == null && this.name.isEmpty()) throw new Exception("");
    }

    public Table setColumns(List<Column> columns) {
        this.columns = columns;
        return this;
    }
    public Table setName(String name) {
        this.name = name;
        return this;
    }
    public Table setEngine(String engine) {
        this.engine = engine;
        return this;
    }
}