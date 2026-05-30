package CapaLogicaNegocio.Logica_Negocio;

public class PromocionProducto {

    private String id;
    private String idPromocion;
    private String idProducto;

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
