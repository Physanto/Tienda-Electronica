package CapaLogicaNegocio.Logica_Negocio;

import java.util.Date;

/**
 *
 * @author Santiago Lopez
 */
public class Producto {

    private Long stock;
    private Double precioActual;
    private String id, idCategoria, codigo, nombre, marca, serie, urlImg;
    private Date fechaVencimiento;

    public Producto() { }

    public Producto(String id, String codigo, String nombre, String marca, String serie, Long stock, Double precioActual, Date fechaVencimiento, String urlImg, String idCategoria) {
        this.id = id;
        this.stock = stock;
        this.precioActual = precioActual;
        this.codigo = codigo;
        this.nombre = nombre;
        this.marca = marca;
        this.serie = serie;
        this.urlImg = urlImg;
        this.fechaVencimiento = fechaVencimiento;
        this.idCategoria = idCategoria;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public Double getPrecioActual() {
        return precioActual;
    }

    public void setPrecioActual(Double precioActual) {
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

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String nombreImg) {
        this.urlImg = nombreImg;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getIdCategoria() { return idCategoria; }

    public void setIdCategoria(String idCategoria) { this.idCategoria = idCategoria; }
}
