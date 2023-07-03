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
package com.github.str4ng3r;

import java.util.HashMap;

import org.junit.Test;

import com.github.str4ng3r.Join.JOIN;
import com.github.str4ng3r.exceptions.InvalidSqlGenerationException;

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
        dataToUpdate.put("a.address", "Mexico City, Polanco #121");
        dataToUpdate.put("u.name", "Pablo Eduardo");
        dataToUpdate.put("u.lastName0", "Martinez");
        dataToUpdate.put("u.lastName1", "Solis");
        return dataToUpdate;
    }

    @Test
    public void testUnit() throws InvalidSqlGenerationException {
        // try {
            HashMap<String, String> data = testBaseData();
            Update u = baseUpdateTest();
            u.andWhere("u.id = :id", u.addParameter("id", data.get("id")));

            data.forEach((k, v) -> u.addParameter(k, v));
            System.out.println(u.getSqlAndParameters());
        // } catch (InvalidSqlGenerationException e) {
        //     // TODO Auto-generated catch block
        //     System.out.println(e.getMessage());
        //     e.printStackTrace();
        // }
    }
}