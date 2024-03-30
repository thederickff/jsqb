package io.github.str4ng3r.ddl;

import io.github.str4ng3r.sql.Constants.SqlDialect;

public abstract class Migration {

    abstract void up(CommonDDLUtil ddl);

    abstract void down(CommonDDLUtil ddl);

    void execute(Object newParam) {
        CommonDDLUtil ddl = new CommonDDLUtil();
        ddl.setDialect(SqlDialect.Sql);
        up(ddl);
        ddl.setDialect(SqlDialect.Sql);
        down(ddl);
    }
}
