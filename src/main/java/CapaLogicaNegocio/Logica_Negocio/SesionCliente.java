package CapaLogicaNegocio.Logica_Negocio;

/**
 * Mantiene en memoria al cliente que tiene la sesion activa en el modulo Cliente (GUI_Cliente).
 * El modulo Cliente trabaja UNICAMENTE contra la nube, asi que aqui solo se guarda la identidad
 * (con un id en formato UUID valido) que necesita el CompraControlador para registrar las ventas
 * en Firebase. Es un punto de acceso estatico y vive solo durante la ejecucion de la app.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class SesionCliente {

    // Cliente demo por defecto: el login actual es de demostracion (usuario "Persona") y no esta
    // ligado todavia a un Cliente real, por eso se usa un id (UUID) fijo y valido para poder
    // registrar las compras del cliente en la nube.
    private static final String ID_CLIENTE_DEMO = "11111111-1111-1111-1111-111111111111";

    private static Cliente clienteActual;

    private SesionCliente() { }

    /**
     * Inicia la sesion con el cliente pasado por argumento.
     * @param cliente el cliente que acaba de iniciar sesion
     */
    public static void iniciarSesion(Cliente cliente) {
        clienteActual = cliente;
    }

    /**
     * Devuelve el cliente con la sesion activa. Si no se ha iniciado ninguna sesion explicita,
     * devuelve un cliente demo con un id (UUID) valido para que las compras puedan registrarse.
     * @return el cliente con la sesion activa (nunca null)
     */
    public static Cliente getClienteActual() {
        if (clienteActual == null) {
            clienteActual = new Cliente(ID_CLIENTE_DEMO, "Cliente", "Demo", "0000000000", "N/A");
        }
        return clienteActual;
    }

    /**
     * Cierra la sesion del cliente actual.
     */
    public static void cerrarSesion() {
        clienteActual = null;
    }
}
