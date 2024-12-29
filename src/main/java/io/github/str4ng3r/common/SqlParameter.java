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
  public String sql;
  List<Object> listParamaters = new ArrayList<>();
  HashMap<String, String> dictionarieParameters;
  protected Pagination p = null;

  SqlParameter(String sql, List<Object> parameter) {
    this.sql = sql;
    this.listParamaters = parameter;
  }

  public SqlParameter(String sql, HashMap<String, String> parameters) {
    this.sql = sql;
    this.dictionarieParameters = parameters;
  }

  public List<Object> getListParameters(){
    return this.listParamaters;
  }
  
  public HashMap<String, String> dictionarieParameters(){
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
