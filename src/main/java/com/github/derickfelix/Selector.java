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
package com.github.derickfelix;

import com.github.derickfelix.Join.JOIN;

/**
 *
 * @author Pablo Eduardo Martinez Solis
 */
public class Selector extends QueryBuilder {

  private Tables tables = new Tables();
  private OrderGroupBy orderBy = new OrderGroupBy();
  private OrderGroupBy groupBy = new OrderGroupBy();

  private WhereHaving where;
  private WhereHaving having;

  Selector() {
    super();
    initialize();
  }

  private void initialize() {
    this.where = new WhereHaving(" WHERE ", this.parameter);
    this.having = new WhereHaving(" HAVING ", this.parameter);
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

  @Override
  protected String write() {
    StringBuilder sql = this.tables.write();

    this.where.write(sql);

    if (this.groupBy != null)
      sql.append(" GROUP BY ").append(this.groupBy.write());

    this.having.write(sql);

    if (this.orderBy != null)
      sql.append(" ORDER BY ").append(this.orderBy.write());

    return sql.toString();
  }

}
