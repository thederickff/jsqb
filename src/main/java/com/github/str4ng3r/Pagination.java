package com.github.str4ng3r;

public class Pagination {
  Integer pageSize;
  Integer count;
  Integer currentPage;
  Integer totalPages;

  public Pagination(Integer pageSize, Integer count, Integer currentPage) {
    this.pageSize = pageSize;
    this.count = count;
    this.currentPage = currentPage;
  }

  @Override
  public String toString() {
    return "{\n\tpageSize: " + pageSize + ",\n\tcount: " + count + ",\n\tcurrentPage: " + currentPage + "\n\t}";
  }
}
