package io.github.str4ng3r.common;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedHashMap;

import org.junit.Test;

import io.github.str4ng3r.common.Join;
import io.github.str4ng3r.exceptions.InvalidSqlGenerationException;

/**
 * Tests exhaustivos para la clase Update.
 *
 * BUGS DOCUMENTADOS:
 *   BUG-3 corregido: excludeColumns() ahora se aplica en write(), filtrando las
 *           columnas excluidas del SET y de los parámetros generados.
 */
public class UpdateTest {

    // =========================================================================
    // Helpers
    // =========================================================================

    Update updateBase() {
        return new Update()
                .from("usuarios u")
                .join(Join.INNER, "direcciones d", "d.usuario_id = u.id");
    }

    // =========================================================================
    // Casos felices
    // =========================================================================

    @Test
    public void updateUnaColumna() throws InvalidSqlGenerationException {
        SqlParameter r = new Update()
                .from("productos p")
                .setColumnsValuesToUpdate(cols -> cols.put("p.precio", 99.9))
                .where("p.id = :id", params -> params.put("id", 42))
                .getSqlAndParameters();

        // Nota: Tables.write() agrega un espacio al final del SET antes del WHERE
        check("UPDATE productos p SET p.precio = ?  WHERE p.id = ?", r.getSql());
        assertEquals(2, r.getListParameters().size());
        assertEquals(99.9, r.getListParameters().get(0));
        assertEquals(42,   r.getListParameters().get(1));
    }

    @Test
    public void updateVariasColumnas() throws InvalidSqlGenerationException {
        SqlParameter r = new Update()
                .from("usuarios u")
                .setColumnsValuesToUpdate(cols -> {
                    cols.put("u.nombre", "Carlos");
                    cols.put("u.email",  "carlos@ejemplo.com");
                })
                .where("u.id = :id", params -> params.put("id", 7))
                .getSqlAndParameters();

        check("UPDATE usuarios u SET u.nombre = ?, u.email = ?  WHERE u.id = ?", r.getSql());
        // 2 columnas SET + 1 WHERE
        assertEquals(3, r.getListParameters().size());
        assertEquals("Carlos",              r.getListParameters().get(0));
        assertEquals("carlos@ejemplo.com",  r.getListParameters().get(1));
        assertEquals(7,                     r.getListParameters().get(2));
    }

    /**
     * Documenta el SQL exacto que hoy genera un UPDATE con JOIN.
     * LIMITACIÓN CONOCIDA: la cláusula SET se emite ANTES del JOIN. En MySQL la
     * sintaxis válida es "UPDATE t1 JOIN t2 ON ... SET ...", por lo que este
     * orden no es ejecutable en MySQL para updates multi-tabla. El test fija el
     * comportamiento actual para detectar regresiones si se corrige el orden.
     */
    @Test
    public void updateConJoin() throws InvalidSqlGenerationException {
        SqlParameter r = updateBase()
                .setColumnsValuesToUpdate(cols -> cols.put("u.activo", false))
                .where("d.ciudad = :ciudad", params -> params.put("ciudad", "CDMX"))
                .getSqlAndParameters();

        check("UPDATE usuarios u SET u.activo = ?  INNER JOIN direcciones d ON d.usuario_id = u.id"
                + " WHERE d.ciudad = ?", r.getSql());
        assertEquals(2, r.getListParameters().size());
        assertEquals(false, r.getListParameters().get(0));
        assertEquals("CDMX", r.getListParameters().get(1));
    }

    @Test
    public void updateConAndWhere() throws InvalidSqlGenerationException {
        SqlParameter r = new Update()
                .from("sesiones s")
                .setColumnsValuesToUpdate(cols -> cols.put("s.activa", false))
                .where("s.usuario_id = :uid",       params -> params.put("uid", 10))
                .andWhere("s.expira_en < :ahora",   params -> params.put("ahora", "2025-01-01"))
                .getSqlAndParameters();

        check("UPDATE sesiones s SET s.activa = ?  WHERE s.usuario_id = ? AND s.expira_en < ?", r.getSql());
        assertEquals(3, r.getListParameters().size());
    }

