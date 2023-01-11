package com.github.derickfelix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parameter {
  public static String p = "(:[a-zA-Z0-9]+)";
  public static Pattern pattern = Pattern.compile(p, Pattern.CASE_INSENSITIVE);
  public HashMap<String, String> parameters;

  Parameter() {
    parameters = new HashMap<>();
  }

  Parameter(String column, String value) {
    this.parameters.put(":" + column, value);
  }

  public Parameter(String column, String value, boolean b) {
    if (b)
      value = "\"" + value + "\"";
    this.parameters.put(":" + column, value);
  }

  public List<String> getIndexesOfOcurrences(String sql) {
    List<String> indexes = new ArrayList<>();
    Matcher m = Parameter.pattern.matcher(sql);
    while (m.find())
      indexes.add(m.group());
    return indexes;
  }

  public String replaceParamatersOnSql(String sql) {
    return sql.replaceAll(p, "?");
  }

  public List<String> sortParameters(List<String> indexes) {
    List<String> sortedParameters = new ArrayList<>();

    indexes.stream().forEach(p -> {
      if (this.parameters.containsKey(p))
        sortedParameters.add(p);
    });

    return sortedParameters;
  }

  public void filterParameter(List<String> parameterToRemove) {
    parameterToRemove.parallelStream().forEach(p -> parameters.remove(p));
  }

}
