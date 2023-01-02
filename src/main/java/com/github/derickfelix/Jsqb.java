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

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author derickfelix
 */
public class Jsqb {

  private List<Table> tables;
  private List<String> where;
  private List<String> fields;
  private HashMap<String, String> parameters;
  private String orderBy;
  private boolean firstWhere;
  private Pattern pattern = Pattern.compile("WHERE :([a-z0-9])+", Pattern.CASE_INSENSITIVE);

  public static enum JOIN {
    INNER(" INNER JOIN "), LEFT(" LEFT JOIN "), RIGHT(" RIGHT JOIN ");

    public String joinOpt;

    private JOIN(String joinOpt) {
      this.joinOpt = joinOpt;
    }
  };

  private void initialize() {
    this.tables = new ArrayList<>();
    this.where = new ArrayList<>();
    this.fields = new ArrayList<>();
    this.parameters = new HashMap<>();
    this.orderBy = null;
    this.firstWhere = true;
  }

  public Jsqb select(String tableName, String... fields) {
    initialize();

    Table table = new Table();
    table.name = tableName;
    this.fields.addAll(Arrays.asList(fields));
    tables.add(table);

    return this;
  }

  public Jsqb addSelect(String... fields) {
    this.fields.addAll(Arrays.asList(fields));
    return this;
  }

  public Jsqb join(JOIN join, String tableName, String on) {
    Table table = new Table();
    table.join = join.joinOpt;
    table.name = tableName;
    table.on = on;
    tables.add(table);

    return this;
  }

  public Jsqb where(String where, Parameter... parameters) {
    if (!this.firstWhere) {
      List<String> parameterToRemove = getParametersFromWhere();
      filterParameter(parameterToRemove);
    }

    firstWhere = false;
    this.where.add(where);

    return this;
  }

  public Jsqb andWhere(String where, Parameter... parameters) {
    where = this.firstWhere ? where : "AND" + where;
    where.concat(where);
    firstWhere = false;
    return this;
  }

  public Parameter createParameter(String column, String value) {
    this.parameters.put(column, value);
    return new Parameter();
  }

  public List<String> getParametersFromWhere() {
    ArrayList<String> p = new ArrayList<String>();
    for (String w : this.where) {
      Matcher m = this.pattern.matcher(w);
      if (m.find())
        p.add(m.group(0));
    }
    return p;
  }

  private void filterParameter(List<String> parameterToRemove) {
    parameterToRemove.parallelStream().forEach(p -> this.parameters.remove(p));
  }

  public Jsqb orderBy(String orderBy, boolean descending) {
    this.orderBy = descending ? orderBy + " DESC" : orderBy;
    return this;
  }

  public String write() {
    // if (tables.get(0).name != null)
    // return "Not valid sql";

    StringBuilder sql = new StringBuilder();
    sql.append("SELECT ");

    boolean all = fields.size() > 1;

    if (all) {
      fillWithFields(sql, true);
    } else {
      sql.append("* ");
    }

    sql.append("FROM ").append(tables.get(0).name);

    for (int i = 1; i < tables.size(); i++) {
      Table table = tables.get(i);
      sql.append(table.join)
          .append(table.name)
          .append(" ON ")
          .append(table.on);
    }

    where.forEach(w -> sql.append(w));

    if (orderBy != null) {
      sql.append(" ORDER BY ").append(orderBy);
    }

    return sql.toString();
  }

  private void fillWithFields(StringBuilder sql, boolean last) {
    int lastElement = this.fields.size() - 1;
    for (int i = 0; i < lastElement; i++) {
      String field = this.fields.get(i);

      sql.append(field).append(", ");
    }

    if (lastElement > 0)
      sql.append(fields.get(lastElement)).append(" ");
  }

  public class Parameter {
  }

  private class Table {
    private String name;
    private String on;
    private String join;
  }
}
