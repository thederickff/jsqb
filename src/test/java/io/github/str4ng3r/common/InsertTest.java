package io.github.str4ng3r.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.github.str4ng3r.exceptions.InvalidSqlGenerationException;

/**
 * Tests para la clase Insert.
 *
 * BUGS DOCUMENTADOS:
 *   BUG-1 corregido: El constructor Insert(String table) ahora asigna
 *           this.table = table, de modo que el SQL generado usa el nombre
 *           real de la tabla en vez de "null".
 */
public class InsertTest {

    // -------------------------------------------------------------------------
    // Casos felices
    // -------------------------------------------------------------------------

    @Test
    public void insertBasico() throws InvalidSqlGenerationException {
        String sql = new Insert()
                .setTable("usuarios")
                .setColumns("nombre")
                .setValues("Pablo")
                .getSql();

        verificar("INSERT INTO usuarios ( nombre )  VALUES (? )", sql);
    }

    @Test
    public void insertVariasColumnasYValores() throws InvalidSqlGenerationException {
        String sql = new Insert()
                .setTable("usuarios")
                .setColumns("id", "nombre", "email")
                .setValues(1, "Pablo", "pablo@ejemplo.com")
                .getSql();

        verificar("INSERT INTO usuarios ( id,nombre,email )  VALUES (?,?,? )", sql);
    }

    @Test
    public void insertUnSoloValor() throws InvalidSqlGenerationException {
        // Borde: exactamente un valor (no debe añadir coma al final)
        String sql = new Insert()
                .setTable("config")
                .setColumns("clave")
                .setValues("activo")
                .getSql();

        verificar("INSERT INTO config ( clave )  VALUES (? )", sql);
    }

    @Test
    public void insertConEsquema() throws InvalidSqlGenerationException {
        String sql = new Insert()
                .setTable("mi_esquema.usuarios")
                .setColumns("nombre")
                .setValues("Ana")
                .getSql();

        assertTrue("El SQL debe contener el nombre completo de tabla con esquema",
                sql.contains("mi_esquema.usuarios"));
    }

    @Test
    public void insertConDosValoresGeneraPlaceholdersCorrectos() throws InvalidSqlGenerationException {
        String sql = new Insert()
                .setTable("roles")
                .setColumns("id", "nombre")
                .setValues(10, "admin")
                .getSql();

        // Debe haber exactamente dos '?'
        long conteo = sql.chars().filter(c -> c == '?').count();
        assertEquals("Debe haber exactamente 2 placeholders '?'", 2, conteo);
    }

    // -------------------------------------------------------------------------
    // BUG-1 corregido: constructor Insert(String) asigna la tabla
    // -------------------------------------------------------------------------

    /**
     * BUG-1 corregido: Insert(String table) ahora asigna this.table, por lo que
     * el SQL generado usa el nombre real de la tabla.
     */
    @Test
    public void constructorConTablaAsignaCorrectamente() throws InvalidSqlGenerationException {
        String sql = new Insert("usuarios")
                .setColumns("nombre")
                .setValues("Pablo")
                .getSql();

        verificar("INSERT INTO usuarios ( nombre )  VALUES (? )", sql);
    }

    // -------------------------------------------------------------------------
    // Validación de simetría columnas/valores
    // -------------------------------------------------------------------------

    @Test(expected = InvalidSqlGenerationException.class)
    public void insertConMasColumnasQueValoresLanzaExcepcion() throws InvalidSqlGenerationException {
        new Insert()
                .setTable("usuarios")
                .setColumns("id", "nombre", "email")
                .setValues(1, "Pablo")
                .getSql();
    }

    @Test(expected = InvalidSqlGenerationException.class)
    public void insertConMasValoresQueColumnasLanzaExcepcion() throws InvalidSqlGenerationException {
        new Insert()
                .setTable("usuarios")
                .setColumns("id", "nombre")
                .setValues(1, "Pablo", "extra")
                .getSql();
    }

    @Test(expected = InvalidSqlGenerationException.class)
    public void insertSinTablaLanzaExcepcion() throws InvalidSqlGenerationException {
        new Insert()
                .setColumns("nombre")
                .setValues("Pablo")
                .getSql();
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
