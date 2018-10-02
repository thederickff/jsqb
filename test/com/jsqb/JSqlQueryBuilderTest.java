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
package com.jsqb;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
        String act = jsqb.select().from("users").write();
        
        check(exp, act);
    }
    
    @Test
    public void testSelect()
    {
        System.out.println("SELECT");
        JSqlQueryBuilder jsqb = new JSqlQueryBuilder();
        
        String exp = "SELECT users.user_id, users.name, users.email FROM users";
        String act = jsqb.select("user_id", "name", "email").from("users").write();

        check(exp, act);
    }
    
    @Test
    public void testAliasedSelect()
    {
        System.out.println("ALIASED SELECT");
        
        JSqlQueryBuilder jsqb = new JSqlQueryBuilder();

        String exp = "SELECT users.user_id AS userId, users.name AS username, users.email AS email FROM users";
        String act = jsqb.select("user_id AS userId", "name AS username", "email AS email").from("users").write();
        
        check(exp, act);
    }
    
    private void check(String exp, String act)
    {
        System.out.println("Exp: " + exp);
        System.out.println("Act: " + act);
        
        assertEquals(exp, act);
    }
}
