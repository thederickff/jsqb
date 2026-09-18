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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import io.github.str4ng3r.exceptions.InvalidCurrentPageException;
import io.github.str4ng3r.exceptions.InvalidSqlGenerationException;

/**
 * Tests focalizados en la lógica de paginación por dialecto y en la expansión
 * de parámetros (colecciones -> múltiples placeholders y orden de binding).
 *
 * BUG-PAG corregido: calculatePagination() ahora calcula offset y limit y los
 * mapea al orden de tokens de cada dialecto:
 *   Oracle:   OFFSET {offset} ROWS FETCH NEXT {limit} ROWS ONLY
 *   MySQL:    LIMIT {offset}, {limit}
 *   Postgres: LIMIT {limit} OFFSET {offset}
 *   SQL:      LIMIT {limit} OFFSET {offset}
 */
public class PaginationTest {

    private Selector selector(Constants.SqlDialect d) {
        return new Selector().select("usuarios").setDialect(d);
    }

    // -------------------------------------------------------------------------
    // Oracle (correcto)
    // -------------------------------------------------------------------------

    @Test
    public void oraclePrimeraPagina() throws InvalidCurrentPageException, InvalidSqlGenerationException {
        Selector s = selector(Constants.SqlDialect.Oracle);
        SqlParameter sp = s.getSqlAndParameters();
        s.setPagination(sp, new Pagination(10, 50, 1));
        assertEquals("SELECT * FROM usuarios OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY", sp.getSql());
    }

    @Test
    public void oracleSegundaPagina() throws InvalidCurrentPageException, InvalidSqlGenerationException {
        Selector s = selector(Constants.SqlDialect.Oracle);
        SqlParameter sp = s.getSqlAndParameters();
        s.setPagination(sp, new Pagination(10, 100, 2));
        assertEquals("SELECT * FROM usuarios OFFSET 10 ROWS FETCH NEXT 10 ROWS ONLY", sp.getSql());
    }

    @Test
    public void totalPagesSeCalcula() throws InvalidCurrentPageException, InvalidSqlGenerationException {
        Selector s = selector(Constants.SqlDialect.Oracle);
        SqlParameter sp = s.getSqlAndParameters();
        Pagination p = new Pagination(10, 95, 1);
        s.setPagination(sp, p);
        // ceil(95 / 10) = 10
        assertEquals(Integer.valueOf(10), p.getTotalPages());
    }

    // -------------------------------------------------------------------------
    // MySQL / Postgres / SQL (BUG-PAG corregido)
    // -------------------------------------------------------------------------

    @Test
    public void mysqlSegundaPagina()
            throws InvalidCurrentPageException, InvalidSqlGenerationException {
        Selector s = selector(Constants.SqlDialect.Mysql);
        SqlParameter sp = s.getSqlAndParameters();
        s.setPagination(sp, new Pagination(10, 100, 2));
        // MySQL: LIMIT {offset}, {count}
        assertEquals("SELECT * FROM usuarios LIMIT 10, 10", sp.getSql());
    }

    @Test
    public void postgresSegundaPagina()
            throws InvalidCurrentPageException, InvalidSqlGenerationException {
        Selector s = selector(Constants.SqlDialect.Postgres);
        SqlParameter sp = s.getSqlAndParameters();
        s.setPagination(sp, new Pagination(10, 100, 2));
        // Postgres: LIMIT {count} OFFSET {offset}
        assertEquals("SELECT * FROM usuarios LIMIT 10 OFFSET 10", sp.getSql());
    }

    @Test
    public void postgresTerceraPaginaConPageSizeDistinto()
            throws InvalidCurrentPageException, InvalidSqlGenerationException {
        Selector s = selector(Constants.SqlDialect.Postgres);
        SqlParameter sp = s.getSqlAndParameters();
        s.setPagination(sp, new Pagination(5, 100, 3));
        // offset = 5 * (3-1) = 10, limit = 5
        assertEquals("SELECT * FROM usuarios LIMIT 5 OFFSET 10", sp.getSql());
    }

    @Test
    public void sqlSegundaPagina()
            throws InvalidCurrentPageException, InvalidSqlGenerationException {
        Selector s = selector(Constants.SqlDialect.Sql);
        SqlParameter sp = s.getSqlAndParameters();
        s.setPagination(sp, new Pagination(10, 100, 2));
        assertEquals("SELECT * FROM usuarios LIMIT 10 OFFSET 10", sp.getSql());
    }

    @Test(expected = InvalidCurrentPageException.class)
    public void paginaCeroLanzaExcepcion() throws InvalidCurrentPageException, InvalidSqlGenerationException {
        Selector s = selector(Constants.SqlDialect.Oracle);
        SqlParameter sp = s.getSqlAndParameters();
        s.setPagination(sp, new Pagination(10, 100, 0));
    }

    // -------------------------------------------------------------------------
    // Expansión de parámetros (Collection -> placeholders y orden)
    // -------------------------------------------------------------------------

    @Test
    public void expansionDeColeccionEnWhereIn() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("productos")
                .where("id IN (:ids)", p -> p.put("ids", Arrays.asList(10, 20, 30)))
                .getSqlAndParameters();
        assertEquals("SELECT * FROM productos WHERE id IN (?,?,?)", r.getSql());
        assertEquals(Arrays.asList(10, 20, 30), r.getListParameters());
    }

    @Test
    public void ordenDeParametrosMezclandoEscalarYColeccion() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("pedidos")
                .where("estado IN (:estados)", p -> p.put("estados", Arrays.asList("A", "B")))
                .andWhere("usuario_id = :uid", p -> p.put("uid", 7))
                .getSqlAndParameters();
        assertEquals("SELECT * FROM pedidos WHERE estado IN (?,?) AND usuario_id = ?", r.getSql());
        assertEquals(Arrays.asList("A", "B", 7), r.getListParameters());
    }

    @Test
    public void coleccionDeUnElementoGeneraUnPlaceholder() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("usuarios")
                .where("id IN (:ids)", p -> p.put("ids", Arrays.asList(99)))
                .getSqlAndParameters();
        assertEquals("SELECT * FROM usuarios WHERE id IN (?)", r.getSql());
        assertEquals(Arrays.asList(99), r.getListParameters());
    }
}
