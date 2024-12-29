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
package io.github.str4ng3r.common;

import java.util.HashMap;

/**
 *
 * @author Pablo Eduardo Martinez Solis
 */
public class Constants {
  public static HashMap<String, String> dialectConstants = new HashMap<>();

  public enum SqlDialect {
    Mysql("MySQL"), Oracle("Oracle"), Postgres("Postgres"), Sql("SQL");

    public String sqlDialect;

    SqlDialect(String sqlDialect) {
      this.sqlDialect = sqlDialect;
    }
  }

  public enum Actions {
    SEPARATOR, PAGINATION, CREATEPK;
  };

  private String sqlDialect = SqlDialect.Sql.sqlDialect;

  public Constants() {
    if (!dialectConstants.isEmpty())
      return;
    //SQL
    dialectConstants.put(calculatedKey(SqlDialect.Sql.sqlDialect, Constants.Actions.SEPARATOR), "");
    dialectConstants.put(calculatedKey(Constants.SqlDialect.Postgres.sqlDialect, Constants.Actions.SEPARATOR), "`");
    dialectConstants.put(calculatedKey(Constants.SqlDialect.Oracle.sqlDialect, Constants.Actions.PAGINATION),
        " OFFSET :low ROWS FETCH NEXT :upper ROWS ONLY");
    dialectConstants.put(calculatedKey(Constants.SqlDialect.Mysql.sqlDialect, Constants.Actions.PAGINATION),
        " LIMIT :low, :upper");
    dialectConstants.put(calculatedKey(Constants.SqlDialect.Sql.sqlDialect, Constants.Actions.PAGINATION),
        " LIMIT :low OFFSET :upper");
    dialectConstants.put(calculatedKey(Constants.SqlDialect.Postgres.sqlDialect, Constants.Actions.PAGINATION),
            " LIMIT :low OFFSET :upper");
    //DDL
    dialectConstants.put(calculatedKey(Constants.SqlDialect.Mysql.sqlDialect, Constants.Actions.CREATEPK),
        " PRIMARY_KEY( :name ) ");
    dialectConstants.put(calculatedKey(Constants.SqlDialect.Postgres.sqlDialect, Constants.Actions.CREATEPK),
        " PRIMARY_KEY( :names ) ");
    dialectConstants.put(calculatedKey(Constants.SqlDialect.Mysql.sqlDialect, Constants.Actions.CREATEPK),
        " IDENTITY(1, 1) PRIMARY KEY");

  }

  private String calculatedKey(String dialect, Actions pagination) {return dialect + "," + pagination;}

  public void setDialect(SqlDialect sql) {
    sqlDialect = sql.sqlDialect;
  }

  public String getSqlDialect() {
    return sqlDialect;
  }

  public String getAction(Actions action) {
    String k = calculatedKey(sqlDialect, action);
    if (dialectConstants.containsKey(k))
      return dialectConstants.get(k);

    return dialectConstants.get(calculatedKey(SqlDialect.Sql.sqlDialect, action));
  }
  
  public String replaceValues(String action, String ...values){
    return Parameter.setParameter(action, values);
  }
}
