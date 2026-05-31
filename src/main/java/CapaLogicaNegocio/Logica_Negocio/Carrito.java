package CapaLogicaNegocio.Logica_Negocio;

import java.util.ArrayList;

/**
 * Carrito de compras del cliente. Vive EN MEMORIA durante la sesion (no se persiste en ninguna
 * base de datos); solo cuando el cliente confirma la compra se traduce en una Venta y sus
 * DetalleVenta en la nube
 * Centraliza la logica de agregar, quitar y totalizar los items.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class Carrito {

    private ArrayList<ItemCarrito> items;

    public Carrito(){
        this.items = new ArrayList<>();
    }

    public ArrayList<ItemCarrito> getItems() {
        return items;
    }

    /**
     * Agrega un producto al carrito. Si el producto ya estaba agregado, simplemente le suma la cantidad.
     * @param producto el producto que se quiere agregar
     * @param cantidad la cantidad de unidades a agregar (debe ser mayor a 0)
     */
    public void agregar(Producto producto, Long cantidad){
        if(producto == null || cantidad == null || cantidad <= 0) return;

        for(ItemCarrito item : items){
            if(item.getProducto().getId().equals(producto.getId())){
                item.setCantidad(item.getCantidad() + cantidad);
                return;
            }
        }
        items.add(new ItemCarrito(producto, cantidad));
    }

    /**
     * Quita por completo del carrito el producto identificado con el id pasado por argumento.
     * @param idProducto el id del producto que se quiere quitar
     */
    public void quitar(String idProducto){
        items.removeIf(item -> item.getProducto().getId().equals(idProducto));
    }

    /**
     * Vacia el carrito por completo.
     */
    public void vaciar(){
        items.clear();
    }

    /**
     * @return true si el carrito no tiene items, de lo contrario false
     */
    public boolean estaVacio(){
        return items.isEmpty();
    }

    /**
     * Suma los subtotales de todas las lineas del carrito.
     * @return el total a pagar por la compra
     */
    public Double calcularTotal(){
        double total = 0.0;
        for(ItemCarrito item : items){
            total += item.getSubtotal();
        }
        return total;
    }
}
