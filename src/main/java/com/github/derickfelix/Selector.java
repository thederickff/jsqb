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

import com.github.derickfelix.Tables.JOIN;

/**
 * @author derickfelix
 */
public class Selector {

  private Tables tables = new Tables();
  private OrderGroupBy orderBy = new OrderGroupBy();
  private OrderGroupBy groupBy = new OrderGroupBy();
  private Parameter parameter = new Parameter();

  private WhereHaving where;
  private WhereHaving having;

  Selector() {
    initialize();
  }

  private void initialize() {
    this.where = new WhereHaving(" WHERE ", parameter);
    this.having = new WhereHaving(" HAVING ", parameter);
    this.orderBy = null;
    this.groupBy = null;
  }

  public Selector select(String tableName, String... fields) {
    this.tables.addTable(tableName, fields);
    return this;
  }

  public Selector addSelect(String... fields) {
    this.tables.addFields(fields);
    return this;
  }

  public Selector join(JOIN join, String tableName, String on) {
    tables.addJoin(join, tableName, on);
    return this;
  }

  public Selector orderBy(String orderBy, boolean descending) {
    this.orderBy = new OrderGroupBy();
    this.orderBy.orderBy(orderBy, descending);
    return this;
  }

  public Selector groupBy(String columns) {
    this.groupBy = new OrderGroupBy();
    this.groupBy.groupBy(columns);
    return this;
  }

  public Selector having(String criteria, Parameter... parameters) {
    this.having.addCriteria(criteria, parameters);
    return this;
  }

  public Selector andHaving(String criteria, Parameter... parameters) {
    this.having.andAddCriteria(criteria, parameters);
    return this;
  }

  public Selector where(String criteria, Parameter... parameters) {
    this.where.addCriteria(criteria, parameters);
    return this;
  }

  public Selector andWhere(String criteria, Parameter... parameters) {
    this.where.andAddCriteria(criteria, parameters);
    return this;
  }

  private String write() {
    StringBuilder sql = this.tables.write();

    this.where.write(sql);

    if (this.groupBy != null)
      sql.append(" GROUP BY ").append(this.groupBy.write());

    this.having.write(sql);

    if (this.orderBy != null)
      sql.append(" ORDER BY ").append(this.orderBy.write());

    return sql.toString();
  }

  public SqlParameter getSqlAndParameters() {
    String sql = this.write();

    List<String> orderParameters = parameter.sortParameters(parameter.getIndexesOfOcurrences(sql));
    sql = parameter.replaceParamatersOnSql(sql);

    return new SqlParameter(sql, orderParameters);
  }

}
