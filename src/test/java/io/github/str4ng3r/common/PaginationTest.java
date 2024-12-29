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

import static org.junit.Assert.assertEquals;

import io.github.str4ng3r.exceptions.InvalidCurrentPageException;
import io.github.str4ng3r.exceptions.InvalidSqlGenerationException;

public class PaginationTest {


  public void testPagination() throws InvalidCurrentPageException, InvalidSqlGenerationException {
    Selector s = new Selector().select("test t");
    SqlParameter sql = s.getSqlAndParameters();
    s.setPagination(sql, new Pagination(10, 2629, 1));
    String exp = "SELECT * FROM test t LIMIT 0 OFFSET 10";
    check(exp, sql.sql);
  }

  private void check(String exp, String act) {
    System.out.println("Exp: " + exp.toLowerCase());
    System.out.println("Act: " + act.toLowerCase());

    assertEquals(exp, act);
  }
}
