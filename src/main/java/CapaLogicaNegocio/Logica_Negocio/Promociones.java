package CapaLogicaNegocio.Logica_Negocio;

import java.util.Date;

public class Promociones {

    public enum TipoPromocion {GENERAL, ESPECIFICA};

    private String id, nombre;
    private Double descuento;
    private Date fechaInicio;
    private Date fechaFin;
    private TipoPromocion tipo;

    public Promociones() {}

    public Promociones(String id, String nombre, Double descuento, Date fechaInicio, Date fechaFin, TipoPromocion tipo) {
        this.id = id;
        this.nombre = nombre;
        this.descuento = descuento;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getDescuento() {
        return descuento;
    }

    public void setDescuento(Double descuento) {
        this.descuento = descuento;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public TipoPromocion getTipo() {
        return tipo;
    }

    public void setTipo(TipoPromocion tipo) {
        this.tipo = tipo;
    }
}
