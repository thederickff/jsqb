package com.github.str4ng3r;

public class Pagination {
  Integer pageSize;
  Integer count;
  Integer currentPage;

  public Pagination(Integer pageSize, Integer count, Integer currentPage) {
    this.pageSize = pageSize;
    this.count = count;
    this.currentPage = currentPage;
  }
}
