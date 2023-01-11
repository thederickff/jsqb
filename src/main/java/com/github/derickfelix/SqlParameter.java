package com.github.derickfelix;

import java.util.ArrayList;
import java.util.List;

public class SqlParameter {
  public String sql;
  public List<String> paramaters = new ArrayList<>();

  SqlParameter(String sql, List<String> paramaters) {
    this.sql = sql;
    this.paramaters = paramaters;
  }
}
