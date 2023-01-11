package com.github.derickfelix;

public class OrderGroupBy {
  String orderBy;

  public void orderBy(String orderBy, boolean descending) {
    this.orderBy = descending ? orderBy + " DESC" : orderBy;
  }

  public void groupBy(String groupBy) {
    this.orderBy = groupBy;
  }

  public String write() {
    return orderBy;
  }
}
