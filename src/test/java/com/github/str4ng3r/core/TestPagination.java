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
package com.github.str4ng3r.core;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.str4ng3r.Pagination;
import com.github.str4ng3r.Selector;
import com.github.str4ng3r.SqlParameter;
import com.github.str4ng3r.exceptions.InvalidCurrentPageException;
import com.github.str4ng3r.exceptions.InvalidSqlGenerationException;

public class TestPagination {

  @Test
  public void testPagination() throws InvalidCurrentPageException, InvalidSqlGenerationException {
    Selector s = new Selector().select("test t");
    SqlParameter sql = s.getSqlAndParameters();
    s.getPagination(sql, new Pagination(10, 2629, 1));

  }

}
