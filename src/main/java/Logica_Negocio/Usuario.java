package Logica_Negocio;

import Helpers.HelperCifrado;
/**
 *
 * @author Santiago Lopez Patron Template Method
 */

/**
 * Clase que moldea a un Usuario geneal en el sistema, esta sirve como base para implementaciones concretas.
 */
public abstract class Usuario {
    
    private String usuario;
    private String contrasenha;

    public Usuario(String usuario, String contrasenha) {
        this.usuario = usuario;
        this.contrasenha = contrasenha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasenha() {
        return contrasenha;
    }

    public void setContrasenha(String contrasenha) {
        this.contrasenha = contrasenha;
    }

    /**
     * Verifica si las credenciales ingresadas coinciden con las almacenadas, este es un metodo abstracto comun.
     * @param usuario usuarioa cifrado que se recibe para validacion
     * @param contrasenha contrasenha cifrada que se recibe
     * @return true si coinciden, false si no.
     */
    public abstract boolean  LogOn(String usuario, String contrasenha);
}
