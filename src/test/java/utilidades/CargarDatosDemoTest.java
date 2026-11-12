package utilidades;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.HashSet;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CargarDatosDemoTest {
    @Test
    void generaCienClientesDeterministasSinColisiones() {
        var clientes = CargarDatosDemo.clientes();
        assertEquals(100, clientes.size());
        assertEquals(clientes, CargarDatosDemo.clientes());
        var identificadores = new HashSet<String>();
        var cedulas = new HashSet<String>();
        for (var cliente : clientes) {
            String id = (String) cliente.get("id");
            assertEquals(id, UUID.fromString(id).toString());
            assertTrue(identificadores.add(id));
            assertTrue(cedulas.add((String) cliente.get("cedula")));
            assertEquals(6, cliente.size());
            assertTrue(((String) cliente.get("direccion")).contains("ficticia"));
            assertNull(cliente.get("urlImg"));
        }
    }
}
