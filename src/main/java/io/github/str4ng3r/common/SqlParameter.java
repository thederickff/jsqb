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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Pablo Eduardo Martinez Solis
 */
public class SqlParameter {
  private String sql;
  private List<Object> listParamaters = new ArrayList<>();
  private HashMap<String, Object> dictionarieParameters;
  private Pagination p = null;

  SqlParameter(String sql, List<Object> parameter) {
    this.sql = sql;
    this.listParamaters = parameter;
  }

  public SqlParameter(String sql, HashMap<String, Object> parameters) {
    this.sql = sql;
    this.dictionarieParameters = parameters;
  }

  public String getSql() {
    return this.sql;
  }

  /** Package-private: only the builder pipeline may mutate the generated SQL. */
  void setSql(String sql) {
    this.sql = sql;
  }

  /** Package-private: pagination metadata is set internally by the builder. */
  void setPagination(Pagination p) {
    this.p = p;
  }

  Pagination getPagination() {
    return this.p;
  }

  public List<Object> getListParameters() {
    return this.listParamaters;
  }

  public HashMap<String, Object> dictionarieParameters() {
    return this.dictionarieParameters;
  }

  @Override
  public String toString() {
    String query = "{\n\tsql: \"" + this.sql + "\",\n\tparameters: " + this.listParamaters;
    if (p != null)
      query += ",\n\tpagination: " + p;
    return query + "\n}";
  }
}
