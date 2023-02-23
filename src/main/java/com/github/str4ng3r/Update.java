package com.github.str4ng3r;

import java.util.HashSet;
import java.util.Set;

import com.github.str4ng3r.Tables.ACTIONSQL;

public class Update extends QueryBuilder<Update> {

    private final Set<String> columnsToExclude = new HashSet<String>();

    public Update() {
        super();
        super.setReferenceObject(this);
        initialize();
    }

    private void initialize() {
        this.tables = new Tables(ACTIONSQL.UPDATE);
    }

    public Update excludeColumns(String... columnsToExclude) {
        for (String column : columnsToExclude) 
            this.columnsToExclude.add(column);
        return this;
    }

    /**
     * This should init from
     * 
     * @param criteria
     * @param parameters
     *
     * @return same object as pipe
     */
    public Update from(String... tableNames) {
        this.tables.from(tableNames);
        return this;
    }

    @Override
    protected String write() {
        StringBuilder sql = this.tables.write();

        this.where.write(sql);

        return sql.toString();
    }
}
