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


/**
 *
 * @author derickfelix
 */
public class JsqbTest {
    
    private final Jsqb jsqb;
    
    public JsqbTest()
    {
        this.jsqb = new Jsqb();
    }
    
    @BeforeClass
    public static void setUpClass()
    {
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp()
    {
    }
    
    @After
    public void tearDown()
    {
    }

    @Test
    public void testSelectAll()
    {
        System.out.println("SELECT ALL");

        String exp = "SELECT users.* FROM users";
        String act = jsqb.select("users").write();

        check(exp, act);
    }

    @Test
    public void testSelect()
    {
        System.out.println("SELECT");
        
        String exp = "SELECT users.user_id, users.name, users.email FROM users";
        String act = jsqb.select("users", "user_id", "name", "email").write();

        check(exp, act);
    }

    @Test
    public void testAliasedSelect()
    {
        System.out.println("ALIASED SELECT");

        String exp = "SELECT users.user_id AS userId, users.name AS username, users.email AS email FROM users";
        String act = jsqb.select("users", "user_id AS userId", "name AS username", "email AS email").write();

        check(exp, act);
    }

    @Test
    public void testInnerJoin()
    {
        System.out.println("INNER JOIN");
        
        String exp = "SELECT users.user_id, users.name, users.email, roles.name FROM users INNER JOIN roles on roles.id = users.role_id";
        String act = jsqb.select("users", "user_id", "name", "email")
                .innerJoin("roles", "roles.id = users.role_id", "name").write();

        check(exp, act);
    }

    @Test
    public void testMultipleInnerJoins()
    {
        System.out.println("MULTIPLE INNER JOINS");
        
        String exp = "SELECT "
                + "table_a.a, table_a.b, table_a.c, "
                + "table_b.a, table_b.b, table_b.c, "
                + "table_c.a, table_c.b, table_c.c "
                + "FROM table_a "
                + "INNER JOIN table_b on table_b.a = table_a.b "
                + "INNER JOIN table_c on table_c.a = table_a.c "
                + "WHERE table_a.a > 10 AND table_b.b < 20 AND table_c.c > 15";

        String act = jsqb.select("table_a", "a", "b", "c")
                .innerJoin("table_b", "table_b.a = table_a.b", "a", "b", "c")
                .innerJoin("table_c", "table_c.a = table_a.c", "a", "b", "c")
                .where("table_a.a > 10 AND table_b.b < 20 AND table_c.c > 15")
                .write();

        check(exp, act);
    }
    
    @Test
    public void testMultipleUsesOfJsqb()
    {
        System.out.println("MULTIPLE USES OF JSQB");

        String sql = jsqb.select("users", "id", "name", "role").where("id = :id, and create_at > :date").write();
        
        System.out.println(sql);
        
        String sql2 = jsqb.select("users", "id", "name", "role").write();
        
        System.out.println(sql2);
    }
    
    @Test
    public void testOrderBy()
    {
        System.out.println("TEST ORDER BY");
        String exp = "SELECT holidays.* FROM holidays ORDER BY date_of_holiday DESC";
        
        String act = jsqb.select("holidays").orderBy("date_of_holiday", true).write();
        
        check(exp, act);
    }

    private void check(String exp, String act)
    {
        System.out.println("Exp: " + exp.toLowerCase());
        System.out.println("Act: " + act.toLowerCase());

        assertEquals(exp, act);
    }
    
}