    @Test
    public void updateConWhereIn() throws InvalidSqlGenerationException {
        SqlParameter r = new Update()
                .from("notificaciones n")
                .setColumnsValuesToUpdate(cols -> cols.put("n.leida", true))
                .where("n.id IN (:ids)", params -> params.put("ids", Arrays.asList(1, 2, 3)))
                .getSqlAndParameters();

        // BUG-5 corregido: WHERE IN expande un placeholder por cada elemento
        check("UPDATE notificaciones n SET n.leida = ?  WHERE n.id IN (?,?,?)", r.getSql());
        // 1 valor del SET + 3 valores de la colección del IN
        assertEquals(4, r.getListParameters().size());
        assertEquals(true, r.getListParameters().get(0));
        assertEquals(1, r.getListParameters().get(1));
        assertEquals(2, r.getListParameters().get(2));
        assertEquals(3, r.getListParameters().get(3));
    }

    @Test
    public void updateConLeftJoin() throws InvalidSqlGenerationException {
        SqlParameter r = new Update()
                .from("usuarios u")
                .join(Join.LEFT, "pedidos p", "p.usuario_id = u.id")
                .setColumnsValuesToUpdate(cols -> cols.put("u.tiene_pedidos", false))
                .where("p.id IS NULL", params -> {})
                .getSqlAndParameters();

        assertTrue("Debe contener LEFT JOIN", r.getSql().contains("LEFT JOIN"));
        assertEquals(1, r.getListParameters().size());
    }

    @Test
    public void updateElOrdenDeLosParametrosEsCorrecto() throws InvalidSqlGenerationException {
        SqlParameter r = new Update()
                .from("items i")
                .setColumnsValuesToUpdate(cols -> cols.put("i.valor", 42))
                .where("i.clave = :clave", params -> params.put("clave", "CONFIG_X"))
                .getSqlAndParameters();

        // Primer parámetro = valor del SET, segundo = valor del WHERE
        assertEquals(42,         r.getListParameters().get(0));
        assertEquals("CONFIG_X", r.getListParameters().get(1));
    }

    // =========================================================================
    // BUG-3 corregido: excludeColumns filtra columnas del SET
    // =========================================================================

    /**
     * BUG-3 corregido: excludeColumns() ahora se aplica en write(); las columnas
     * excluidas no aparecen en el SET ni en los parámetros generados.
     */
    @Test
    public void excludeColumnsFiltraDelSql() throws InvalidSqlGenerationException {
        SqlParameter r = new Update()
                .from("usuarios u")
                .excludeColumns("u.password")
                .setColumnsValuesToUpdate(cols -> {
                    cols.put("u.nombre",   "Ana");
                    cols.put("u.password", "secreto"); // debe excluirse
                })
                .where("u.id = :id", params -> params.put("id", 1))
                .getSqlAndParameters();

        assertFalse("u.password no debe aparecer en el SQL",
                r.getSql().contains("u.password"));
        assertTrue("u.nombre debe permanecer en el SET",
                r.getSql().contains("u.nombre = ?"));
        // 1 valor del SET (u.nombre) + 1 del WHERE; el valor de u.password se excluye
        assertEquals(2, r.getListParameters().size());
        assertEquals("Ana", r.getListParameters().get(0));
        assertEquals(1,     r.getListParameters().get(1));
        assertFalse("El valor 'secreto' no debe estar en los parámetros",
                r.getListParameters().contains("secreto"));
    }

    // =========================================================================
    // Casos de error esperados
    // =========================================================================

    @Test(expected = InvalidSqlGenerationException.class)
    public void updateSinWhereLanzaExcepcion() throws InvalidSqlGenerationException {
        new Update()
                .from("usuarios u")
                .setColumnsValuesToUpdate(cols -> cols.put("u.nombre", "Test"))
                .getSqlAndParameters();
    }

    @Test(expected = InvalidSqlGenerationException.class)
    public void updateSinTablaLanzaExcepcion() throws InvalidSqlGenerationException {
        new Update()
                .setColumnsValuesToUpdate(cols -> cols.put("nombre", "Test"))
                .where("id = :id", params -> params.put("id", 1))
                .getSqlAndParameters();
    }

    // =========================================================================
    // getSqlAndParametersDictionarie
    // =========================================================================

    @Test
    public void updateDictionarieDevuelveParametros() throws InvalidSqlGenerationException {
        SqlParameter r = new Update()
                .from("usuarios u")
                .setColumnsValuesToUpdate(cols -> cols.put("u.nombre", "Ana"))
                .where("u.id = :id", params -> params.put("id", 5))
                .getSqlAndParametersDictionarie();

        assertNotNull(r.dictionarieParameters());
        assertTrue("El diccionario debe contener 'id'", r.dictionarieParameters().containsKey("id"));
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
