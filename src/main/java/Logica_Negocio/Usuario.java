package Logica_Negocio;

/**
 *
 * @author Santiago Lopez Patron Template Method
 */
public abstract class Usuario {

    private String id;
    private String email;
    private String contrasenha;
    private String estado;
    private String clienteID;

    public Usuario(){ }

    public Usuario(String id, String email, String contrasenha, String estado, String clienteID) {
        this.id = id;
        this.email = email;
        this.contrasenha = contrasenha;
        this.estado = estado;
        this.clienteID = clienteID;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasenha() {
        return contrasenha;
    }

    public void setContrasenha(String contrasenha) {
        this.contrasenha = contrasenha;
    }

    public String getEstado(){
        return estado;
    }

    public void setEstado(String estado){
        this.estado = estado;
    }

    public String getClienteID() {
        return clienteID;
    }

    public void setClienteID(String clienteID) {
        this.clienteID = clienteID;
    }

    /**
     * Verifica si las credenciales ingresadas coinciden con las almacenadas, este es un metodo abstracto comun.
     * @param email email cifrado que se recibe para validacion
     * @param contrasenha contrasenha cifrada que se recibe
     * @return true si coinciden, false si no.
     */
    public abstract boolean  LogOn(String email, String contrasenha);
}
