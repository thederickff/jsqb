package com.github.str4ng3r;

import com.github.str4ng3r.Tables.ACTIONSQL;

public class Update extends QueryBuilder<Update> {
    public Update() {
        super();
        super.setReferenceObject(this);
        initialize();
    }

    private void initialize() {
        this.tables = new Tables(ACTIONSQL.UPDATE);
    }

    @Override
    protected String write() {
        StringBuilder sql = this.tables.write();

        this.where.write(sql);

        return sql.toString();
    }
}
