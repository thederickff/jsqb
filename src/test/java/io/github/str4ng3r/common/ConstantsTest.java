package io.github.str4ng3r.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * Tests para Constants, incluyendo un caso de concurrencia que verifica que la
 * inicialización del diccionario de dialectos (bloque static + mapa inmutable)
 * es segura frente a accesos concurrentes.
 */
public class ConstantsTest {

    @Test
    public void getActionDevuelvePaginacionPorDialecto() {
        Constants c = new Constants();
        c.setDialect(Constants.SqlDialect.Oracle);
        assertEquals(" OFFSET :low ROWS FETCH NEXT :upper ROWS ONLY",
                c.getAction(Constants.Actions.PAGINATION));
    }

    @Test
    public void getActionCaeEnSqlPorDefectoCuandoNoHayClaveEspecifica() {
        Constants c = new Constants();
        c.setDialect(Constants.SqlDialect.Mysql);
        // MySQL no define SEPARATOR, así que debe caer en el valor de SQL ("")
        assertEquals("", c.getAction(Constants.Actions.SEPARATOR));
    }

    /**
     * Antes del fix, dialectConstants se llenaba perezosamente en el constructor
     * con un guard 'if (!isEmpty()) return', lo que permitía que un hilo leyera
     * el mapa a medio construir. Este test crea muchos Constants en paralelo y
     * lee el mapa de forma concurrente; con la inicialización en bloque static
     * el mapa siempre está completo e inmutable.
     */
    @Test
    public void inicializacionConcurrenteEsSegura() throws InterruptedException {
        final int hilos = 32;
        final CountDownLatch arranque = new CountDownLatch(1);
        final CountDownLatch fin = new CountDownLatch(hilos);
        final ConcurrentLinkedQueue<Throwable> errores = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < hilos; i++) {
            new Thread(() -> {
                try {
                    arranque.await(); // todos arrancan a la vez para maximizar contención
                    Constants c = new Constants();
                    c.setDialect(Constants.SqlDialect.Oracle);
                    String v = c.getAction(Constants.Actions.PAGINATION);
                    assertNotNull("El valor de paginación no debe ser null", v);
                    assertTrue(v.contains("OFFSET"));
                    // El mapa compartido debe estar completo
                    assertEquals(6, Constants.dialectConstants.size());
                } catch (Throwable t) {
                    errores.add(t);
                } finally {
                    fin.countDown();
                }
            }).start();
        }

        arranque.countDown();
        assertTrue("Los hilos no terminaron a tiempo", fin.await(10, TimeUnit.SECONDS));
        assertTrue("Hubo errores en acceso concurrente: " + errores, errores.isEmpty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void dialectConstantsEsInmutable() {
        Constants.dialectConstants.put("x", "y");
    }
}
