package CapaLogicaNegocio.Logica_Negocio;

/**
 * Clase que sirve para modelar los datos que se necesitan para la IA de las promociones
 *
 * @author Manuel Figueroa (Physanto)
 */
public class Promocion {

    private String id;
    private Double stockActual;
    private Double diasSinVender;
    private Double totalVendido;
    private int cluster;
    double puntajeRiesgo;
    String clasificacion;

    public Promocion() {}

    public Promocion(int cluster, Double stockActual, Double diasSinVender, Double totalVendido){
        this.cluster = cluster;
        this.stockActual = stockActual;
        this.diasSinVender = diasSinVender;
        this.totalVendido = totalVendido;
        puntajeRiesgo = stockActual * diasSinVender;
    }

    public Promocion(String id, Double stockActual, Double diasSinVender, Double totalVendido) {
        this.id = id;
        this.stockActual = stockActual;
        this.diasSinVender = diasSinVender;
        this.totalVendido = totalVendido;
    }

    public Promocion(String id, Double stockActual, Double diasSinVender, Double totalVendido, int cluster) {
        this.id = id;
        this.stockActual = stockActual;
        this.diasSinVender = diasSinVender;
        this.totalVendido = totalVendido;
        this.cluster = cluster;
    }

    @Override
    public String toString() {
        return "Promocion{" +
                "id='" + id + '\'' +
                ", stockActual=" + stockActual +
                ", diasSinVender=" + diasSinVender +
                ", totalVendido=" + totalVendido +
                ", cluster=" + cluster +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getStockActual() {
        return stockActual;
    }

    public void setStockActual(Double stockActual) {
        this.stockActual = stockActual;
    }

    public Double getDiasSinVender() {
        return diasSinVender;
    }

    public void setDiasSinVender(Double diasSinVender) {
        this.diasSinVender = diasSinVender;
    }

    public Double getTotalVendido() {
        return totalVendido;
    }

    public double getPuntajeRiesgo() {
        return puntajeRiesgo;
    }

    public void setPuntajeRiesgo(double puntajeRiesgo) {
        this.puntajeRiesgo = puntajeRiesgo;
    }

    public String getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }

    public void setTotalVendido(Double totalVendido) {
        this.totalVendido = totalVendido;
    }

    public int getCluster() {
        return cluster;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }
}
