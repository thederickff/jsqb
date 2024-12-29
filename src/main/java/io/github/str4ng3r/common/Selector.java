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
import io.github.str4ng3r.exceptions.InvalidSqlGenerationException;
import io.github.str4ng3r.common.Tables.ACTIONSQL;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * A class that handles select statements
 *
 * @author Pablo Eduardo Martinez Solis
 */
public class Selector extends QueryBuilder<Selector> {

    private OrderGroupBy orderBy;
    private OrderGroupBy groupBy;
    private WhereHaving having;

    public Selector() {
        super();
        super.setReferenceObject(this);
        initialize();
    }

    private void initialize() {
        this.tables = new Tables(ACTIONSQL.SELECT);
        this.orderBy = null;
        this.groupBy = null;
        this.having = null;
    }

    /**
     * Initialize select and put the first table to join
     *
     * @param tableName
     * @param fields
     * @return same object as pipe
     */
    public Selector select(String tableName, String... fields) {
        this.tables.addTable(tableName, fields);
        return this;
    }

    /**
     * Add more fields to satement
     *
     * @param fields An list to fields to fetch
     * @return same object as pipe
     */
    public Selector addSelect(String... fields) {
        this.tables.addFields(fields);
        return this;
    }

    /**
     * Add order by to statement
     *
     * @param orderBy
     * @param descending
     * @return same object as pipe
     */
    public Selector orderBy(String orderBy, boolean descending) {
        if (this.orderBy == null)
            this.orderBy = new OrderGroupBy();
        this.orderBy.orderBy(orderBy, descending);
        return this;
    }

    /**
     * Add group by to statement
     * Pass columns to group by separae by ,
     *
     * @param columns
     * @return same object as pipe
     */
    public Selector groupBy(String columns) {
        this.groupBy = new OrderGroupBy();
        this.groupBy.groupBy(columns);
        return this;
    }

    /**
     * This initialize the having (If there's any previous filter criteria should be
     * reset)
     *
     * @param criteria
     * @param parameters
     * @return same object as pipe
     */
    public Selector having(String criteria, Consumer<HashMap<String, String>> parameters) {
        if (this.having == null)
            this.having = new WhereHaving(" HAVING ", this.parameter);
        this.having.addCriteria(criteria, parameters);
        return this;
    }

    /**
     * Add more filter criteria to previous criteria
     *
     * @param criteria
     * @param parameters
     * @return same object as pipe
     */
    public Selector andHaving(String criteria, Consumer<HashMap<String, String>> parameters) {
        if (this.having == null)
            this.having = new WhereHaving(" HAVING ", this.parameter);
        this.having.andAddCriteria(criteria, parameters);
        return this;
    }

    public String getCount(String sql) {
        String newSql = "SELECT COUNT(*) FROM ( " + sql + " )";
        if (constants.getSqlDialect() == Constants.SqlDialect.Mysql.sqlDialect)
            newSql += " AS  temp_count";
        return newSql;
    }

    /**
     * Return a querybuilder with pagination
     *
     * @param sqlParameter
     * @param pagination
     * @return same object as pipe
     * @throws InvalidCurrentPageException
     */
    public Selector setPagination(SqlParameter sqlParameter, Pagination pagination) throws InvalidCurrentPageException {
        pagination.calculatePagination(sqlParameter, constants, parameter);
        sqlParameter.p = pagination;
        return this;
    }

    @Override
    protected String write() throws InvalidSqlGenerationException {
        StringBuilder sql = this.tables.write();

        this.where.write(sql);

        if (this.groupBy != null)
            sql.append(" GROUP BY ").append(this.groupBy.write());

        if (this.having != null)
            this.having.write(sql);

        if (this.orderBy != null)
            sql.append(" ORDER BY ").append(this.orderBy.write());

        return sql.toString();
    }

}
