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
  private List<String> having;
  private List<String> fields;
  private HashMap<String, String> parametersWhere;
  private HashMap<String, String> parametersHaving;
  private String orderBy;
  private String groupBy;
  private boolean firstWhere;
  private boolean firstHaving;
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
    this.having = new ArrayList<>();
    this.fields = new ArrayList<>();
    this.parametersWhere = new HashMap<>();
    this.parametersHaving = new HashMap<>();
    this.orderBy = null;
    this.groupBy = null;
    this.firstWhere = true;
    this.firstHaving = true;
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
      List<String> parameterToRemove = getParameters(this.where);
      filterParameter(parameterToRemove, this.parametersWhere);
    }
    firstWhere = false;
    this.where.add(" WHERE " + where);
    return this;
  }

  public Jsqb andWhere(String where, Parameter... parameters) {
    where = this.firstWhere ? where : " AND " + where;
    firstWhere = false;
    this.where.add(where);
    return this;
  }

  public Jsqb having(String having, Parameter... parameters) {
    if (!this.firstHaving) {
      List<String> parameterToRemove = getParameters(this.having);
      filterParameter(parameterToRemove, this.parametersHaving);
    }
    firstHaving = false;
    this.having.add(" HAVING " + having);
    return this;
  }

  public Jsqb andHaving(String having, Parameter... parameters) {
    having = this.firstHaving ? having : " AND " + having;
    firstHaving = false;
    this.having.add(having);
    return this;
  }

  public Parameter createParameter(String column, String value, boolean appendQuotes) {
    if (appendQuotes)
      value = "\"" + value + "\"";
    return createParameter(column, value);
  }

  public Parameter createParameter(String column, String value) {
    this.parametersWhere.put(column, value);
    return new Parameter();
  }

  public List<String> getParameters(List<String> list) {
    ArrayList<String> p = new ArrayList<String>();
    for (String w : list) {
      Matcher m = this.pattern.matcher(w);
      if (m.find())
        p.add(m.group(0));
    }
    return p;
  }

  private void filterParameter(List<String> parameterToRemove, HashMap<String, String> dictionarie) {
    parameterToRemove.parallelStream().forEach(p -> dictionarie.remove(p));
  }

  public Jsqb orderBy(String orderBy, boolean descending) {
    this.orderBy = descending ? orderBy + " DESC" : orderBy;
    return this;
  }

  public Jsqb groupBy(String columns) {
    this.groupBy = columns;
    return this;
  }

  public String write() {
    if (tables.get(0).name == null)
      return "Not valid sql";

    StringBuilder sql = new StringBuilder();
    sql.append("SELECT ");

    boolean all = fields.size() > 1;

    if (all) {
      fillWithFields(sql);
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

    if (this.groupBy != null)
      sql.append(" GROUP BY ").append(this.groupBy);

    having.forEach(h -> sql.append(h));

    if (this.orderBy != null)
      sql.append(" ORDER BY ").append(this.orderBy);

    return sql.toString();
  }

  private void fillWithFields(StringBuilder sql) {
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
