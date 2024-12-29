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

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import io.github.str4ng3r.exceptions.InvalidSqlGenerationException;
import io.github.str4ng3r.common.Constants.SqlDialect;
import io.github.str4ng3r.common.Join.JOIN;

/**
 *
 * @author Pablo Eduardo Martinez Solis
 */
abstract class QueryBuilder<T> {
  protected Constants constants = new Constants();
  protected Parameter parameter = new Parameter();
  protected WhereHaving where = new WhereHaving(" WHERE ", this.parameter);
  protected Tables tables;
  protected T t;

  QueryBuilder() {}

  protected void setReferenceObject(T t) {
    this.t = t;
  }

  public Parameter addParameter(String column, String value) {
    return this.parameter.addParameter(column, value);
  }

  /**
   * Build query
   *
   * @return Sql
   *
   * @throws InvalidSqlGenerationException
   */
  abstract protected String write() throws InvalidSqlGenerationException;

  /**
   * Generate SQL Statement with paramaters as an array
   *
   * @return SqlParameter
   * @throws InvalidSqlGenerationException 
   */
  public SqlParameter getSqlAndParameters() throws InvalidSqlGenerationException {
    String sql = this.write();

    List<Object> orderParameters = parameter.sortParameters(parameter.getIndexesOfOccurrences(sql));

    return new SqlParameter(parameter.getSql(), orderParameters);
  }

  /**
   * Generate SQL Statement with paramaters as an dictionary (this is a little bit more fast)
   *
   * @return SqlParameter
   * @throws InvalidSqlGenerationException 
   */
  public SqlParameter getSqlAndParametersDictionarie() throws InvalidSqlGenerationException {
    String sql = this.write();
    return new SqlParameter(sql, parameter.parameters);
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
  public T where(String criteria, Consumer<HashMap<String, String>> parameters) {
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
  public T andWhere(String criteria, Consumer<HashMap<String, String>> parameters) {
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

  /**
   * Add cross join to statement
   *
   * @param tableName A source to join could be a query or table
   *
   * @return same object as pipe
   */
  public T crossJoin(String tableName) {
    tables.addJoin(JOIN.CROSS, tableName, "");
    return t;
  }

  /**
   * Add table
   *
   * @param tableName A source to join could be a query or table
   *
   * @return same object as pipe
   */
  public T addFrom(String tableName) {
    tables.from(tableName);
    return t;
  }
}
