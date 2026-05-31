package CapaLogicaNegocio.Controlador;

import CapaDatos.Logica_Conexion.DetalleVentaOnlineCRUD;
import CapaDatos.Logica_Conexion.ProductoOnlineCRUD;
import CapaDatos.Logica_Conexion.VentaOnlineCRUD;
import CapaLogicaNegocio.Helpers.HelperValidacion;
import CapaLogicaNegocio.Logica_Negocio.Carrito;
import CapaLogicaNegocio.Logica_Negocio.DetalleVenta;
import CapaLogicaNegocio.Logica_Negocio.ItemCarrito;
import CapaLogicaNegocio.Logica_Negocio.Producto;
import CapaLogicaNegocio.Logica_Negocio.Venta;

import java.util.Date;
import java.util.UUID;

/**
 * Clase Controladora del modulo Cliente encargada de procesar la compra (checkout).
 * Trabaja UNICAMENTE contra la base de datos de la nube (no usa HelperGestorBD ni la base local),
 * porque el cliente solo opera en linea. A partir del carrito genera una Venta y un DetalleVenta
 * por cada item, y descuenta el stock de cada producto en la nube. CADA COSA del negocio debe
 * pasar por el controlador, la vista solo conoce el controlador. nada mas.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class CompraControlador {

    private VentaOnlineCRUD ventaOnlineCRUD;
    private DetalleVentaOnlineCRUD detalleVentaOnlineCRUD;
    private ProductoOnlineCRUD productoOnlineCRUD;

    public CompraControlador(){
        this.ventaOnlineCRUD = new VentaOnlineCRUD();
        this.detalleVentaOnlineCRUD = new DetalleVentaOnlineCRUD();
        this.productoOnlineCRUD = new ProductoOnlineCRUD();
    }

    /**
     * Registra la compra del cliente en la nube. Valida el carrito y el stock disponible, crea la
     * Venta y un DetalleVenta por cada item, y descuenta el stock de cada producto comprado.
     *
     * @param carrito el carrito con los productos y cantidades que el cliente desea comprar
     * @param idCliente id (UUID) del cliente que realiza la compra (ej. SesionCliente.getClienteActual().getId())
     * @param metodoPago metodo de pago elegido (EFECTIVO o TARJETA)
     * @return un record RespuestaControlador con la Venta registrada en su propiedad .dato() si todo salio bien
     *
     * @example Ejemplo de uso desde la vista
     * <pre>{@code
     * CompraControlador compraControlador = new CompraControlador();
     * RespuestaControlador<Venta> respuesta = compraControlador.registrarCompra(
     *         carrito, SesionCliente.getClienteActual().getId(), Venta.MetodoPago.EFECTIVO);
     *
     * if(respuesta.exito()){
     *     JOptionPane.showMessageDialog(this, respuesta.mensaje());
     *     carrito.vaciar();
     * } else {
     *     JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Error", JOptionPane.ERROR_MESSAGE);
     * }
     * }
     * </pre>
     */
    public RespuestaControlador<Venta> registrarCompra(Carrito carrito, String idCliente, Venta.MetodoPago metodoPago){

        if(carrito == null) return new RespuestaControlador<>(false, "El carrito es nulo", null);

        if(carrito.estaVacio()) return new RespuestaControlador<>(false, "El carrito esta vacio", null);

        if(metodoPago == null) return new RespuestaControlador<>(false, "Debe seleccionar un metodo de pago", null);

        if(idCliente == null || idCliente.isEmpty()) return new RespuestaControlador<>(false, "El id del cliente es obligatorio", null);

        if(HelperValidacion.validarUUID(idCliente) > 0) return new RespuestaControlador<>(false, "El id del cliente no tiene un formato valido (UUID)", null);

        // 1. Validar que cada producto exista y tenga stock suficiente. Se relee desde la nube para
        //    no confiar en los datos (posiblemente viejos) que traia el catalogo de la vista.
        for(ItemCarrito item : carrito.getItems()){
            Producto producto = item.getProducto();

            if(producto == null || item.getCantidad() == null || item.getCantidad() <= 0)
                return new RespuestaControlador<>(false, "Hay un item invalido en el carrito", null);

            Producto productoNube = productoOnlineCRUD.obtenerNube(Producto.class, producto.getId());

            if(productoNube == null)
                return new RespuestaControlador<>(false, "El producto '" + producto.getNombre() + "' ya no existe en el catalogo", null);

            if(productoNube.getStock() == null || productoNube.getStock() < item.getCantidad())
                return new RespuestaControlador<>(false, "Stock insuficiente para el producto '" + productoNube.getNombre() + "'", null);
        }

        // 2. Crear y registrar la Venta en la nube.
        String idVenta = UUID.randomUUID().toString();
        Venta venta = new Venta(idVenta, new Date(), carrito.calcularTotal(), metodoPago, idCliente);

        if(!ventaOnlineCRUD.registrarNube(venta))
            return new RespuestaControlador<>(false, "No se pudo registrar la venta en la nube", null);

        // 3. Crear un DetalleVenta por cada item y descontar el stock del producto correspondiente.
        for(ItemCarrito item : carrito.getItems()){
            Producto producto = item.getProducto();
            Long cantidad = item.getCantidad();

            DetalleVenta detalle = new DetalleVenta(
                    UUID.randomUUID().toString(),
                    cantidad,
                    item.getSubtotal(),
                    producto.getPrecioActual(),
                    producto.getId(),
                    idVenta
            );

            if(!detalleVentaOnlineCRUD.registrarNube(detalle))
                return new RespuestaControlador<>(false, "La venta se registro pero fallo el detalle del producto '" + producto.getNombre() + "'", venta);

            descontarStock(producto, cantidad);
        }

        return new RespuestaControlador<>(true, "Compra realizada con exito", venta);
    }

    /**
     * Descuenta del stock del producto en la nube la cantidad comprada. Relee el producto antes de
     * descontar para operar sobre el valor mas reciente.
     * @param producto el producto al que se le descuenta stock
     * @param cantidad la cantidad comprada
     */
    private void descontarStock(Producto producto, Long cantidad){
        Producto productoNube = productoOnlineCRUD.obtenerNube(Producto.class, producto.getId());

        if(productoNube == null || productoNube.getStock() == null) return;

        productoNube.setStock(productoNube.getStock() - cantidad);
        productoOnlineCRUD.actualizarNube(productoNube);
    }
}
