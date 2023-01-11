package com.github.derickfelix;

import java.util.HashMap;

class Constants {
  public static HashMap<String, String> dialectConstants = new HashMap<>();

  public static enum SqlDialect {
    Mysql("MySQL"), Oracle("Oracle"), Postgres("Postgres"), Sql("SQL");

    public String sqlDialect;

    SqlDialect(String sqlDialect) {
      this.sqlDialect = sqlDialect;
    }
  }

  public static enum Actions {
    SEPARATOR("SEPARATOR");

    public String action;

    Actions(String action) {
      this.action = action;
    }
  };

  Constants() {
    dialectConstants.put(SqlDialect.Sql.sqlDialect + Constants.Actions.SEPARATOR.action, "");
    dialectConstants.put(Constants.SqlDialect.Postgres.sqlDialect + Constants.Actions.SEPARATOR.action, "`");
  }

  public String getAction(String dialect, Actions action) {
    String k = dialect + action.action;
    if (dialectConstants.containsKey(k))
      return dialectConstants.get(k);

    return dialectConstants.get(SqlDialect.Sql.sqlDialect + action);
  }
}
