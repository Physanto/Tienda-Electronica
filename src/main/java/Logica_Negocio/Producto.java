package Logica_Negocio;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 *
 * @author Santiago Lopez
 */


/**
 * Clase que moldea un Producto en el sistema
 */
public class Producto {

    private int id, stock, idCategoria;
    private BigDecimal precioActual;
    private String codigo, nombre, marca, serial, nombreImg;
    private Timestamp fechaVencimiento;

    public Producto() { }

    public Producto(int id, String codigo, String nombre, String marca, String serial, int stock, BigDecimal precioActual, Timestamp fechaVencimiento, String nombreImg, int idCategoria) {
        this.id = id;
        this.stock = stock;
        this.precioActual = precioActual;
        this.codigo = codigo;
        this.nombre = nombre;
        this.marca = marca;
        this.serial = serial;
        this.nombreImg = nombreImg;
        this.fechaVencimiento = fechaVencimiento;
        this.idCategoria = idCategoria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public BigDecimal getPrecioActual() {
        return precioActual;
    }

    public void setPrecioActual(BigDecimal precioActual) {
        this.precioActual = precioActual;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getNombreImg() {
        return nombreImg;
    }

    public void setNombreImg(String nombreImg) {
        this.nombreImg = nombreImg;
    }

    public Timestamp getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Timestamp fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public int getIdCategoria() { return idCategoria; }

    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
}
