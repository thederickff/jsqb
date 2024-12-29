package io.github.str4ng3r.common;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.github.str4ng3r.exceptions.InvalidSqlGenerationException;
import io.github.str4ng3r.common.Tables.ACTIONSQL;

public class Update extends QueryBuilder<Update> {

  private final Set<String> columnsToExclude = new HashSet<String>();

  public Update() {
    super();
    super.setReferenceObject(this);
    initialize();
  }

  private void initialize() {
    this.tables = new Tables(ACTIONSQL.UPDATE);
  }

  public Update excludeColumns(String... columnsToExclude) {
    Collections.addAll(this.columnsToExclude, columnsToExclude);
    return this;
  }

  /**
   * This should init from
   * 
   * @param tableNames an array of tables
   *
   * @return same object as pipe
   */
  public Update from(String... tableNames) {
    this.tables.from(tableNames);
    return this;
  }

  @Override
  protected String write() throws InvalidSqlGenerationException {
    if (this.where.listFilterCriteria.isEmpty())
      throw new InvalidSqlGenerationException("It's dangerous to create an update without a where");

    if (this.columnsToExclude.stream().anyMatch(k -> parameter.parameters.containsKey(":" + k)))
      throw new InvalidSqlGenerationException("Can not update a column from database");

    this.columnsToExclude.add("id");
    this.parameter.parameters.forEach((k, v) -> {
      if (!k.equals(":id"))
        tables.addFields(k.substring(1) + " = " + k);
    });

    StringBuilder sql = this.tables.write();
    this.where.write(sql);

    return sql.toString();
  }
}
