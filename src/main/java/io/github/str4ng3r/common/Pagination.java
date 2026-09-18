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

import io.github.str4ng3r.exceptions.InvalidCurrentPageException;
import io.github.str4ng3r.common.Constants.SqlDialect;

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

  protected Pagination(Pagination p) {
    this(p.pageSize, p.count, p.currentPage);
    this.totalPages = p.totalPages;
  }

  public Pagination() {
  }

  @Override
  public String toString() {
    return "{\n\tpageSize: " + pageSize + ",\n\tcount: " + count + ",\n\tcurrentPage: " + currentPage
        + "\n\ttotalPages: " + totalPages + "\n}";
  }

  protected void calculatePagination(SqlParameter sqlP, Constants constants, Parameter parameter)
      throws InvalidCurrentPageException {
    if (currentPage < 1)
      throw new InvalidCurrentPageException("The page must be greater than 0");

    int offset = pageSize * (currentPage - 1);
    int limit = pageSize;

    // Cada dialecto ordena sus tokens :low/:upper de forma distinta:
    //   Oracle:   OFFSET :low ROWS FETCH NEXT :upper ROWS ONLY  -> (offset, limit)
    //   MySQL:    LIMIT :low, :upper                            -> (offset, limit)
    //   Postgres: LIMIT :low OFFSET :upper                      -> (limit, offset)
    //   SQL:      LIMIT :low OFFSET :upper                      -> (limit, offset)
    String first, second;
    String dialect = constants.getSqlDialect();
    if (dialect.equals(SqlDialect.Postgres.sqlDialect) || dialect.equals(SqlDialect.Sql.sqlDialect)) {
      first = Integer.toString(limit);
      second = Integer.toString(offset);
    } else { // Oracle y MySQL
      first = Integer.toString(offset);
      second = Integer.toString(limit);
    }

    totalPages = (int) Math.ceil((double) count / pageSize);
    sqlP.setPagination(this);
    sqlP.setSql(sqlP.getSql()
        + constants.replaceValues(constants.getAction(Constants.Actions.PAGINATION), first, second));
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public Integer getCurrentPage() {
    return currentPage;
  }

  public void setCurrentPage(Integer currentPage) {
    this.currentPage = currentPage;
  }

  public Integer getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(Integer totalPages) {
    this.totalPages = totalPages;
  }
}
