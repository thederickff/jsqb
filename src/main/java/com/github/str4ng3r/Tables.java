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
package com.github.str4ng3r;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.str4ng3r.Join.JOIN;

/**
 *
 * @author Pablo Eduardo Martinez Solis
 */
class Tables {
  private List<String> fields;
  private List<Table> tables;

  Tables() {
    this.fields = new ArrayList<>();
    this.tables = new ArrayList<>();
  }

  public void addFields(String... fields) {
    this.fields.addAll(Arrays.asList(fields));
  }

  public void addTable(String name, String... fields) {
    this.fields.clear();
    this.tables.add(new Table(name));
    this.addFields(fields);
  }

  public void addJoin(JOIN join, String name, String on) {
    this.tables.add(new Table(join.joinOpt, name, on));
  }

  private void fillWithFields(StringBuilder sql) {
    int lastElement = this.fields.size() - 1;
    for (int i = 0; i < lastElement; i++) {
      String field = this.fields.get(i);
      sql.append(field).append(", ");
    }
    sql.append(fields.get(lastElement)).append(" ");
  }

  public StringBuilder write() {
    StringBuilder sql = new StringBuilder();
    if (getTables().size() == 0)
      return sql.append("Not valid sql");

    sql.append("SELECT ");

    if (fields.size() > 1) {
      fillWithFields(sql);
    } else {
      sql.append(" * ");
    }

    sql.append(" FROM ").append(tables.get(0).name);

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
