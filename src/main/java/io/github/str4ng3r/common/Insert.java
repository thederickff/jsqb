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

import io.github.str4ng3r.exceptions.InvalidSqlGenerationException;

import java.util.ArrayList;
import java.util.List;

public class Insert {

    StringBuilder columns;
    List<Object> values;

    StringBuilder valuesQuestion;

    String table;
    private int columnsCount = 0;

    public Insert() {
        columns = new StringBuilder();
        values = new ArrayList<>();
        valuesQuestion = new StringBuilder();
    }

    public Insert(String table) {
        this();
        this.table = table;
    }

    public Insert setColumns(String... columns) {
        this.columns.append(String.join(",", columns));
        this.columnsCount = columns.length;
        return this;
    }

    public Insert setValues(Object... values) {
        int lastElement = values.length - 1;
        for (int i = 0; i < lastElement; i++) {
            valuesQuestion.append("?,");
            this.values.add(values[i]);
        }
        valuesQuestion.append("?");
        this.values.add(values[lastElement]);
        return this;
    }


    public Insert setTable(String table) {
        this.table = table;
        return this;
    }

    public String write() throws InvalidSqlGenerationException {
        if (table == null || table.isEmpty())
            throw new InvalidSqlGenerationException("Insert requires a table name");
        if (columnsCount != values.size())
            throw new InvalidSqlGenerationException(
                    "Column/value count mismatch: " + columnsCount + " columns but " + values.size() + " values");

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ")
                .append(table)
                .append(" ( ")
                .append(columns)
                .append(" ) ")
                .append(" VALUES (")
                .append(this.valuesQuestion)
                .append(" )");
        return sql.toString();
    }

    public String getSql() throws InvalidSqlGenerationException {
        return write();
    }

}
