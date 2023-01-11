package com.github.derickfelix;

import java.util.List;

abstract class QueryBuilder {
  protected Constants constants = new Constants();
  protected Parameter parameter = new Parameter();

  QueryBuilder() {
  }

  public Parameter addParameter(String column, String value) {
    return this.parameter.addParameter(column, value);
  }

  public Parameter addParameter(String column, String value, boolean b) {
    return this.parameter.addParameter(column, value, b);
  }

  abstract protected String write();

  public SqlParameter getSqlAndParameters() {
    String sql = this.write();

    List<String> orderParameters = parameter.sortParameters(parameter.getIndexesOfOcurrences(sql));
    sql = parameter.replaceParamatersOnSql(sql);

    return new SqlParameter(sql, orderParameters);
  }
}
