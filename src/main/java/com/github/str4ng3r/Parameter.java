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
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Pablo Eduardo Martinez Solis
 */
public final class Parameter {
  public static String p = "(:[a-zA-Z0-9]+)";
  public static Pattern pattern = Pattern.compile(p);
  private HashMap<String, String> parameters = new HashMap<>();

  public Parameter addParameter(String column, String value) {
    this.parameters.put(":" + column, value);
    return this;
  }

  public Parameter addParameter(String column, String value, boolean b) {
    if (b)
      value = "\"" + value + "\"";
    this.parameters.put(":" + column, value);
    return this;
  }

  public List<String> getIndexesOfOcurrences(String sql) {
    List<String> indexes = new ArrayList<>();
    Matcher m = Parameter.pattern.matcher(sql);
    while (m.find())
      indexes.add(m.group());
    return indexes;
  }

  String replaceParamatersOnSql(String sql) {
    return sql.replaceAll(p, "?");
  }

  String setParameter(String sql, String[] parameters) {
    Matcher m = Parameter.pattern.matcher(sql);
    int c = 0;
    while (m.find())
      m.replaceFirst(parameters[c++]);
    return sql;
  }

  List<String> sortParameters(List<String> indexes) {
    List<String> sortedParameters = new ArrayList<>();

    indexes.stream().forEach(p -> {
      if (this.parameters.containsKey(p))
        sortedParameters.add(this.parameters.get(p));
    });

    return sortedParameters;
  }

  /**
   * Filter parameters
   *
   * @param parameterToRemove [TODO:description]
   */
  void filterParameter(List<String> parameterToRemove) {
    parameterToRemove.parallelStream().forEach(p -> parameters.remove(p));
  }
}
