package Logica_Negocio;

public class Promocion {

    private String id;
    private Double stockActual;
    private Double diasSinVender;
    private Double totalVendido;
    private int cluster;

    public Promocion() {}

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
