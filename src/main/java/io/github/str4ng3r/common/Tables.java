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
import java.util.stream.Collectors;

import io.github.str4ng3r.exceptions.InvalidSqlGenerationException;
import io.github.str4ng3r.common.Join.JOIN;

/**
 *
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
  };

  public Tables(ACTIONSQL action) {
    this.action = action;
    this.fields = new ArrayList<>();
    this.tables = new ArrayList<>();
  }

  public void addFields(String... fields) {
    this.fields.addAll(Arrays.asList(fields));
  }

  public void from(String... tableNames) {
    for (String t : tableNames)
      this.tables.add(new Table(t));
  }

  public void addTable(String tableName, String... fields) {
    this.fields.clear();
    this.tables.add(new Table(tableName));
    this.addFields(fields);
  }

  public void addJoin(JOIN join, String name, String on) {
    this.tables.add(new Table(join.joinOpt, name, on));
  }

  private void addSeparator(List<String> list, StringBuilder sql) {
    sql.append(String.join(", ", list).concat(" "));
  }

  private void addSeparatorTables(List<Table> list, StringBuilder sql) {
    sql.append(
        list.stream().map(f -> f.name).collect(Collectors.joining(", ")));
  }

  public StringBuilder write() throws InvalidSqlGenerationException {
    StringBuilder sql = new StringBuilder();
    if (getTables().isEmpty())
      throw new InvalidSqlGenerationException("Tables array is empty, so it could not generate the query");

    sql.append(this.action.action);

    if (this.action == ACTIONSQL.SELECT) {
      if (fields.isEmpty())
        sql.append("* ");
      else
        addSeparator(fields, sql);
      sql.append("FROM ");
    } else if (this.action == ACTIONSQL.DELETE) {
      addSeparatorTables(tables, sql);
      sql.append("FROM ");
    } else if (this.action == ACTIONSQL.UPDATE) {
      sql.append(tables.get(0).name);
      sql.append(" SET ");
      addSeparator(fields, sql);
    }

    sql.append(tables.get(0).name);

    for (int i = 1; i < tables.size(); i++) {
      Table table = tables.get(i);
      sql.append(table.join)
          .append(table.name)
          .append(" ON ")
          .append(table.on);
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
  };
}
