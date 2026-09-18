package io.github.str4ng3r.common;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import io.github.str4ng3r.common.Join;
import io.github.str4ng3r.exceptions.InvalidCurrentPageException;
import io.github.str4ng3r.exceptions.InvalidSqlGenerationException;

/**
 * Tests exhaustivos para la clase Selector.
 *
 * BUGS DOCUMENTADOS:
 *   BUG-2 corregido: OrderGroupBy.orderBy() ahora acumula las columnas en vez
 *           de sobreescribir. El test orderByMultiplesColumnasAcumula() lo verifica.
 */
public class SelectorQueryTest {

    // =========================================================================
    // SELECT básico
    // =========================================================================

    @Test
    public void selectSinCamposUsaAsterisco() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector().select("productos").getSqlAndParameters();
        check("SELECT * FROM productos", r.getSql());
        assertTrue(r.getListParameters().isEmpty());
    }

    @Test
    public void selectConUnCampo() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("usuarios", "nombre")
                .getSqlAndParameters();
        check("SELECT nombre FROM usuarios", r.getSql());
    }

    @Test
    public void selectConMultiplesCampos() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("usuarios", "id", "nombre", "email")
                .getSqlAndParameters();
        check("SELECT id, nombre, email FROM usuarios", r.getSql());
    }

    @Test
    public void selectConAlias() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("usuarios as u", "u.id", "u.nombre as name")
                .getSqlAndParameters();
        check("SELECT u.id, u.nombre as name FROM usuarios as u", r.getSql());
    }

    @Test
    public void addSelectAgregaCamposAlQuery() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("usuarios", "id")
                .addSelect("nombre", "email")
                .getSqlAndParameters();
        check("SELECT id, nombre, email FROM usuarios", r.getSql());
    }

    // =========================================================================
    // WHERE
    // =========================================================================

    @Test
    public void whereConParametro() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("usuarios")
                .where("id = :id", p -> p.put("id", 1))
                .getSqlAndParameters();
        check("SELECT * FROM usuarios WHERE id = ?", r.getSql());
        assertEquals(1, r.getListParameters().size());
        assertEquals(1, r.getListParameters().get(0));
    }

    @Test
    public void whereConAndWhere() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("usuarios")
                .where("activo = :activo", p -> p.put("activo", true))
                .andWhere("rol = :rol", p -> p.put("rol", "admin"))
                .getSqlAndParameters();
        check("SELECT * FROM usuarios WHERE activo = ? AND rol = ?", r.getSql());
        assertEquals(2, r.getListParameters().size());
    }

    @Test
    public void whereConMultiplesAndWhere() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("productos")
                .where("categoria = :cat",   p -> p.put("cat", "TECH"))
                .andWhere("precio > :min",   p -> p.put("min", 100))
                .andWhere("precio < :max",   p -> p.put("max", 500))
                .andWhere("stock > :stock",  p -> p.put("stock", 0))
                .getSqlAndParameters();
        check("SELECT * FROM productos WHERE categoria = ? AND precio > ? AND precio < ? AND stock > ?",
                r.getSql());
        assertEquals(4, r.getListParameters().size());
    }

    @Test
    public void whereSinParametroLiteral() throws InvalidSqlGenerationException {
        // WHERE con expresión sin bind params
        SqlParameter r = new Selector()
                .select("logs")
                .where("activo = TRUE", p -> {})
                .getSqlAndParameters();
        check("SELECT * FROM logs WHERE activo = TRUE", r.getSql());
        assertTrue(r.getListParameters().isEmpty());
    }

    // =========================================================================
    // WHERE IN (expansión de Collection) — BUG-5 corregido
    // =========================================================================

    @Test
    public void whereInConLista() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("productos")
                .where("id IN (:ids)", p -> p.put("ids", Arrays.asList(1, 2, 3)))
                .getSqlAndParameters();
        check("SELECT * FROM productos WHERE id IN (?,?,?)", r.getSql());
        assertEquals(3, r.getListParameters().size());
        assertEquals(1, r.getListParameters().get(0));
        assertEquals(2, r.getListParameters().get(1));
        assertEquals(3, r.getListParameters().get(2));
    }

    @Test
    public void whereInConListaDeStrings() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("roles")
                .where("nombre IN (:nombres)", p -> p.put("nombres", Arrays.asList("admin", "user", "guest")))
                .getSqlAndParameters();
        check("SELECT * FROM roles WHERE nombre IN (?,?,?)", r.getSql());
        assertEquals(3, r.getListParameters().size());
        assertEquals("admin", r.getListParameters().get(0));
        assertEquals("user",  r.getListParameters().get(1));
        assertEquals("guest", r.getListParameters().get(2));
    }

    @Test
    public void whereInConUnSoloElemento() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("usuarios")
                .where("id IN (:ids)", p -> p.put("ids", Arrays.asList(42)))
                .getSqlAndParameters();
        check("SELECT * FROM usuarios WHERE id IN (?)", r.getSql());
        assertEquals(1, r.getListParameters().size());
        assertEquals(42, r.getListParameters().get(0));
    }

    @Test
    public void whereInCombidadoConOtrosWhere() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("pedidos")
                .where("estado IN (:estados)", p -> p.put("estados", Arrays.asList("PENDIENTE", "ENVIADO")))
                .andWhere("usuario_id = :uid", p -> p.put("uid", 5))
                .getSqlAndParameters();
        check("SELECT * FROM pedidos WHERE estado IN (?,?) AND usuario_id = ?", r.getSql());
        assertEquals(3, r.getListParameters().size());
        assertEquals("PENDIENTE", r.getListParameters().get(0));
        assertEquals("ENVIADO",   r.getListParameters().get(1));
        assertEquals(5,           r.getListParameters().get(2));
    }

    // =========================================================================
    // JOIN
    // =========================================================================

    @Test
    public void innerJoin() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("usuarios u", "u.id", "u.nombre")
                .join(Join.INNER, "roles r", "r.id = u.rol_id")
                .getSqlAndParameters();
        check("SELECT u.id, u.nombre FROM usuarios u INNER JOIN roles r ON r.id = u.rol_id", r.getSql());
    }

    @Test
    public void leftJoin() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("usuarios u", "u.id")
                .join(Join.LEFT, "pedidos p", "p.usuario_id = u.id")
                .getSqlAndParameters();
        check("SELECT u.id FROM usuarios u LEFT JOIN pedidos p ON p.usuario_id = u.id", r.getSql());
    }

    @Test
    public void rightJoin() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("usuarios u", "u.id")
                .join(Join.RIGHT, "departamentos d", "d.id = u.dep_id")
                .getSqlAndParameters();
        check("SELECT u.id FROM usuarios u RIGHT JOIN departamentos d ON d.id = u.dep_id", r.getSql());
    }

    @Test
    public void multipleJoins() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("usuarios u", "u.id", "u.nombre")
                .join(Join.INNER, "roles r",       "r.id = u.rol_id")
                .join(Join.LEFT,  "direcciones d", "d.usuario_id = u.id")
                .addSelect("r.nombre", "d.calle")
                .getSqlAndParameters();

        check("SELECT u.id, u.nombre, r.nombre, d.calle FROM usuarios u"
                + " INNER JOIN roles r ON r.id = u.rol_id"
                + " LEFT JOIN direcciones d ON d.usuario_id = u.id", r.getSql());
    }

    @Test
    public void joinConWhere() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("usuarios u", "u.id", "u.nombre")
                .join(Join.INNER, "roles r", "r.id = u.rol_id")
                .where("r.nivel = :nivel", p -> p.put("nivel", 1))
                .getSqlAndParameters();

        check("SELECT u.id, u.nombre FROM usuarios u INNER JOIN roles r ON r.id = u.rol_id WHERE r.nivel = ?",
                r.getSql());
        assertEquals(1, r.getListParameters().size());
    }

    @Test
    public void crossJoinNoGeneraOnColgante() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("colores c", "c.id", "t.id")
                .crossJoin("tallas t")
                .getSqlAndParameters();
        check("SELECT c.id, t.id FROM colores c CROSS JOIN tallas t", r.getSql());
    }

    // =========================================================================
    // Validación de entrada (#4) e inmutabilidad de parámetros (#5)
    // =========================================================================

    @Test(expected = IllegalArgumentException.class)
    public void selectConTablaVaciaLanzaExcepcion() {
        new Selector().select("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void selectConTablaNullLanzaExcepcion() {
        new Selector().select(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void joinConTablaVaciaLanzaExcepcion() {
        new Selector().select("usuarios u").join(Join.INNER, "  ", "1=1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void joinConTipoNullLanzaExcepcion() {
        new Selector().select("usuarios u").join(null, "roles r", "1=1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void whereConCriterioVacioLanzaExcepcion() {
        new Selector().select("usuarios").where("   ", p -> {});
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getListParametersEsInmutable() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("usuarios")
                .where("id = :id", p -> p.put("id", 1))
                .getSqlAndParameters();
        r.getListParameters().add("intruso");
    }

    // =========================================================================
    // ORDER BY
    // =========================================================================

    @Test
    public void orderByDescendente() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("empleados")
                .orderBy("salario", true)
                .getSqlAndParameters();
        check("SELECT * FROM empleados ORDER BY salario DESC", r.getSql());
    }

    @Test
    public void orderByAscendente() throws InvalidSqlGenerationException {
        // BUG-4 corregido: orderBy(col, false) ahora agrega ASC
        SqlParameter r = new Selector()
                .select("productos")
                .orderBy("precio", false)
                .getSqlAndParameters();
        check("SELECT * FROM productos ORDER BY precio ASC", r.getSql());
    }

    /**
     * BUG-2 corregido: OrderGroupBy.orderBy() ahora acumula múltiples columnas
     * en vez de sobreescribir. Dos llamadas a orderBy() conservan ambas columnas
     * en el orden en que se agregaron.
     */
    @Test
    public void orderByMultiplesColumnasAcumula() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("pedidos")
                .orderBy("fecha", true)
                .orderBy("total", false)
                .getSqlAndParameters();

        check("SELECT * FROM pedidos ORDER BY fecha DESC, total ASC", r.getSql());
        assertTrue("Debe conservar 'fecha'", r.getSql().contains("fecha DESC"));
        assertTrue("Debe conservar 'total'", r.getSql().contains("total ASC"));
    }

    // =========================================================================
    // GROUP BY
    // =========================================================================

    @Test
    public void groupBySolo() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("ventas", "categoria", "COUNT(*) total")
                .groupBy("categoria")
                .getSqlAndParameters();
        check("SELECT categoria, COUNT(*) total FROM ventas GROUP BY categoria", r.getSql());
    }

    @Test
    public void groupByConWhere() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("ventas", "vendedor_id", "SUM(monto) total")
                .where("fecha > :fecha", p -> p.put("fecha", "2024-01-01"))
                .groupBy("vendedor_id")
                .getSqlAndParameters();
        check("SELECT vendedor_id, SUM(monto) total FROM ventas WHERE fecha > ? GROUP BY vendedor_id",
                r.getSql());
    }

    // =========================================================================
    // HAVING
    // =========================================================================

    @Test
    public void havingSolo() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("ventas", "categoria", "COUNT(*) cnt")
                .groupBy("categoria")
                .having("COUNT(*) > :min", p -> p.put("min", 5))
                .getSqlAndParameters();
        check("SELECT categoria, COUNT(*) cnt FROM ventas GROUP BY categoria HAVING COUNT(*) > ?",
                r.getSql());
        assertEquals(1, r.getListParameters().size());
        assertEquals(5, r.getListParameters().get(0));
    }

    @Test
    public void havingConAndHaving() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("ventas", "categoria", "SUM(monto) total")
                .groupBy("categoria")
                .having("SUM(monto) > :min", p -> p.put("min", 100))
                .andHaving("SUM(monto) < :max", p -> p.put("max", 1000))
                .getSqlAndParameters();
        check("SELECT categoria, SUM(monto) total FROM ventas GROUP BY categoria HAVING SUM(monto) > ? AND SUM(monto) < ?",
                r.getSql());
        assertEquals(2, r.getListParameters().size());
    }

    @Test
    public void havingConConsumerNullNoLanzaExcepcion() throws InvalidSqlGenerationException {
        // BUG-6 corregido: having(criteria, null) ya no lanza NPE
        SqlParameter r = new Selector()
                .select("logs", "nivel", "COUNT(*) cnt")
                .groupBy("nivel")
                .having("COUNT(*) > 0", null)
                .getSqlAndParameters();
        assertTrue("El SQL debe contener HAVING", r.getSql().contains("HAVING COUNT(*) > 0"));
    }

    // =========================================================================
    // SELECT + WHERE + JOIN + GROUP BY + HAVING + ORDER BY combinados
    // =========================================================================

    @Test
    public void queryCompleta() throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("pedidos p", "p.usuario_id", "SUM(p.total) total_gastado")
                .join(Join.INNER, "usuarios u", "u.id = p.usuario_id")
                .where("p.fecha > :fecha", pw -> pw.put("fecha", "2024-01-01"))
                .groupBy("p.usuario_id")
                .having("SUM(p.total) > :minTotal", ph -> ph.put("minTotal", 500))
                .orderBy("total_gastado", true)
                .getSqlAndParameters();

        check("SELECT p.usuario_id, SUM(p.total) total_gastado FROM pedidos p"
                + " INNER JOIN usuarios u ON u.id = p.usuario_id"
                + " WHERE p.fecha > ? GROUP BY p.usuario_id"
                + " HAVING SUM(p.total) > ? ORDER BY total_gastado DESC", r.getSql());
        assertEquals(2, r.getListParameters().size());
        assertEquals("2024-01-01", r.getListParameters().get(0));
        assertEquals(500, r.getListParameters().get(1));
    }

    // =========================================================================
    // getCount
    // =========================================================================

    @Test
    public void getCountSinMysqlNoAgregaAlias() throws InvalidSqlGenerationException {
        Selector s = new Selector().select("usuarios");
        String sql = s.getSqlAndParameters().getSql();
        String conteo = s.getCount(sql);
        check("SELECT COUNT(*) FROM ( SELECT * FROM usuarios )", conteo);
    }

    @Test
    public void getCountConDialectoMysqlAgregaAlias() throws InvalidSqlGenerationException {
        Selector s = new Selector().select("usuarios").setDialect(Constants.SqlDialect.Mysql);
        String sql = s.getSqlAndParameters().getSql();
        String conteo = s.getCount(sql);
        assertTrue("MySQL debe agregar alias temp_count", conteo.contains("AS  temp_count"));
    }

    // =========================================================================
    // Paginación
    // =========================================================================

    @Test
    public void paginacionOracle() throws InvalidCurrentPageException, InvalidSqlGenerationException {
        Selector s = new Selector()
                .select("usuarios")
                .setDialect(Constants.SqlDialect.Oracle);
        SqlParameter sp = s.getSqlAndParameters();
        s.setPagination(sp, new Pagination(10, 100, 2));

        // Oracle: OFFSET {(page-1)*pageSize} ROWS FETCH NEXT {pageSize} ROWS ONLY
        assertTrue("Debe contener OFFSET", sp.getSql().contains("OFFSET"));
        assertTrue("Debe contener FETCH NEXT", sp.getSql().contains("FETCH NEXT"));
        assertNotNull(sp.getPagination());
    }

    @Test
    public void paginacionMysql() throws InvalidCurrentPageException, InvalidSqlGenerationException {
        Selector s = new Selector()
                .select("usuarios")
                .setDialect(Constants.SqlDialect.Mysql);
        SqlParameter sp = s.getSqlAndParameters();
        s.setPagination(sp, new Pagination(10, 100, 1));

        assertTrue("MySQL debe contener LIMIT", sp.getSql().contains("LIMIT"));
    }

    @Test
    public void paginacionPrimeraPagina() throws InvalidCurrentPageException, InvalidSqlGenerationException {
        // BUG-7 corregido: página 1 debe tener OFFSET 0 FETCH NEXT pageSize
        Selector s = new Selector()
                .select("usuarios")
                .setDialect(Constants.SqlDialect.Oracle);
        SqlParameter sp = s.getSqlAndParameters();
        s.setPagination(sp, new Pagination(10, 50, 1));

        assertTrue("Página 1 debe tener OFFSET 0",     sp.getSql().contains("OFFSET 0"));
        assertTrue("Página 1 debe tener FETCH NEXT 10", sp.getSql().contains("FETCH NEXT 10"));
    }

    @Test
    public void paginacionSegundaPagina() throws InvalidCurrentPageException, InvalidSqlGenerationException {
        Selector s = new Selector()
                .select("usuarios")
                .setDialect(Constants.SqlDialect.Oracle);
        SqlParameter sp = s.getSqlAndParameters();
        s.setPagination(sp, new Pagination(10, 100, 2));

        // Página 2: OFFSET = 10, FETCH NEXT = 10
        assertTrue("Página 2 debe tener OFFSET 10",     sp.getSql().contains("OFFSET 10"));
        assertTrue("Página 2 debe tener FETCH NEXT 10", sp.getSql().contains("FETCH NEXT 10"));
    }

    @Test(expected = InvalidCurrentPageException.class)
    public void paginacionConPaginaMenorACeroLanzaExcepcion()
            throws InvalidCurrentPageException, InvalidSqlGenerationException {
        Selector s = new Selector().select("usuarios").setDialect(Constants.SqlDialect.Oracle);
        SqlParameter sp = s.getSqlAndParameters();
        s.setPagination(sp, new Pagination(10, 100, 0));
    }

    @Test(expected = InvalidCurrentPageException.class)
    public void paginacionConPaginaNegativaLanzaExcepcion()
            throws InvalidCurrentPageException, InvalidSqlGenerationException {
        Selector s = new Selector().select("usuarios").setDialect(Constants.SqlDialect.Oracle);
        SqlParameter sp = s.getSqlAndParameters();
        s.setPagination(sp, new Pagination(10, 100, -1));
    }

    // =========================================================================
    // Dialecto
    // =========================================================================

    @Test
    public void dialectoPorDefectoEsSql() throws InvalidSqlGenerationException {
        // El dialecto por defecto no altera el SQL básico
        SqlParameter r = new Selector().select("usuarios").getSqlAndParameters();
        check("SELECT * FROM usuarios", r.getSql());
    }

    // =========================================================================
    // Casos de error
    // =========================================================================

    @Test(expected = InvalidSqlGenerationException.class)
    public void selectSinTablaNiCamposLanzaExcepcion() throws InvalidSqlGenerationException {
        new Selector().getSqlAndParameters();
    }

    // =========================================================================
    // getSqlAndParametersDictionarie
    // =========================================================================

    @Test
    public void getSqlAndParametersDictionarieDevuelveParametros()
            throws InvalidSqlGenerationException {
        SqlParameter r = new Selector()
                .select("usuarios")
                .where("id = :id", p -> p.put("id", 99))
                .getSqlAndParametersDictionarie();

        assertNotNull(r.dictionarieParameters());
        assertTrue("El diccionario debe contener 'id'", r.dictionarieParameters().containsKey("id"));
        assertEquals(99, r.dictionarieParameters().get("id"));
    }

    // =========================================================================
    // Utilidad
    // =========================================================================

    private void check(String exp, String act) {
        System.out.println("Esp: " + exp);
        System.out.println("Act: " + act);
        assertEquals(exp, act);
    }
}
