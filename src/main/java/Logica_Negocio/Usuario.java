package Logica_Negocio;

/**
 *
 * @author Santiago Lopez Patron Template Method
 */

/**
 * Clase que moldea a un Usuario geneal en el sistema, esta sirve como base para implementaciones concretas.
 */
public abstract class Usuario {

    private String id;
    private String usuario;
    private String contrasenha;
    private Boolean estado;

    public Usuario(){ }

    public Usuario(String id, String usuario, String contrasenha, Boolean estado) {
        this.id = id;
        this.usuario = usuario;
        this.contrasenha = contrasenha;
        this.estado = estado;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
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
