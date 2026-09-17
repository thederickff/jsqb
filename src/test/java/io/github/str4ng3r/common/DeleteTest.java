package io.github.str4ng3r.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import io.github.str4ng3r.exceptions.InvalidSqlGenerationException;

/**
 * Tests para la clase Delete.
 */
public class DeleteTest {

    // -------------------------------------------------------------------------
    // Casos felices
    // -------------------------------------------------------------------------

    @Test
    public void deleteSimpleConWhere() throws InvalidSqlGenerationException {
        SqlParameter resultado = new Delete()
                .from("usuarios")
                .where("id = :id", p -> p.put("id", 5))
                .getSqlAndParameters();

        verificar("DELETE FROM usuarios WHERE id = ?", resultado.getSql());
        assertEquals(1, resultado.getListParameters().size());
        assertEquals(5, resultado.getListParameters().get(0));
    }

    @Test
    public void deleteConAndWhere() throws InvalidSqlGenerationException {
        SqlParameter resultado = new Delete()
                .from("sesiones")
                .where("usuario_id = :usuarioId", p -> p.put("usuarioId", 10))
                .andWhere("activa = :activa", p -> p.put("activa", false))
                .getSqlAndParameters();

        verificar("DELETE FROM sesiones WHERE usuario_id = ? AND activa = ?", resultado.getSql());
        assertEquals(2, resultado.getListParameters().size());
        assertEquals(10, resultado.getListParameters().get(0));
        assertEquals(false, resultado.getListParameters().get(1));
    }

    /**
     * BUG-5 corregido: WHERE IN expande un placeholder por cada elemento de la
     * colección (igual que en Selector).
     */
    @Test
    public void deleteWhereInExpandePlaceholders() throws InvalidSqlGenerationException {
        SqlParameter resultado = new Delete()
                .from("etiquetas")
                .where("id IN (:ids)", p -> p.put("ids", Arrays.asList(1, 2, 3)))
                .getSqlAndParameters();

        verificar("DELETE FROM etiquetas WHERE id IN (?,?,?)", resultado.getSql());
        assertEquals(3, resultado.getListParameters().size());
        assertEquals(1, resultado.getListParameters().get(0));
        assertEquals(2, resultado.getListParameters().get(1));
        assertEquals(3, resultado.getListParameters().get(2));
    }

    /**
     * BUG-8 corregido: Tables.write() para DELETE ahora solo incluye la tabla
     * principal en el FROM; las tablas de JOIN se agregan con su cláusula JOIN
     * en vez de duplicarse en el FROM.
     */
    @Test
    public void deleteConJoinNoDuplicaTablaEnFrom() throws InvalidSqlGenerationException {
        SqlParameter resultado = new Delete()
                .from("pedidos p")
                .join(Join.INNER, "clientes c", "c.id = p.cliente_id")
                .where("c.activo = :activo", p -> p.put("activo", false))
                .getSqlAndParameters();

        verificar("DELETE FROM pedidos p INNER JOIN clientes c ON c.id = p.cliente_id WHERE c.activo = ?",
                resultado.getSql());
    }

    @Test
    public void deleteConMultiplesTablas() throws InvalidSqlGenerationException {
        // from() acepta varargs — múltiples tablas en el DELETE
        SqlParameter resultado = new Delete()
                .from("logs_acceso", "logs_error")
                .where("created_at < :fecha", p -> p.put("fecha", "2024-01-01"))
                .getSqlAndParameters();

        assertTrue("El SQL debe contener logs_acceso", resultado.getSql().contains("logs_acceso"));
        assertTrue("El SQL debe contener logs_error",  resultado.getSql().contains("logs_error"));
        assertTrue("El SQL debe contener WHERE",       resultado.getSql().contains("WHERE"));
        assertEquals(1, resultado.getListParameters().size());
    }

    @Test
    public void deleteConVariosAndWhere() throws InvalidSqlGenerationException {
        SqlParameter resultado = new Delete()
                .from("notificaciones")
                .where("usuario_id = :uid", p -> p.put("uid", 99))
                .andWhere("leida = :leida",  p -> p.put("leida", true))
                .andWhere("tipo = :tipo",    p -> p.put("tipo", "PROMO"))
                .getSqlAndParameters();

        verificar("DELETE FROM notificaciones WHERE usuario_id = ? AND leida = ? AND tipo = ?",
                resultado.getSql());
        assertEquals(3, resultado.getListParameters().size());
        assertEquals(99,     resultado.getListParameters().get(0));
        assertEquals(true,   resultado.getListParameters().get(1));
        assertEquals("PROMO",resultado.getListParameters().get(2));
    }

    /**
     * BUG-8 corregido (mismo): LEFT JOIN tampoco duplica la tabla en el FROM.
     */
    @Test
    public void deleteConLeftJoinNoDuplicaTablaEnFrom() throws InvalidSqlGenerationException {
        SqlParameter resultado = new Delete()
                .from("usuarios u")
                .join(Join.LEFT, "pedidos p", "p.usuario_id = u.id")
                .where("p.id IS NULL", p -> {})
                .getSqlAndParameters();

        verificar("DELETE FROM usuarios u LEFT JOIN pedidos p ON p.usuario_id = u.id WHERE p.id IS NULL",
                resultado.getSql());
    }

    // -------------------------------------------------------------------------
    // Casos de error esperados
    // -------------------------------------------------------------------------

    @Test(expected = InvalidSqlGenerationException.class)
    public void deleteSinWhereLanzaExcepcion() throws InvalidSqlGenerationException {
        // La librería protege contra DELETE sin WHERE
        new Delete()
                .from("usuarios")
                .getSqlAndParameters();
    }

    @Test(expected = InvalidSqlGenerationException.class)
    public void deleteSinTablaLanzaExcepcion() throws InvalidSqlGenerationException {
        // No se especificó ninguna tabla
        new Delete()
                .where("id = :id", p -> p.put("id", 1))
                .getSqlAndParameters();
    }

    // -------------------------------------------------------------------------
    // Utilidad
    // -------------------------------------------------------------------------

    private void verificar(String esperado, String actual) {
        System.out.println("Esperado : " + esperado);
        System.out.println("Actual   : " + actual);
        assertEquals(esperado, actual);
    }
}
