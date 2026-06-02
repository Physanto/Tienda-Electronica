package CapaLogicaNegocio.Logica_Negocio;

import java.util.Date;

/**
 * Representa una venta dentro del sistema.
 * Esta entidad almacena y envia la informacion necesaria donde sea solicitada
 *
 * @author Manuel Figueroa (Physanto)
 */
public class Venta {

    // Metodos de pago de la tienda online. Se quito EFECTIVO (no aplica para una tienda en linea) y
    // se agregaron PSE y EFECTY, los medios usados en Colombia.
    public enum MetodoPago { TARJETA, PSE, EFECTY };

    /**
     * Convierte un texto a {@link MetodoPago} de forma tolerante. Si el valor no corresponde a un
     * metodo valido (por ejemplo "EFECTIVO" de datos antiguos), devuelve {@link MetodoPago#TARJETA}
     * en lugar de lanzar excepcion, para no romper la carga de ventas existentes.
     * @param valor texto del metodo de pago (puede venir de la BD local o de la nube)
     * @return el MetodoPago correspondiente, o TARJETA si no se reconoce
     */
    public static MetodoPago metodoPagoDesde(String valor) {
        if (valor == null) return MetodoPago.TARJETA;
        try {
            return MetodoPago.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return MetodoPago.TARJETA;
        }
    }

    private String id;
    private Date fechaVenta;
    private Double totalVenta;
    private MetodoPago metodoPago;
    private String idCliente;

    public Venta(){ }

    public Venta(String id, Date fechaVenta, Double totalVenta, MetodoPago metodoPago, String idCliente) {
        this.id = id;
        this.fechaVenta = fechaVenta;
        this.totalVenta = totalVenta;
        this.metodoPago = metodoPago;
        this.idCliente = idCliente;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
