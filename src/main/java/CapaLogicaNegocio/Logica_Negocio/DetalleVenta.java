package CapaLogicaNegocio.Logica_Negocio;

/**
 * Representa una DetalleVenta dentro del sistema.
 * Esta entidad almacena y envia la informacion necesaria donde sea solicitada.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class DetalleVenta {

    private String id, idProducto, idVenta;
    private Long cantidad;
    private Double subtotal, precioVenta;

    public DetalleVenta(){ }

    public DetalleVenta(String id, Long cantidad, Double subtotal, Double precioVenta, String idProducto, String idVenta) {
        this.id = id;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.precioVenta = precioVenta;
        this.idProducto = idProducto;
        this.idVenta = idVenta;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(String idVenta) {
        this.idVenta = idVenta;
    }
}
