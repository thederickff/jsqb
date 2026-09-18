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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.str4ng3r.exceptions.InvalidSqlGenerationException;

/**
 * @author Pablo Eduardo Martinez Solis
 */
class Tables {
    private List<String> fields;
    private List<Table> tables;
    private ACTIONSQL action;

    public static enum ACTIONSQL {
        DELETE("DELETE "), UPDATE("UPDATE "), SELECT("SELECT ");

        public String action;

        ACTIONSQL(String action) {
            this.action = action;
        }
    }

    public Tables(ACTIONSQL action) {
        this.action = action;
        this.fields = new ArrayList<>();
        this.tables = new ArrayList<>();
    }

    public void addFields(String... fields) {
        this.fields.addAll(Arrays.asList(fields));
    }

    public void from(String... tableNames) {
        if (tableNames == null || tableNames.length == 0)
            throw new IllegalArgumentException("At least one table name is required");
        for (String t : tableNames) {
            if (t == null || t.trim().isEmpty())
                throw new IllegalArgumentException("Table name must not be null or empty");
            this.tables.add(new Table(t));
        }
    }

    public void addTable(String tableName, String... fields) {
        if (tableName == null || tableName.trim().isEmpty())
            throw new IllegalArgumentException("Table name must not be null or empty");
        this.fields.clear();
        this.tables.add(new Table(tableName));
        this.addFields(fields);
    }

    public void addJoin(Join join, String name, String on) {
        if (join == null)
            throw new IllegalArgumentException("Join type must not be null");
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Join table name must not be null or empty");
        this.tables.add(new Table(join.joinOpt, name, on));
    }

    private void addSeparator(List<String> list, StringBuilder sql) {
        sql.append(String.join(", ", list).concat(" "));
    }

    public StringBuilder write() throws InvalidSqlGenerationException {
        StringBuilder sql = new StringBuilder();
        if (getTables().isEmpty())
            throw new InvalidSqlGenerationException("Tables array is empty, so it could not generate the query");

        // Las tablas base son las que no provienen de un join (join == null); se
        // unen con coma en el FROM. Las tablas de join se emiten aparte con su
        // propia cláusula (INNER/LEFT/... JOIN ... ON ...).
        List<String> baseTables = new ArrayList<>();
        for (Table t : tables)
            if (t.join == null)
                baseTables.add(t.name);

        if (baseTables.isEmpty())
            throw new InvalidSqlGenerationException("No base table was provided for the query");

        String baseFrom = String.join(", ", baseTables);

        sql.append(this.action.action);

        if (this.action == ACTIONSQL.SELECT) {
            if (fields.isEmpty()) sql.append("* ");
            else addSeparator(fields, sql);
            sql.append("FROM ");
            sql.append(baseFrom);
        } else if (this.action == ACTIONSQL.DELETE) {
            sql.append("FROM ");
            sql.append(baseFrom);
        } else if (this.action == ACTIONSQL.UPDATE) {
            sql.append(baseFrom);
            sql.append(" SET ");
            addSeparator(fields, sql);
        }

        for (Table table : tables) {
            if (table.join == null)
                continue; // ya incluida en el FROM
            sql.append(table.join).append(table.name);
            // CROSS JOIN (o cualquier join sin condición) no debe generar 'ON' colgante
            if (table.on != null && !table.on.trim().isEmpty())
                sql.append(" ON ").append(table.on);
        }

        return sql;
    }

    private class Table {
        private String name;
        private String on;
        private String join;

        public Table(String name) {
            this.name = name;
        }

        public Table(String join, String name, String on) {
            this(name);
            this.join = join;
            this.on = on;
        }
    }

    public List<String> getFields() {
        return fields;
    }

    public List<Table> getTables() {
        return tables;
    }
}