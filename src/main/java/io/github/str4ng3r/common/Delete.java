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

import io.github.str4ng3r.exceptions.InvalidSqlGenerationException;
import io.github.str4ng3r.common.Tables.ACTIONSQL;

/**
 *
 * @author Pablo Eduardo Martinez Solis
 */
public class Delete extends QueryBuilder<Delete> {

  public Delete() {
    super();
    super.setReferenceObject(this);
    initialize();
  }

  private void initialize() {
    this.tables = new Tables(ACTIONSQL.DELETE);
  }

  /**
   * This should init from
   * 
   * @param tableNames
   *
   * @return same object as pipe
   */
  public Delete from(String... tableNames) {
    this.tables.from(tableNames);
    return this;
  }

  @Override
  protected String write() throws InvalidSqlGenerationException {
    if (this.where.listFilterCriteria.isEmpty())
      throw new InvalidSqlGenerationException("It's dangerous to create a delete without where");

    StringBuilder sql = this.tables.write();

    this.where.write(sql);

    return sql.toString();
  }

}
