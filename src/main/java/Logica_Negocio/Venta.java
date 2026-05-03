package Logica_Negocio;

import java.sql.Timestamp;

public class Venta {

    public enum MetodoPago { EFECTIVO, TARJETA };

    private int id;
    private Timestamp fechaVenta;
    private double totalVenta;
    private MetodoPago metodoPago;

    public Venta(){ }

    public Venta(int id, Timestamp fechaVenta, double totalVenta) {
        this.id = id;
        this.fechaVenta = fechaVenta;
        this.totalVenta = totalVenta;
    }

    public MetodoPago getMetodoPago(){
        return metodoPago;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(Timestamp fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public double getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(double totalVenta) {
        this.totalVenta = totalVenta;
    }
}
