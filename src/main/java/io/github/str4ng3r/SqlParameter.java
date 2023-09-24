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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pablo Eduardo Martinez Solis
 */
public class SqlParameter {
  public String sql;
  public List<String> paramaters = new ArrayList<>();
  protected Pagination p = null;

  SqlParameter(String sql, List<String> paramaters) {
    this.sql = sql;
    this.paramaters = paramaters;
  }

  @Override
  public String toString() {
    String query = "{\n\tsql: \"" + this.sql + "\",\n\tparameters: " + this.paramaters;
    if (p != null)
      query += ",\n\tpagination: " + p;
    return query + "\n}";
  }
}
