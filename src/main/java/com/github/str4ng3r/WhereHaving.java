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
import java.util.List;
import java.util.regex.Matcher;

/**
 *
 * @author Pablo Eduardo Martinez Solis
 */
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
    this.firstCriteria = false;
    this.listFilterCriteria.add(criteria);
  }

  public void andAddCriteria(String criteria, Parameter... parameters) {
    String c = this.firstCriteria ? criteria : " AND " + criteria;
    this.listFilterCriteria.add(c);
    this.firstCriteria = false;
  }

  public StringBuilder write(StringBuilder sql) {
    if (listFilterCriteria.isEmpty())
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
