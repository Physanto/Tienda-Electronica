package Logica_Negocio;

import java.util.Date;

/**
 * Representa una venta dentro del sistema.
 * Esta entidad almacena y envia la informacion necesaria donde sea solicitada
 *
 * @author Manuel Figueroa (Physanto)
 */
public class Venta {

    public enum MetodoPago { EFECTIVO, TARJETA };

    private String id;
    private Date fechaVenta;
    private Double totalVenta;
    private MetodoPago metodoPago;
    private String idCliente;

    public Venta(){ }

    public Venta(String id, Date fechaVenta, Double totalVenta, MetodoPago metodoPago, String idCliente) {
        this.id = id;
        this.fechaVenta = fechaVenta;
        this.totalVenta = totalVenta;
        this.metodoPago = metodoPago;
        this.idCliente = idCliente;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(Date fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public Double getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(Double totalVenta) {
        this.totalVenta = totalVenta;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }
}
