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

import org.junit.Test;

import io.github.str4ng3r.exceptions.InvalidCurrentPageException;
import io.github.str4ng3r.exceptions.InvalidSqlGenerationException;
import io.github.str4ng3r.common.Join.JOIN;

import java.util.function.Consumer;

/**
 * @author Pablo Eduardo Martinez Solis
 */
public class SelectorTest {

    public static void testPagination(Selector selector, String startDate, String endDate,
                                      Integer page, Integer pageSize,
                                      Consumer<Selector> cb

    ) throws InvalidCurrentPageException, InvalidSqlGenerationException {
        if (endDate != null)
            selector.andWhere("u.endDate = :endDate", parameters -> parameters.put("endDate", endDate));

        if (startDate != null)
            selector.andWhere("u.startDate= :startDate", parameters -> parameters.put("startDate", startDate));

        SqlParameter sql = selector.getSqlAndParameters();

        cb.accept(selector);
        // Execute this...
//        selector.getCount(sql.sql);
//        Integer count = 1000;
//        selector.setPagination(sql, new Pagination(pageSize, count, page));
    }


    public static Selector baseQueryUsers(String name, String lastName, String cp) {
        Selector s = new Selector()
                .select("users as u",
                        "u.id id", "u.name name", "u.email email", "u.role role",
                        "u.email as email"
                )
                .join(JOIN.LEFT, "userAddress as ua", "u.id = ua.userId")
                .join(JOIN.INNER, "addresses as a", "a.id = ua.addressId")
                .setDialect(Constants.SqlDialect.Postgres);

        if (name != null)
            s.andWhere("u.name LIKE  CONCAT('%', :name, '%')", parameters -> parameters.put("name", name));

        if (lastName != null)
            s.andWhere("u.lastName LIKE CONCAT('%', :lastName, '%')", parameters -> parameters.put("lastName", lastName));

        if (cp != null)
            s.andWhere("a.cp = :cp", parameters -> parameters.put("cp", cp));

        return s;
    }

    public static Selector baseQueryShops() {
        Selector s = new Selector();
        return s.select("user u", "u.id", "u.name", "u.email", "u.role ")
                .join(JOIN.LEFT, "userShop as us", "u.id = us.userId")
                .join(JOIN.INNER, "shops as s", "s.id = us.shopId")
                .setDialect(Constants.SqlDialect.Postgres);
    }

}
