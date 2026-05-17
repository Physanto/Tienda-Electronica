package CapaLogicaNegocio.Logica_Negocio;

/**
 * Representa una Sincronizadora dentro del sistema.
 * Esta entidad almacena y envia la informacion necesaria donde sea solicitada
 *
 * @author Manuel Figueroa (Physanto)
 */
public class Sincronizadora {

    public enum Accion { INSERT, UPDATE, DELETE};

    private String id, idRegistroAfectado;
    private Accion accion;
    private String tablaAfectada;
    private String registroJson;
    private String estado;

    public Sincronizadora(){ }

    public Sincronizadora(String id, Accion accion, String tablaAfectada, String idRegistroAfectado, String registroJson, String estado) {
        this.id = id;
        this.accion = accion;
        this.tablaAfectada = tablaAfectada;
        this.idRegistroAfectado = idRegistroAfectado;
        this.registroJson = registroJson;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Accion getAccion() {
        return accion;
    }

    public void setAccion(Accion accion) {
        this.accion = accion;
    }

    public String getTablaAfectada() {
        return tablaAfectada;
    }

    public void setTablaAfectada(String tablaAfectada) {
        this.tablaAfectada = tablaAfectada;
    }

    public String getIdRegistroAfectado() {
        return idRegistroAfectado;
    }

    public void setIdRegistroAfectado(String idRegistroAfectado) {
        this.idRegistroAfectado = idRegistroAfectado;
    }

    public String getRegistroJson() {
        return registroJson;
    }

    public void setRegistroJson(String registroJson) {
        this.registroJson = registroJson;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
