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

import java.util.ArrayList;
import java.util.List;

/**
 * @author derickfelix
 */
public class JSqlQueryBuilder {

    private final List<Table> tables;
    private String with;

    public JSqlQueryBuilder()
    {
        this.tables = new ArrayList<>();
    }

    public JSqlQueryBuilder select(String tableName, String... fields)
    {
        Table table = new Table();
        table.name = tableName;
        table.fields = fields;
        tables.add(table);

        return this;
    }

    public JSqlQueryBuilder innerJoin(String tableName, String on, String... fields)
    {
        Table table = new Table();
        table.name = tableName;
        table.fields = fields;
        table.on = on;
        tables.add(table);

        return this;
    }

    public JSqlQueryBuilder with(String with)
    {
        this.with = with;

        return this;
    }

    public String write()
    {
        if (tables.get(0).name != null) {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT ");

            boolean all = true;

            for (int i = 0; i < tables.size(); i++) {
                Table table = tables.get(i);

                if (table.fields.length != 0) {
                    all = false;
                    break;
                }
            }

            if (all) {
                Table from = tables.get(0);
                sql.append(from.name).append(".* ");
            } else {
                for (int i = 0; i < tables.size(); i++) {
                    Table table = tables.get(i);

                    if (tables.size() == i + 1) {
                        fillWithFields(table, sql, true);
                    } else {
                        fillWithFields(table, sql, false);
                    }
                }
            }

            for (int i = 0; i < tables.size(); i++) {
                Table table = tables.get(i);

                if (i == 0) {
                    sql.append("FROM ")
                            .append(table.name);
                } else {
                    sql.append(" INNER JOIN ")
                            .append(table.name)
                            .append(" on ")
                            .append(table.on);
                }
            }

            if (with != null) {
                sql.append(" WHERE ").append(with);
            }

            return sql.toString();
        }

        return "";
    }

    private void fillWithFields(Table table, StringBuilder sql, boolean last)
    {
        String[] fields = table.fields;

        for (int i = 0; i < fields.length; i++) {
            String field = fields[i];

            sql.append(table.name)
                    .append('.')
                    .append(field);

            if (last && fields.length == i + 1) {
                sql.append(" ");
            } else {
                sql.append(", ");
            }
        }
    }

    private class Table {

        private String name;
        private String[] fields;
        private String on;

    }
}
