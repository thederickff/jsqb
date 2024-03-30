package io.github.str4ng3r.ddl;

public class Column {
    String name;
    String type;
    boolean notNull;
    boolean isPrimary;
    boolean isUnique;
    
    public Column(){}

    public Column setName(String name) {
        this.name = name;
        return this;
    }
    public Column setType(String type) {
        this.type = type;
        return this;
    }
    public Column setNotNull(boolean notNull) {
        this.notNull = notNull;
        return this;
    }
    public Column setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
        return this;
    }
    public Column setUnique(boolean isUnique) {
        this.isUnique = isUnique;
        return this;
    }
}
