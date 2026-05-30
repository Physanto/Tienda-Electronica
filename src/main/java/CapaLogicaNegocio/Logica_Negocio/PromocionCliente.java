package CapaLogicaNegocio.Logica_Negocio;

public class PromocionCliente {

    private String id;
    private String idPromocion;
    private String idCliente;

    public PromocionCliente(){ }

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
