package Pruebas_Unit;

import CapaLogicaNegocio.Helpers.HelperValidacion;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Santiago Lopez
 */
public class Pruebas_JUnit {

    @Test
    public void testCadenaVacia() {
        assertEquals(1, HelperValidacion.ValidarTodoLetra(""));
    }

    @Test
    public void testSoloLetras() {
        // No vacía, solo letras, sin caracteres especiales: 0 + 0 + 0.
        assertEquals(0, HelperValidacion.ValidarTodoLetra("HolaMundo"));
    }

    @Test
    public void testConCaracterEspecial() {
        // Contiene un carácter que no es letra y un especial reconocido: 0 + 1 + 1.
        assertEquals(2, HelperValidacion.ValidarTodoLetra("Hola@mundo"));
    }

    @Test
    public void testConVariosEspeciales() {
        // '!' activa el indicador de especiales; '+' y '.' no están en su lista.
        assertEquals(2, HelperValidacion.ValidarTodoLetra("Hola!mundo+."));
    }

    @Test
    public void testConNumeroYNoEspecial() {
        // Los dígitos activan RetornarLetra, pero no el indicador de especiales.
        assertEquals(1, HelperValidacion.ValidarTodoLetra("Hola123"));
    }

    @Test
    public void testSoloCaracteresEspeciales() {
        // '@' y ';' están en la lista: el indicador vale 1, no su cantidad.
        assertEquals(2, HelperValidacion.ValidarTodoLetra("@+;."));
    }

    @Test
    public void testConEspaciosYLetras() {
        // El espacio interior no es letra y sí está en la lista de especiales.
        assertEquals(2, HelperValidacion.ValidarTodoLetra("Hola mundo"));
    }

    @Test
    @Disabled("Demostración de fallo: se espera 5 deliberadamente, pero el resultado es 2")
    public void testSoloCaracteresEspecialesIncorrecto() {
        assertEquals(5, HelperValidacion.ValidarTodoLetra("@+;."));
    }

    @Test
    public void testConTodos() {
        // validarTodo empieza con minúscula. Hay dígitos y especiales: 0 + 1 + 1.
        assertEquals(2, HelperValidacion.validarTodo("@Hola123: mundo!"));
    }
}
