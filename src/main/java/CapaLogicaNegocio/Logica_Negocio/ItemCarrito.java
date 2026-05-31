package CapaLogicaNegocio.Logica_Negocio;

/**
 * Representa una linea del carrito de compras del cliente: un producto y la cantidad
 * que el cliente desea llevar. Calcula su propio subtotal en base al precio del producto.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class ItemCarrito {

    private Producto producto;
    private Long cantidad;

    public ItemCarrito(){ }

    public ItemCarrito(Producto producto, Long cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Calcula el subtotal de la linea (precio actual del producto * cantidad).
     * @return el subtotal de la linea, o 0.0 si falta informacion para calcularlo
     */
    public Double getSubtotal() {
        if(producto == null || producto.getPrecioActual() == null || cantidad == null) return 0.0;
        return producto.getPrecioActual() * cantidad;
    }
}
