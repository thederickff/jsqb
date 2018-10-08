/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
public class JSqlQueryBuilderTest {
    
    public JSqlQueryBuilderTest()
    {
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
    
    /**
     * Test of select method, of class JsqlQueryBuilder.
     */
    @Test
    public void testSelectAll()
    {
        System.out.println("SELECT ALL");
        JSqlQueryBuilder jsqb = new JSqlQueryBuilder();

        String exp = "SELECT users.* FROM users";
        String act = jsqb.select("users").write();

        check(exp, act);
    }

    @Test
    public void testSelect()
    {
        System.out.println("SELECT");
        JSqlQueryBuilder jsqb = new JSqlQueryBuilder();

        String exp = "SELECT users.user_id, users.name, users.email FROM users";
        String act = jsqb.select("users", "user_id", "name", "email").write();

        check(exp, act);
    }

    @Test
    public void testAliasedSelect()
    {
        System.out.println("ALIASED SELECT");

        JSqlQueryBuilder jsqb = new JSqlQueryBuilder();

        String exp = "SELECT users.user_id AS userId, users.name AS username, users.email AS email FROM users";
        String act = jsqb.select("users", "user_id AS userId", "name AS username", "email AS email").write();

        check(exp, act);
    }

    @Test
    public void testInnerJoin()
    {
        System.out.println("INNER JOIN");
        JSqlQueryBuilder jsqb = new JSqlQueryBuilder();

        String exp = "SELECT users.user_id, users.name, users.email, roles.name FROM users INNER JOIN roles on roles.id = users.role_id";
        String act = jsqb.select("users", "user_id", "name", "email")
                .innerJoin("roles", "roles.id = users.role_id", "name").write();

        check(exp, act);
    }

    @Test
    public void testMultipleInnerJoins()
    {
        System.out.println("MULTIPLE INNER JOINS");
        JSqlQueryBuilder jsqb = new JSqlQueryBuilder();

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
                .with("table_a.a > 10 AND table_b.b < 20 AND table_c.c > 15")
                .write();

        check(exp, act);
    }

    private void check(String exp, String act)
    {
        System.out.println("Exp: " + exp.toLowerCase());
        System.out.println("Act: " + act.toLowerCase());

        assertEquals(exp, act);
    }
    
}
