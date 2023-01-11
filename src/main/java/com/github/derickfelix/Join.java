package com.github.derickfelix;

public class Join {
  public static enum JOIN {
    INNER(" INNER JOIN "), LEFT(" LEFT JOIN "), RIGHT(" RIGHT JOIN ");

    public String joinOpt;

    private JOIN(String joinOpt) {
      this.joinOpt = joinOpt;
    }
  }
}
