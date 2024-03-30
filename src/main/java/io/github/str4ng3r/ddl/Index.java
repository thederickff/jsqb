package io.github.str4ng3r.ddl;


public class Index {
    String name;
    String column;
    String type;
    
    public Index(String name, String column, String type) {
        this(name, column);
        this.name = name;
    }
    
    public Index(String name, String column) {
        this(name);
        this.name = name;
        this.column = column;
    }

    public Index(String name) {
        this.name = name;
    }
}
