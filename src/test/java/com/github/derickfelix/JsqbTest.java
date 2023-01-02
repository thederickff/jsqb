/*
 * Copyright 2018 derickfelix.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.derickfelix;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.derickfelix.Jsqb.JOIN;

/**
 *
 * @author derickfelix
 */
public class JsqbTest {

  private final Jsqb jsqb;

  public JsqbTest() {
    this.jsqb = new Jsqb();
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
  public void testMultipleInnerJoins() {
    System.out.println("MULTIPLE INNER JOINS");

    String endDate = "12/12/2021";
    String startDate = null;
    String gaId = "1223424";

    jsqb
        .select("table_a as a", "COUNT(a.a) as cRows", "a.b", "a.c", "b.a", "c.a")
        .join(JOIN.LEFT, "table_b as b", "table_b.a = table_a.b")
        .join(JOIN.INNER, "table_c as c", "table_c.a = table_a.c")
        .join(JOIN.RIGHT, "table_d as d", "table_d.a = table_c.a")
        .where("table_a.a > :lowerValue AND table_b.b < 20 AND table_c.c > :upperValue",
            jsqb.createParameter("lowerValue", "12"),
            jsqb.createParameter("upperValue", "15"))
        .orderBy("a.a", true)
        .groupBy("a.a");

    if (endDate != null)
      jsqb.addSelect("a.startDate")
          .andWhere("a.endDate = :endDate", jsqb.createParameter("endDate", endDate, true));

    if (gaId != null)
      jsqb.join(JOIN.INNER, "group_account as ga", "ga.id = a.group_account_id")
          .andWhere("ga.id = :gaId", jsqb.createParameter("gaId", gaId));

    if (startDate != null)
      jsqb.andWhere("a.startDate= :startDate", jsqb.createParameter("startDate", startDate, true));

    jsqb.having("cRows > 1");
    jsqb.andHaving("cRows < 10");
    String act = jsqb.write();
    System.out.println(act);

  }

  @Test
  public void testOrderBy() {
    // System.out.println("TEST ORDER BY");
    // String exp = "SELECT holidays.* FROM holidays ORDER BY date_of_holiday DESC";

    String act = jsqb.select("holidays").orderBy("date_of_holiday", true).write();
    System.out.println(act);

    // check(exp, act);
  }

  private void check(String exp, String act) {
    System.out.println("Exp: " + exp.toLowerCase());
    System.out.println("Act: " + act.toLowerCase());

    assertEquals(exp, act);
  }

}
