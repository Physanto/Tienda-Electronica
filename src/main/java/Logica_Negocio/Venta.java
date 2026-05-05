package Logica_Negocio;

import java.util.Date;

public class Venta {

    public enum MetodoPago { EFECTIVO, TARJETA };

    private String idVenta;
    private Date fechaVenta;
    private Double totalVenta;
    private MetodoPago metodoPago;
    private String idCliente;

    public Venta(){ }

    public Venta(String idVenta, Date fechaVenta, Double totalVenta, MetodoPago metodoPago, String idCliente) {
        this.idVenta = idVenta;
        this.fechaVenta = fechaVenta;
        this.totalVenta = totalVenta;
        this.metodoPago = metodoPago;
        this.idCliente = idCliente;
    }

    public String getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(String idVenta) {
        this.idVenta = idVenta;
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
