/*
 * The GPLv3 License (GPLv3)
 * 
 * Copyright (c) 2023 Pablo Eduardo Martinez Solis
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.str4ng3r;

import java.util.HashMap;

/**
 *
 * @author Pablo Eduardo Martinez Solis
 */
public class Constants {
  public static HashMap<String, String> dialectConstants = new HashMap<>();

  public static enum SqlDialect {
    Mysql("MySQL"), Oracle("Oracle"), Postgres("Postgres"), Sql("SQL");

    public String sqlDialect;

    SqlDialect(String sqlDialect) {
      this.sqlDialect = sqlDialect;
    }
  }

  public static enum Actions {
    SEPARATOR, PAGINATION;
  };

  private String sqlDialect = SqlDialect.Sql.sqlDialect;

  Constants() {
    if (!dialectConstants.isEmpty())
      return;
    dialectConstants.put(SqlDialect.Sql.sqlDialect + Constants.Actions.SEPARATOR, "");
    dialectConstants.put(Constants.SqlDialect.Postgres.sqlDialect + Constants.Actions.SEPARATOR, "`");
    dialectConstants.put(Constants.SqlDialect.Oracle.sqlDialect + Constants.Actions.PAGINATION,
        " OFFSET :low ROWS FETCH NEXT :upper ROWS ONLY");
    dialectConstants.put(Constants.SqlDialect.Mysql.sqlDialect + Constants.Actions.PAGINATION,
        " LIMIT :low, :upper");
    dialectConstants.put(Constants.SqlDialect.Sql.sqlDialect + Constants.Actions.PAGINATION,
        " LIMIT :low OFFSET :upper");
  }

  public void setDialect(SqlDialect sql) {
    sqlDialect = sql.sqlDialect;
  }

  public String getSqlDialect() {
    return sqlDialect;
  }

  public String getAction(Actions action) {
    String k = sqlDialect + action;
    if (dialectConstants.containsKey(k))
      return dialectConstants.get(k);

    return dialectConstants.get(SqlDialect.Sql.sqlDialect + action);
  }
}
