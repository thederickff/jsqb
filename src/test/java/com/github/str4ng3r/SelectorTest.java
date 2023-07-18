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
package com.github.str4ng3r;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import com.github.str4ng3r.Join.JOIN;
import com.github.str4ng3r.exceptions.InvalidCurrentPageException;
import com.github.str4ng3r.exceptions.InvalidSqlGenerationException;

/**
 *
 * @author Pablo Eduardo Martinez Solis
 */
public class SelectorTest {

  SqlParameter baseQueryPaginated(Selector selector, String startDate, String gaId, String endDate, Integer page,
      Integer pageSize) throws InvalidCurrentPageException, InvalidSqlGenerationException {
    if (endDate != null)
      selector.addSelect("a.startDate")
          .andWhere("a.endDate = :endDate", selector.addParameter("endDate", endDate, true));

    if (gaId != null)
      selector.join(JOIN.INNER, "group_account as ga", "ga.id = a.group_account_id")
          .andWhere("ga.id = :gaId", selector.addParameter("gaId", gaId));

    if (startDate != null)
      selector.andWhere("a.startDate= :startDate", selector.addParameter("startDate", startDate, true));

    if (page != null && pageSize != null) {
      SqlParameter sql = selector.getSqlAndParameters();

      // Execute this...
      selector.getCount(sql.sql);

      Integer count = 1000;
      selector.setPagination(sql, new Pagination(pageSize, count, page));
      return sql;
    }

    return selector.getSqlAndParameters();
  }

  @Test
  public void testDelete() throws InvalidSqlGenerationException {
    SqlParameter sql = new Delete()
        .from("users u")
        .join(JOIN.INNER, "additional_properties ap", "u.id = ap.user_id")
        .where("ap.need_to_delete = TRUE")
        .getSqlAndParameters();
    System.out.println(sql);
  }

  @Test
  public void updateTest() throws InvalidSqlGenerationException {
    SqlParameter sql = new Update()
        .from("users u")
        .join(JOIN.INNER, "additional_properties ap", "u.id = ap.user_id")
        .where("ap.need_to_delete = TRUE")
        .getSqlAndParameters();

    System.out.println(sql);
  }

  Selector baseQuery() {
    Selector s = new Selector();
    return s.select("table_a", "last_name", "first_name", "age")
        .join(JOIN.LEFT, "table_b as b", "table_b.a = table_a.b")
        .join(JOIN.INNER, "table_c as c", "table_c.a = table_a.c")
        .join(JOIN.RIGHT, "table_d as d", "table_d.a = table_c.a")
        .where("table_a.a > :lowerValue AND table_b.b < 20 AND table_c.c > :upperValue",
            s.addParameter("lowerValue", "12"),
            s.addParameter("upperValue", "15"))
        .orderBy("a.a", true)
        .groupBy("a.a")
        .having("cRows > 1")
        .setDialect(Constants.SqlDialect.Oracle)
        .andHaving("cRows < 10");
  }

  @Test
  public void testBaseQueryFilterCriteria() throws InvalidCurrentPageException, InvalidSqlGenerationException {
    String endDate = "12/12/2021";
    String startDate = "1/01/2020";
    String gaId = "1223424";

    SqlParameter sql = baseQueryPaginated(baseQuery(), startDate, gaId, endDate, null, null);
    String exp = "SELECT last_name, first_name, age, a.startDateFROM table_a LEFT JOIN table_b as b ON table_b.a = table_a.b INNER JOIN table_c as c ON table_c.a = table_a.c RIGHT JOIN table_d as d ON table_d.a = table_c.a INNER JOIN group_account as ga ON ga.id = a.group_account_id WHERE table_a.a > ? AND table_b.b < 20 AND table_c.c > ? AND a.endDate = ? AND ga.id = ? AND a.startDate= ? GROUP BY a.a HAVING cRows > 1 AND cRows < 10 ORDER BY a.a DESC";
    check(sql.sql, exp);
    assertEquals(5, sql.paramaters.size());
  }

  @Test
  public void testPagination() throws InvalidCurrentPageException, InvalidSqlGenerationException {
    String endDate = "12/12/2021";
    String startDate = "1/01/2020";
    String gaId = "1223424";
    SqlParameter sql = baseQueryPaginated(baseQuery(), startDate, gaId, endDate, 10, 20);

    String exp = "SELECT last_name, first_name, age, a.startDateFROM table_a LEFT JOIN table_b as b ON table_b.a = table_a.b INNER JOIN table_c as c ON table_c.a = table_a.c RIGHT JOIN table_d as d ON table_d.a = table_c.a INNER JOIN group_account as ga ON ga.id = a.group_account_id WHERE table_a.a > ? AND table_b.b < 20 AND table_c.c > ? AND a.endDate = ? AND ga.id = ? AND a.startDate= ? GROUP BY a.a HAVING cRows > 1 AND cRows < 10 ORDER BY a.a DESC OFFSET 180 ROWS FETCH NEXT 20 ROWS ONLY";
    check(exp, sql.sql);
    assertEquals(5, sql.paramaters.size());
  }

  @Test
  public void testOrderBy() throws InvalidSqlGenerationException {
    SqlParameter act = new Selector().select("holidays").orderBy("date_of_holiday", true).getSqlAndParameters();
    String exp = "SELECT * FROM holidays ORDER BY date_of_holiday DESC";

    check(exp, act.sql);
    assertEquals(0, act.paramaters.size());
  }

  private void check(String exp, String act) {
    System.out.println("Exp: " + exp.toLowerCase());
    System.out.println("Act: " + act.toLowerCase());

    assertEquals(exp, act);
  }

}
