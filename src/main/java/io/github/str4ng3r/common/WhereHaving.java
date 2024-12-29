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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 *
 * @author Pablo Eduardo Martinez Solis
 */
final class WhereHaving {

  protected List<String> listFilterCriteria = new ArrayList<>();
  private String prefix;
  private Parameter parameter;

  WhereHaving(String prefix, Parameter parameter) {
    this.prefix = prefix;
    this.parameter = parameter;
  }

  public void removeAllCriterias() {
    List<String> parameterToRemove = getParametersMatch();
    parameter.filterParameter(parameterToRemove);
  }

  public void addCriteria(String criteria, Consumer<HashMap<String, String>> parameters) {
    removeAllCriterias();
    parameters.accept(this.parameter.parameters);
    this.listFilterCriteria.add(criteria);
  }

  public void andAddCriteria(String criteria, Consumer<HashMap<String, String>> parameters) {
    this.listFilterCriteria.add(criteria);
    parameters.accept(this.parameter.parameters);
  }

  public StringBuilder write(StringBuilder sql) {
    if (listFilterCriteria.isEmpty())
      return sql;
    sql.append(prefix);
    sql.append(String.join(" AND ", listFilterCriteria));
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
