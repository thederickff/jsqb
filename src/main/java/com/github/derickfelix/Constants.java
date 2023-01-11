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
package com.github.derickfelix;

import java.util.HashMap;

/**
 *
 * @author Pablo Eduardo Martinez Solis
 */
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
    if (dialectConstants.isEmpty())
      return;
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
