package Logica_Negocio;

import Helpers.HelperCifrado;

/**
 *
 * @author Santiago Lopez Patron Template Method
 */

/**
 * Clase que modela a los usuarios con el rol de Persona
 */
public class Persona extends Usuario {

    public Persona(){ }

    public Persona(String id, String usuario, String contrasenha, Boolean estado) {
        super(id, usuario, contrasenha, estado);
    }

    /**
     * Verifica si las credenciales ingresadas coinciden con las almacenadas
     * @param usuario usuario cifrado que se recibe
     * @param contrasenha contrasenha cifrada que se recibe
     * @return true si coinciden, false si no.
     */
    public boolean LogOn(String usuario, String contrasenha) {

        String comprobarUsuario = HelperCifrado.CifrarSHA256(getUsuario());
        String comprobarContrasenha = HelperCifrado.CifrarSHA256(getContrasenha());
        System.out.println("usuario cifrado: " + "\t" + comprobarUsuario);
        System.out.println("contrasenha cifrada" + "\t" + comprobarContrasenha);

        return (comprobarUsuario.equals(usuario) && comprobarContrasenha.equals(contrasenha));
    }
}
