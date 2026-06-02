package CapaLogicaNegocio.Logica_Negocio;

/**
 * Entidad que representa el vinculo entre una Promocion y el Producto al que aplica.
 * En local vive en la tabla PromocionProducto; en la nube se replica en la coleccion
 * homonima para que el modelo de datos tenga paridad entre ambas bases.
 *
 * Tiene constructor vacio + getters/setters para que Firestore pueda mapearla con
 * toObject() y Gson pueda serializarla/deserializarla en la cola de sincronizacion.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class PromocionProducto {

    private String id;
    private String idPromocion;
    private String idProducto;

    public PromocionProducto() { }

    public PromocionProducto(String id, String idPromocion, String idProducto) {
        this.id = id;
        this.idPromocion = idPromocion;
        this.idProducto = idProducto;
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

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }
}
