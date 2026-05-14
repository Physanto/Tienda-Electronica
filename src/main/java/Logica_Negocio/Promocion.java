package Logica_Negocio;

public class Promocion {

    private String id;
    private Long stockActual;
    private Long diasSinVender;
    private Long totalVendido;
    private int cluster;

    public Promocion(){ }

    public Promocion(String id, Long stockActual, Long diasSinVender, Long totalVendido, int cluster) {
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

    public Long getStockActual() {
        return stockActual;
    }

    public void setStockActual(Long stockActual) {
        this.stockActual = stockActual;
    }

    public Long getDiasSinVender() {
        return diasSinVender;
    }

    public void setDiasSinVender(Long diasSinVender) {
        this.diasSinVender = diasSinVender;
    }

    public Long getTotalVendido() {
        return totalVendido;
    }

    public void setTotalVendido(Long totalVendido) {
        this.totalVendido = totalVendido;
    }

    public int getCluster() {
        return cluster;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }
}
