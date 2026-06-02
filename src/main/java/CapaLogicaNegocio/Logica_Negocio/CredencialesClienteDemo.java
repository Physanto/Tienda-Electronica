package CapaLogicaNegocio.Logica_Negocio;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Credenciales de demostracion para el modulo Cliente.
 *
 * <p>
 * Por ahora el cliente no puede registrarse desde la app, asi que para la demo
 * se definen
 * directamente en el programa (no se leen de la base de datos) un conjunto de
 * usuarios cuyo
 * usuario y contrasenha coinciden con el nombre del cliente (ej. usuario
 * "juan", contrasenha
 * "juan"). Al iniciar sesion, el sistema valida estas credenciales aqui y luego
 * busca en la nube
 * el {@link Cliente} cuyo nombre coincide con el usuario para asociar la sesion
 * al cliente real
 * (con su id real). Asi "juan" inicia sesion como el cliente llamado "juan" en
 * la base de datos.
 * </p>
 *
 * @author Manuel Figueroa (Physanto)
 */
public final class CredencialesClienteDemo {

    // Usuario -> contrasenha. El orden de insercion se conserva (LinkedHashMap)
    // solo por claridad.
    private static final Map<String, String> CREDENCIALES = new LinkedHashMap<>();

    static {
        for (String nombre : new String[] {
                "juan", "pedro", "matias", "jordan", "lucas",
                "luis", "lian", "francisco", "luisa", "andrea" }) {
            // Para la demo el usuario y la contrasenha son iguales al nombre del cliente.
            CREDENCIALES.put(nombre, nombre);
        }
    }

    private CredencialesClienteDemo() {
    }

    /**
     * Indica si el usuario corresponde a uno de los clientes de demostracion (sin
     * importar
     * mayusculas/minusculas).
     * 
     * @param usuario nombre de usuario ingresado en el login
     * @return true si el usuario es un cliente de demo
     */
    public static boolean esUsuarioCliente(String usuario) {
        if (usuario == null)
            return false;
        return CREDENCIALES.containsKey(usuario.trim().toLowerCase());
    }

    /**
     * Valida que el par usuario/contrasenha coincida con una credencial de
     * demostracion.
     * 
     * @param usuario     nombre de usuario ingresado
     * @param contrasenha contrasenha ingresada
     * @return true si las credenciales son validas
     */
    public static boolean validar(String usuario, String contrasenha) {
        if (usuario == null || contrasenha == null)
            return false;
        String esperada = CREDENCIALES.get(usuario.trim().toLowerCase());
        return esperada != null && esperada.equals(contrasenha);
    }
}
