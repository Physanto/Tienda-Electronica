package CapaLogicaNegocio.Logica_Negocio;

/**
 * Entidad que representa el vinculo entre una Promocion y el Cliente al que aplica.
 * En local vive en la tabla PromocionCliente; en la nube se replica en la coleccion
 * homonima para que el modelo de datos tenga paridad entre ambas bases.
 *
 * Tiene constructor vacio + getters/setters para que Firestore pueda mapearla con
 * toObject() y Gson pueda serializarla/deserializarla en la cola de sincronizacion.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class PromocionCliente {

    private String id;
    private String idPromocion;
    private String idCliente;

    public PromocionCliente() { }

    public PromocionCliente(String id, String idPromocion, String idCliente) {
        this.id = id;
        this.idPromocion = idPromocion;
        this.idCliente = idCliente;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdPromocion() {
        return idPromocion;
    }

    public void setIdPromocion(String idPromocion) {
        this.idPromocion = idPromocion;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }
}
