package utilidades;

import CapaLogicaNegocio.Logica_Negocio.CredencialesClienteDemo;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccesoDatosDemoTest {
    @Test void losClientesCargadosTienenAccesoDeDemostracion() {
        for (var cliente : CargarDatosDemo.clientes()) {
            String usuario = ((String) cliente.get("nombre")).toLowerCase(Locale.ROOT);
            assertTrue(CredencialesClienteDemo.esUsuarioCliente(usuario));
            assertTrue(CredencialesClienteDemo.validar(usuario, usuario));
        }
    }
}
