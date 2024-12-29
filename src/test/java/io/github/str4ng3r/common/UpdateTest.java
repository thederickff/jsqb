/*
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

import java.util.HashMap;

import org.junit.Test;

import io.github.str4ng3r.exceptions.InvalidSqlGenerationException;
import io.github.str4ng3r.common.Join.JOIN;

public class UpdateTest {

    Update baseUpdateTest() {
        return new Update()
                .from("users u")
                .excludeColumns("a.address")
                .join(JOIN.INNER, "address a", "u.id = a.user_id");
    }

    HashMap<String, String> testBaseData() {
        HashMap<String, String> dataToUpdate = new HashMap<String, String>();
        dataToUpdate.put("id", "200");
        dataToUpdate.put("u.name", "Pablo Eduardo");
        dataToUpdate.put("u.lastName0", "Martinez");
        dataToUpdate.put("u.lastName1", "Solis");
        return dataToUpdate;
    }

    @Test(expected = InvalidSqlGenerationException.class)
    public void failToUpdateExcludeColumn() throws InvalidSqlGenerationException {
        HashMap<String, String> data = testBaseData();
        Update u = baseUpdateTest();
        //u.andWhere("u.id = :id", u.addParameter("id", data.get("id")));
        data.put("a.address", "A beautiful city");
        data.forEach((k, v) -> u.addParameter(k, v));
        u.getSqlAndParameters();
    }

    @Test()
    public void update() throws InvalidSqlGenerationException {
        HashMap<String, String> data = testBaseData();
        Update u = baseUpdateTest();
        // u.andWhere("u.id = :id", u.addParameter("id", data.get("id")));
        data.forEach((k, v) -> u.addParameter(k, v));
        String exp = "UPDATE users u SET u.name = ?, u.lastName1 = ?, u.lastName0 = ? INNER JOIN address a ON u.id = a.user_id WHERE u.id = ?";
        check(exp, u.getSqlAndParameters().sql);
    }

    private void check(String exp, String act) {
        System.out.println("Exp: " + exp.toLowerCase());
        System.out.println("Act: " + act.toLowerCase());

        assertEquals(exp, act);
    }

}