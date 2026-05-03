package Logica_Negocio;

public class Detalle_Venta {

    private int id, cantidad;
    private double decimal, precioVenta;

    public Detalle_Venta(){ }

    public Detalle_Venta(int id, int cantidad, double decimal, double precioVenta) {
        this.id = id;
        this.cantidad = cantidad;
        this.decimal = decimal;
        this.precioVenta = precioVenta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getDecimal() {
        return decimal;
    }

    public void setDecimal(double decimal) {
        this.decimal = decimal;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }
}
