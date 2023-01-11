package com.github.derickfelix;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

final class WhereHaving {

  private List<String> listFilterCriteria = new ArrayList<>();
  private boolean firstCriteria;
  private String prefix;
  private Parameter parameter;

  WhereHaving(String prefix, Parameter parameter) {
    this.prefix = prefix;
    this.parameter = parameter;
    this.firstCriteria = true;
  }

  public void addCriteria(String criteria, Parameter... parameters) {
    if (!this.firstCriteria) {
      List<String> parameterToRemove = getParametersMatch();
      parameter.filterParameter(parameterToRemove);
      this.firstCriteria = true;
    }
    this.listFilterCriteria.add(criteria);
  }

  public void andAddCriteria(String criteria, Parameter... parameters) {
    String c = this.firstCriteria ? criteria : " AND " + criteria;
    this.listFilterCriteria.add(c);
    this.firstCriteria = false;
  }

  public StringBuilder write(StringBuilder sql) {
    if (listFilterCriteria.size() < 1)
      return sql;
    sql.append(prefix);
    listFilterCriteria.forEach(w -> sql.append(w));
    return sql;
  }

  public List<String> getParametersMatch() {
    ArrayList<String> p = new ArrayList<String>();
    for (String w : listFilterCriteria) {
      Matcher m = Parameter.pattern.matcher(w);
      if (m.find())
        p.add(m.group(0));
    }
    return p;
  }

  public List<String> getListFilterCriteria() {
    return listFilterCriteria;
  }

}
