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

import java.util.List;

import com.github.str4ng3r.Constants.SqlDialect;
import com.github.str4ng3r.Join.JOIN;

/**
 *
 * @author Pablo Eduardo Martinez Solis
 */
abstract class QueryBuilder <T> {
  protected Constants constants = new Constants();
  protected Parameter parameter = new Parameter();
  protected WhereHaving where;
  protected Tables tables;
  T t;

  QueryBuilder() {
    initialize();
  }

  private void initialize (){
    this.where = new WhereHaving(" WHERE ", this.parameter);
  }

  protected void setReferenceObject (T t){
    this.t = t;
  }

  public Parameter addParameter(String column, String value) {
    return this.parameter.addParameter(column, value);
  }

  public Parameter addParameter(String column, String value, boolean b) {
    return this.parameter.addParameter(column, value, b);
  }

  abstract protected String write();

  /**
   * Generate SQL Statement with paramaters
   *
   * @return SqlParameter
   */
  public SqlParameter getSqlAndParameters() {
    String sql = this.write();

    List<String> orderParameters = parameter.sortParameters(parameter.getIndexesOfOcurrences(sql));
    sql = parameter.replaceParamatersOnSql(sql);

    return new SqlParameter(sql, orderParameters);
  }

  /**
   * This should init from 
   * 
   * @param criteria
   * @param parameters
   *
   * @return same object as pipe
   */
  public T from (String...tableNames) {
    this.tables.from(tableNames);
    return t;
  }

  /**
   * This initialize the where (If there's any previous filter criteria should be
   * reset)
   *
   * @param criteria
   * @param parameters
   *
   * @return same object as pipe
   */
  public T where (String criteria, Parameter... parameters) {
    this.where.addCriteria(criteria, parameters);
    return t;
  }

  /**
   * Add more filter criteria to previous criteria
   *
   * @param criteria
   * @param parameters
   *
   * @return same object as pipe
   */
  public T andWhere(String criteria, Parameter... parameters) {
    this.where.andAddCriteria(criteria, parameters);
    return t;
  }

  /**
   * Set valid SQL Synthax to generate SQL according to different types of
   * databases
   *
   * @param sqlDialect An enum of supported databases
   *
   * @return same object as pipe
   */
  public T setDialect(SqlDialect sqlDialect) {
    constants.setDialect(sqlDialect);
    return t;
  }

  /**
   * Add join to statement
   *
   * @param join      An valid enum of join types
   * @param tableName A source to join could be a query or table
   * @param on        Login to join tables
   *
   * @return same object as pipe
   */
  public T join(JOIN join, String tableName, String on) {
    tables.addJoin(join, tableName, on);
    return t;
  }

}
