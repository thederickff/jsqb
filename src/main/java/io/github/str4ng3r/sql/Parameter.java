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
package io.github.str4ng3r.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author Pablo Eduardo Martinez Solis
 */
public final class Parameter {
  public static String p = "(:[a-zA-Z0-9\\.]+)";
  public static Pattern pattern = Pattern.compile(p);
  protected HashMap<String, String> parameters = new HashMap<>();

  public Parameter addParameter(String column, String value) {
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

  static String setParameter(String sql, String... parameters) {
    Matcher m = Parameter.pattern.matcher(sql);
    int c = 0, groupPosition = 1;
    StringBuffer sb = new StringBuffer();

    while (m.find() && c < parameters.length) {
      StringBuffer buf = new StringBuffer(m.group());
      buf.replace(m.start(groupPosition) - m.start(), m.end(groupPosition) - m.start(), parameters[c++]);
      m.appendReplacement(sb, buf.toString());
    }

    m.appendTail(sb);
    return sb.toString();
  }

  List<String> sortParameters(List<String> indexes) {
    return indexes.stream().filter(p -> this.parameters.containsKey(p)).map((p) -> this.parameters.get(p)).collect(Collectors.toList());
  }

  /**
   * Filter parameters
   *
   * @param parameterToRemove
   */
  void filterParameter(List<String> parameterToRemove) {
    parameterToRemove.parallelStream().forEach(p -> parameters.remove(p));
  }
}
