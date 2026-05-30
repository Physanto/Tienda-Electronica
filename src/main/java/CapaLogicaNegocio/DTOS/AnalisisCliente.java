package CapaLogicaNegocio.DTOS;

/**
 * Clase que para modelar el analisis de un cliente en el sistema
 *
 * @author Manuel Figueroa (Physanto)
 */
public class AnalisisCliente {

    private String id;
    private Double ultDiaCompra;
    private Double cantCompras;
    private Double dineroGastado;
    private int cluster;
    private String etiquetaNegocio;
    private String descuentoRecomendado;

    public AnalisisCliente() {}

    public AnalisisCliente(String id, Double ultDiaCompra, Double cantCompras, Double dineroGastado){
        this.id = id;
        this.ultDiaCompra = ultDiaCompra;
        this.cantCompras = cantCompras;
        this.dineroGastado = dineroGastado;
    }

    public AnalisisCliente(int cluster, Double ultDiaCompra, Double cantCompras, Double dineroGastado) {
        this.cluster = cluster;
        this.ultDiaCompra = ultDiaCompra;
        this.cantCompras = cantCompras;
        this.dineroGastado = dineroGastado;
    }
    public AnalisisCliente(String id, Double ultDiaCompra, Double cantCompras, Double dineroGastado, int cluster, String etiquetaNegocio, String descuentoRecomendado) {
        this.id = id;
        this.ultDiaCompra = ultDiaCompra;
        this.cantCompras = cantCompras;
        this.dineroGastado = dineroGastado;
        this.cluster = cluster;
        this.etiquetaNegocio = etiquetaNegocio;
        this.descuentoRecomendado = descuentoRecomendado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getUltDiaCompra() {
        return ultDiaCompra;
    }

    public void setUltDiaCompra(Double ultDiaCompra) {
        this.ultDiaCompra = ultDiaCompra;
    }

    public Double getCantCompras() {
        return cantCompras;
    }

    public void setCantCompras(Double cantCompras) {
        this.cantCompras = cantCompras;
    }

    public Double getDineroGastado() {
        return dineroGastado;
    }

    public void setDineroGastado(Double dineroGastado) {
        this.dineroGastado = dineroGastado;
    }

    public int getCluster() {
        return cluster;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }

    public String getEtiquetaNegocio() {
        return etiquetaNegocio;
    }

    public void setEtiquetaNegocio(String etiquetaNegocio) {
        this.etiquetaNegocio = etiquetaNegocio;
    }

    public String getDescuentoRecomendado() {
        return descuentoRecomendado;
    }

    public void setDescuentoRecomendado(String descuentoRecomendado) {
        this.descuentoRecomendado = descuentoRecomendado;
    }
}
