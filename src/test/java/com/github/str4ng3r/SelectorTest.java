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

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.str4ng3r.Join.JOIN;

/**
 *
 * @author Pablo Eduardo Martinez Solis
 */
public class SelectorTest {

  private final Selector jsqb;

  public SelectorTest() {
    this.jsqb = new Selector();
  }

  @BeforeClass
  public static void setUpClass() {
  }

  @AfterClass
  public static void tearDownClass() {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testDelete (){
    new Delete()
    .from("users u")
    .join(JOIN.INNER, "additional_properties ap", "u.id = ap.user_id")
    .where("ap.need_to_delete = TRUE")
    .getSqlAndParameters();
  }

  Selector baseQuery() {
    return new Selector()
        .select("table_a as a", "COUNT(a.a) as cRows", "a.b", "a.c", "b.a", "c.a",
            "IF (:endDate > '12/10/2022', 'HOla', 'fdadfs')", ":gaId")
        .join(JOIN.LEFT, "table_b as b", "table_b.a = table_a.b")
        .join(JOIN.INNER, "table_c as c", "table_c.a = table_a.c")
        .join(JOIN.RIGHT, "table_d as d", "table_d.a = table_c.a")
        .where("table_a.a > :lowerValue AND table_b.b < 20 AND table_c.c > :upperValue",
            jsqb.addParameter("lowerValue", "12"),
            jsqb.addParameter("upperValue", "15"))
        .orderBy("a.a", true)
        .groupBy("a.a")
        .having("cRows > 1")
        .setDialect(Constants.SqlDialect.Oracle)
        .andHaving("cRows < 10");
  }

  @Test
  public void testMultipleInnerJoins() {
    System.out.println("MULTIPLE INNER JOINS");

    String endDate = "12/12/2021";
    String startDate = "1/01/2020";
    String gaId = "1223424";

    SqlParameter sql = testBaseQuery(baseQuery(), startDate, gaId, endDate, null, null);
    System.out.println(sql);
    sql = testBaseQuery(baseQuery(), startDate, gaId, endDate, 10, 20);
    System.out.println(sql);
    sql = testBaseQuery(baseQuery().setDialect(Constants.SqlDialect.Mysql), startDate, gaId, endDate, 10, 20);
    System.out.println(sql);
  }

  SqlParameter testBaseQuery(Selector selector, String startDate, String gaId, String endDate, Integer page,
      Integer pageSize) {
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
      System.out.println(sql);

      // Execute this...
      selector.getCount(sql.sql);

      Integer count = 1000;
      selector.getPagination(sql, new Pagination(pageSize, count, page));
    }

    return selector.getSqlAndParameters();
  }

  @Test
  public void testOrderBy() {
    SqlParameter act = jsqb.select("holidays").orderBy("date_of_holiday", true).getSqlAndParameters();
    System.out.println(act.sql);
    System.out.println(act.paramaters);

    // check(exp, act);
  }

  private void check(String exp, String act) {
    System.out.println("Exp: " + exp.toLowerCase());
    System.out.println("Act: " + act.toLowerCase());

    assertEquals(exp, act);
  }

}
