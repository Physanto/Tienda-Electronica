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

    private String idUsuario;
    private String usuario;
    private String contrasenha;
    private Boolean estado;

    public Usuario(){ }

    public Usuario(String idUsuario, String usuario, String contrasenha, Boolean estado) {
        this.idUsuario = idUsuario;
        this.usuario = usuario;
        this.contrasenha = contrasenha;
        this.estado = estado;
    }

    public String getIdUsuario(){
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario){
        this.idUsuario = idUsuario;
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

    public Boolean getEstado(){
        return estado;
    }

    public void setEstado(Boolean estado){
        this.estado = estado;
    }

    /**
     * Verifica si las credenciales ingresadas coinciden con las almacenadas, este es un metodo abstracto comun.
     * @param usuario usuarioa cifrado que se recibe para validacion
     * @param contrasenha contrasenha cifrada que se recibe
     * @return true si coinciden, false si no.
     */
    public abstract boolean  LogOn(String usuario, String contrasenha);
}
