package CapaLogicaNegocio.Controlador;

import CapaDatos.Logica_Conexion.CategoriaDAO;
import CapaDatos.Logica_Conexion.Conexion;
import CapaDatos.Logica_Conexion.DetalleVentaDAO;
import CapaDatos.Logica_Conexion.DetalleVentaOnlineCRUD;
import CapaDatos.Logica_Conexion.ProductoDAO;
import CapaDatos.Logica_Conexion.ProductoOnlineCRUD;
import CapaDatos.Logica_Conexion.VentaDAO;
import CapaDatos.Logica_Conexion.VentaOnlineCRUD;
import CapaLogicaNegocio.DTOS.VentasDTO;
import CapaLogicaNegocio.Helpers.HelperGestorBD;
import CapaLogicaNegocio.Helpers.HelperMonitorRed;
import CapaLogicaNegocio.Helpers.HelperValidacion;
import CapaLogicaNegocio.Logica_Negocio.Carrito;
import CapaLogicaNegocio.Logica_Negocio.Categoria;
import CapaLogicaNegocio.Logica_Negocio.DetalleVenta;
import CapaLogicaNegocio.Logica_Negocio.ItemCarrito;
import CapaLogicaNegocio.Logica_Negocio.Producto;
import CapaLogicaNegocio.Logica_Negocio.Venta;

import java.util.UUID;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase Controladora encargada de ser la intermediaria y la comunicadora entre
 * la vista y el modelo
 * para la gestión de Ventas. CADA COSA del negocio debe pasar por el
 * controlador, la vista solo conoce el controlador. nada mas.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class VentaControlador {

    private VentaDAO ventaDAO;
    private VentaOnlineCRUD ventaOnlineCRUD;
    private DetalleVentaDAO detalleVentaDAO;
    private DetalleVentaOnlineCRUD detalleVentaOnlineCRUD;

    public VentaControlador() {
        this.ventaDAO = new VentaDAO();
        this.ventaOnlineCRUD = new VentaOnlineCRUD();
        this.detalleVentaDAO = new DetalleVentaDAO();
        this.detalleVentaOnlineCRUD = new DetalleVentaOnlineCRUD();
    }

    /**
     * Este metodo se encarga de agregar una venta a la base de datos local y nube
     * 
     * @param ventaDTO es un objeto generico (DTO) para la transferencia de datos de
     *                 la venta desde la vista
     * @return un record con su campo .exito() de tipo booleano y mensaje de tipo
     *         String
     *         para observar el mensaje personalizado
     *
     * @example Ejemplo de uso
     * 
     *          <pre>{@code
     * // Instancias el controlador
     * VentaControlador vControlador = new VentaControlador();
     *
     * // Construyes el objeto DTO con los datos de la vista
     * VentaDTO dto = new VentaDTO(txtId.getText(), txtFecha.getText(), txtTotal.getText(), metodoPagoEnum, txtIdCliente.getText());
     *
     * // Le pasas el objeto al controlador y validas
     * RespuestaControlador<Venta> respuesta = vControlador.agregarVenta(dto);
     * if(!respuesta.exito()){
     * JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Error", JOptionPane.ERROR_MESSAGE);
     * } else {
     * JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
     * }
     * }
     * </pre>
     */
    public RespuestaControlador<Boolean> agregarVenta(VentasDTO.VentaDTO ventaDTO) {

        if (ventaDTO == null)
            return new RespuestaControlador<>(false, "el objeto de la venta es nulo", null);

        RespuestaControlador<Boolean> respuestaControlador = validarCampos(ventaDTO);

        if (!respuestaControlador.exito())
            return respuestaControlador;

        Date fechaVenta;
        Double totalVenta;

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            fechaVenta = simpleDateFormat.parse(ventaDTO.fechaVenta());
            totalVenta = Double.valueOf(ventaDTO.totalVenta());
        } catch (Exception e) {
            return new RespuestaControlador<>(false, "la fecha o total de venta no tienen un formato valido", null);
        }

        Venta venta = new Venta(ventaDTO.id(), fechaVenta, totalVenta,
                ventaDTO.metodoPago(), ventaDTO.idCliente());

        boolean exito = HelperGestorBD.guardarRegistro(venta, "Venta", ventaDTO.id(),
                () -> ventaDAO.agregar(venta),
                () -> ventaOnlineCRUD.registrarNube(venta));

        return exito
                ? new RespuestaControlador<>(true, "Se ingresa la venta correctamente", null)
                : new RespuestaControlador<>(false, "No se puedo agregar la venta al sistema", null);
    }

    /**
     * Este metodo se encarga de buscar una venta especifica en la base de datos
     * (local o nube) mediante su ID.
     * 
     * @param id El identificador unico (UUID) de la venta a buscar (capturado desde
     *           la vista).
     * @return un record RespuestaControlador con su campo .exito() en true si la
     *         encontro, un mensaje descriptivo
     *         y en su campo .datos() la instancia de la Venta encontrada (o null si
     *         falla/no existe).
     *         * @example Ejemplo de uso
     * 
     *         <pre>{@code
     * // Obtienes el ID de la vista (ej. de un JTextField de busqueda) y llamas al controlador
     * RespuestaControlador<Venta> respuesta = vControlador.buscarVentaId(txtBuscarId.getText());
     *
     * if(respuesta.exito()){
     * // Extraes los datos del modelo encapsulado y llenas los campos de la pantalla
     * Venta ventaEncontrada = respuesta.datos();
     * txtTotal.setText(String.valueOf(ventaEncontrada.getTotalVenta()));
     * // ... demas campos
     * } else {
     * // Muestras el error devuelto por el controlador
     * JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Búsqueda", JOptionPane.WARNING_MESSAGE);
     * }
     * }
     * </pre>
     */
    public RespuestaControlador<Venta> buscarVentaId(String id) {

        if (id == null)
            return new RespuestaControlador<>(false, "el objeto no debe ser nulo", null);

        if (id.isEmpty())
            return new RespuestaControlador<>(false, "El id no debe estar vacio", null);

        Venta venta = HelperGestorBD.cargarRegistro(
                () -> ventaDAO.obtener(id),
                () -> ventaOnlineCRUD.obtenerNube(Venta.class, id));

        return venta != null
                ? new RespuestaControlador<>(true, "Se encontro el registro en la venta", venta)
                : new RespuestaControlador<>(false, "No se pudo encontrar la venta", null);

    }

    /**
     * Este metodo se encarga de validar y actualizar la informacion de una venta
     * existente,
     * impactando tanto la base de datos local como la nube.
     * 
     * @param ventaDTO Objeto de transferencia de datos con la nueva informacion
     *                 capturada desde la vista.
     * @return un record RespuestaControlador indicando en su campo .exito() si la
     *         operacion fue exitosa, junto a un mensaje.
     *
     * @example Ejemplo de uso
     * 
     *          <pre>{@code
     * // Construyes el DTO con los datos modificados en la pantalla
     * VentaDTO dtoActualizado = new VentaDTO(txtId.getText(), txtFecha.getText(), txtTotal.getText()...);
     *
     * // Llamas al controlador para procesar la actualizacion
     * RespuestaControlador<Venta> respuesta = vControlador.actualizarVenta(dtoActualizado);
     *
     * if(respuesta.exito()){
     * JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);
     * } else {
     * JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Error", JOptionPane.ERROR_MESSAGE);
     * }
     * }
     * </pre>
     */
    public RespuestaControlador<Boolean> actualizarVenta(VentasDTO.VentaDTO ventaDTO) {

        if (ventaDTO == null)
            return new RespuestaControlador<>(false, "el objeto de la venta es nulo", null);

        RespuestaControlador<Boolean> respuestaControlador = validarCampos(ventaDTO);

        if (!respuestaControlador.exito())
            return respuestaControlador;

        Date fechaVenta;
        Double totalVenta;

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            fechaVenta = simpleDateFormat.parse(ventaDTO.fechaVenta());
            totalVenta = Double.valueOf(ventaDTO.totalVenta());
        } catch (Exception e) {
            return new RespuestaControlador<>(false, "la fecha o total de venta no tienen un formato valido", null);
        }

        Venta venta = new Venta(ventaDTO.id(), fechaVenta, totalVenta,
                ventaDTO.metodoPago(), ventaDTO.idCliente());

        boolean exito = HelperGestorBD.actualizarRegistro(venta, "Venta", ventaDTO.id(),
                () -> ventaDAO.actualizar(venta),
                () -> ventaOnlineCRUD.actualizarNube(venta));

        return exito
                ? new RespuestaControlador<>(true, "Se actualiza correctamente la venta", null)
                : new RespuestaControlador<>(false, "No se puede ingresar la venta al sistema", null);
    }

    /**
     * Este metodo se encarga de eliminar permanentemente un registro de venta,
     * sincronizando el borrado en la base de datos local y en la nube.
     * 
     * @param id El identificador unico (UUID) de la venta que se desea eliminar.
     * @return un record RespuestaControlador confirmando mediante .exito() el
     *         resultado de la operacion.
     *         * @example Ejemplo de uso
     * 
     *         <pre>{@code
     * // Es buena practica pedir confirmacion en la vista antes de enviar al controlador
     * int confirmacion = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar la venta?");
     * if (confirmacion == JOptionPane.YES_OPTION) {
     * RespuestaControlador<Venta> respuesta = vControlador.eliminarVenta(txtId.getText());
     * if(respuesta.exito()){
     * JOptionPane.showMessageDialog(this, respuesta.mensaje());
     * // Aqui la vista podria limpiar los campos o refrescar la tabla de ventas
     * } else {
     * JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Error al Eliminar", JOptionPane.ERROR_MESSAGE);
     * }
     * }
     * }
     * </pre>
     */
    public RespuestaControlador<Venta> eliminarVenta(String id) {

        if (id == null)
            return new RespuestaControlador<>(false, "El id es un objeto nulo", null);

        if (id.trim().isEmpty())
            return new RespuestaControlador<>(false, "El campo id esta vacio", null);

        boolean exito = HelperGestorBD.eliminarRegistro(id, "Venta",
                () -> ventaDAO.eliminar(id),
                () -> ventaOnlineCRUD.eliminarNube(id));

        return exito
                ? new RespuestaControlador<>(true, "Venta eliminada con exito", null)
                : new RespuestaControlador<>(false, "Error al intentar eliminar la venta en el sistema.", null);
    }

    /**
     * Este metodo solicita y extrae la lista completa de todas las ventas
     * registradas en el sistema.
     * 
     * @return un record RespuestaControlador que, de ser exitoso, contiene un
     *         ArrayList de objetos Venta en su propiedad .datos().
     *         * @example Ejemplo de uso
     * 
     *         <pre>{@code
     * RespuestaControlador<ArrayList<Venta>> respuesta = vControlador.buscarTodos();
     * if(respuesta.exito()){
     * ArrayList<Venta> listaVentas = respuesta.datos();
     * // Llenar tabla en la vista...
     * }
     * }
     * </pre>
     */
    public RespuestaControlador<ArrayList<Venta>> buscarTodos() {

        ArrayList<Venta> listaVentas = HelperGestorBD.cargarRegistros(
                () -> ventaDAO.obteners(),
                () -> ventaOnlineCRUD.obtenersNube(Venta.class));

        return !listaVentas.isEmpty()
                ? new RespuestaControlador<>(true, "Imprimiendo todas las ventas", listaVentas)
                : new RespuestaControlador<>(false, "Error en la consulta de todas de las ventas", null); // se podria
                                                                                                          // retornar la
                                                                                                          // lista vacia
                                                                                                          // en vez de
                                                                                                          // null
    }

    /**
     * Genera un reporte de ventas agrupadas por categoria.
     * 
     * @return un record RespuestaControlador que contiene una lista de
     *         VentaPorCategoriaDTO.
     *         * @example Ejemplo de uso
     * 
     *         <pre>{@code
     * RespuestaControlador<ArrayList<VentaPorCategoriaDTO>> respuesta = vControlador.ventasPorCategoria();
     * if(respuesta.exito()){
     * ArrayList<VentaPorCategoriaDTO> reporte = respuesta.datos();
     * // Usar los datos para dibujar un grafico o llenar una tabla de estadisticas en la vista
     * }
     * }
     * </pre>
     */
    // NOTA: Este metodo NO usa sincronizacion con la nube, ya que se esta en la
    // primera version de la APP.

    public RespuestaControlador<ArrayList<VentasDTO.VentaPorCategoriaDTO>> ventasPorCategoria() {

        ArrayList<VentasDTO.VentaPorCategoriaDTO> ventaPorCategoriaDTO = ventaDAO.ventasPorCategoria();

        if (!ventaPorCategoriaDTO.isEmpty()) {
            return new RespuestaControlador<>(true, "datos cargados correctamente", ventaPorCategoriaDTO);
        }

        // Fallback: agregar desde la nube cuando la BD local no tiene DetalleVentael
        if (HelperMonitorRed.estaUsandoNube()) {
            ArrayList<VentasDTO.VentaPorCategoriaDTO> cloudData = ventasPorCategoriaDesdeNube();
            if (!cloudData.isEmpty()) {
                return new RespuestaControlador<>(true, "datos cargados desde la nube", cloudData);
            }
        }

        return new RespuestaControlador<>(false, "no hay datos", ventaPorCategoriaDTO);
    }

    private ArrayList<VentasDTO.VentaPorCategoriaDTO> ventasPorCategoriaDesdeNube() {
        ArrayList<VentasDTO.VentaPorCategoriaDTO> resultado = new ArrayList<>();
        try {
            // Cargar colecciones necesarias desde la nube y la BD local
            ArrayList<DetalleVenta> detalles = new DetalleVentaOnlineCRUD().obtenersNube(DetalleVenta.class);
            if (detalles == null || detalles.isEmpty())
                return resultado;

            ArrayList<Producto> productos = new ProductoOnlineCRUD().obtenersNube(Producto.class);
            if (productos == null || productos.isEmpty())
                return resultado;

            ArrayList<Venta> ventas = ventaOnlineCRUD.obtenersNube(Venta.class);
            if (ventas == null || ventas.isEmpty())
                return resultado;

            ArrayList<Categoria> categorias = new CategoriaDAO().obteners();

            // Construir mapas para lookup en O(1)
            Map<String, Producto> mapProductos = new HashMap<>();
            for (Producto p : productos)
                mapProductos.put(p.getId(), p);

            Map<String, Venta> mapVentas = new HashMap<>();
            for (Venta v : ventas)
                mapVentas.put(v.getId(), v);

            Map<String, String> mapCategoriaNombre = new HashMap<>();
            for (Categoria c : categorias)
                mapCategoriaNombre.put(c.getId(), c.getNombre());

            // Acumuladores por idCategoria
            Map<String, Long> acumCantidad = new HashMap<>();
            Map<String, Double> acumTotal = new HashMap<>();
            Map<String, Date> acumPrimera = new HashMap<>();
            Map<String, Date> acumUltima = new HashMap<>();

            for (DetalleVenta det : detalles) {
                Producto prod = mapProductos.get(det.getIdProducto());
                if (prod == null || prod.getIdCategoria() == null)
                    continue;
                String idCat = prod.getIdCategoria();

                long cantidad = det.getCantidad() != null ? det.getCantidad() : 0L;
                double subtotal = det.getSubtotal() != null ? det.getSubtotal() : 0.0;

                acumCantidad.merge(idCat, cantidad, Long::sum);
                acumTotal.merge(idCat, subtotal, Double::sum);

                Venta v = mapVentas.get(det.getIdVenta());
                Date fecha = v != null ? v.getFechaVenta() : null;
                if (fecha != null) {
                    acumPrimera.merge(idCat, fecha, (a, b) -> a.before(b) ? a : b);
                    acumUltima.merge(idCat, fecha, (a, b) -> a.after(b) ? a : b);
                }
            }

            for (Map.Entry<String, Long> entry : acumCantidad.entrySet()) {
                String idCat = entry.getKey();
                String nombreCat = mapCategoriaNombre.getOrDefault(idCat, idCat);
                resultado.add(new VentasDTO.VentaPorCategoriaDTO(
                        nombreCat,
                        entry.getValue(),
                        acumTotal.getOrDefault(idCat, 0.0),
                        acumPrimera.get(idCat),
                        acumUltima.get(idCat)));
            }
        } catch (Exception e) {
            System.out.println("Error cargando ventas por categoria desde nube: " + e.getMessage());
        }
        return resultado;
    }

    public RespuestaControlador<Double> totalVentas() {

        Double totalVentas = ventaDAO.totalVentas();

        return totalVentas != null
                ? new RespuestaControlador<>(true, "se ejecuto correctamente la consulta", totalVentas)
                : new RespuestaControlador<>(false, "fallo la consulta", null);
    }

    public RespuestaControlador<Long> cantidadVentas() {

        Long cantidadVentas = ventaDAO.cantidadVentas();

        return cantidadVentas != null
                ? new RespuestaControlador<>(true, "se ejecuto correctamente la consulta", cantidadVentas)
                : new RespuestaControlador<>(false, "Fallo la consulta", null);
    }

    /**
     * Registra una venta del lado del administrador escribiendo en LOCAL y en NUBE.
     * A diferencia de CompraControlador (solo nube/cliente), este metodo usa
     * HelperGestorBD para garantizar la escritura dual igual que el resto de
     * entidades del admin. No descuenta stock porque esta pensado para datos de
     * prueba / seed donde el stock ya viene definido al crear los productos.
     *
     * @param carrito    carrito con los productos y cantidades a registrar
     * @param idCliente  UUID del cliente que realiza la compra
     * @param metodoPago metodo de pago elegido
     * @return RespuestaControlador con la Venta en .dato() si todo salio bien
     */
    public RespuestaControlador<Venta> registrarVentaAdmin(Carrito carrito, String idCliente,
            Venta.MetodoPago metodoPago) {
        return registrarVentaAdmin(carrito, idCliente, metodoPago, new Date());
    }

    public RespuestaControlador<Venta> registrarVentaAdmin(Carrito carrito, String idCliente,
            Venta.MetodoPago metodoPago, Date fecha) {

        if (carrito == null)
            return new RespuestaControlador<>(false, "El carrito es nulo", null);
        if (carrito.estaVacio())
            return new RespuestaControlador<>(false, "El carrito esta vacio", null);
        if (metodoPago == null)
            return new RespuestaControlador<>(false, "Debe seleccionar un metodo de pago", null);
        if (idCliente == null || idCliente.isEmpty())
            return new RespuestaControlador<>(false, "El id del cliente es obligatorio", null);
        if (HelperValidacion.validarUUID(idCliente) > 0)
            return new RespuestaControlador<>(false, "El id del cliente no tiene formato UUID valido", null);
        if (fecha == null)
            fecha = new Date();

        String idVenta = UUID.randomUUID().toString();
        Venta venta = new Venta(idVenta, fecha, carrito.calcularTotal(), metodoPago, idCliente);

        boolean exitoVenta = HelperGestorBD.guardarRegistro(venta, "Venta", idVenta,
                () -> ventaDAO.agregar(venta),
                () -> ventaOnlineCRUD.registrarNube(venta));

        if (!exitoVenta)
            return new RespuestaControlador<>(false, "No se pudo registrar la venta", null);

        for (ItemCarrito item : carrito.getItems()) {
            Producto producto = item.getProducto();
            if (producto == null)
                continue;
            String idDetalle = UUID.randomUUID().toString();
            DetalleVenta detalle = new DetalleVenta(idDetalle, item.getCantidad(), item.getSubtotal(),
                    producto.getPrecioActual(), producto.getId(), idVenta);
            boolean exitoDetalle = HelperGestorBD.guardarRegistro(detalle, "DetalleVenta", idDetalle,
                    () -> detalleVentaDAO.agregar(detalle),
                    () -> detalleVentaOnlineCRUD.registrarNube(detalle));
            if (!exitoDetalle)
                System.err.println("Fallo el detalle de '" + producto.getNombre() + "' en venta " + idVenta);
        }

        return new RespuestaControlador<>(true, "Venta registrada en local y nube", venta);
    }

    /**
     * Descarga las compras hechas por clientes (almacenadas solo en la nube) hacia
     * la base de datos local. Solo inserta registros que aun no existen en local;
     * los que ya estan se omiten sin error. Util para que el admin vea en local las
     * ventas que los clientes generaron desde la app de cliente.
     *
     * @return RespuestaControlador con el numero de Ventas nuevas insertadas en
     *         .dato()
     */
    public RespuestaControlador<Integer> sincronizarComprasClienteALocal() {

        if (!HelperMonitorRed.estaUsandoNube())
            return new RespuestaControlador<>(false, "Se requiere conexion a la nube para sincronizar", null);

        if (Conexion.getConexionLocal() == null)
            return new RespuestaControlador<>(false, "Se requiere conexion local para sincronizar", null);

        int ventasNuevas = 0;
        java.util.Set<String> idsVentasNuevas = new java.util.HashSet<>();

        ArrayList<Venta> ventasNube = ventaOnlineCRUD.obtenersNube(Venta.class);
        if (ventasNube != null) {
            for (Venta venta : ventasNube) {
                if (venta.getId() == null)
                    continue;
                if (ventaDAO.obtener(venta.getId()) == null) {
                    if (ventaDAO.agregar(venta)) {
                        ventasNuevas++;
                        idsVentasNuevas.add(venta.getId());
                    }
                }
            }
        }

        ArrayList<DetalleVenta> detallesNube = detalleVentaOnlineCRUD.obtenersNube(DetalleVenta.class);
        if (detallesNube != null) {
            for (DetalleVenta detalle : detallesNube) {
                if (detalle.getId() == null)
                    continue;
                boolean esNuevo = detalleVentaDAO.obtener(detalle.getId()) == null;
                if (esNuevo) {
                    detalleVentaDAO.agregar(detalle);
                    // Descontar stock local solo para ventas que se insertaron en esta sincronizacion
                    if (idsVentasNuevas.contains(detalle.getIdVenta()))
                        descontarStockLocal(detalle.getIdProducto(), detalle.getCantidad());
                }
            }
        }

        return new RespuestaControlador<>(true,
                "Sincronizacion completada. Ventas nuevas bajadas de la nube: " + ventasNuevas, ventasNuevas);
    }

    private void descontarStockLocal(String idProducto, Long cantidad) {
        if (idProducto == null || cantidad == null || cantidad <= 0) return;
        ProductoDAO productoDAO = new ProductoDAO();
        Producto producto = productoDAO.obtener(idProducto);
        if (producto == null || producto.getStock() == null) return;
        producto.setStock(Math.max(0, producto.getStock() - cantidad));
        productoDAO.actualizar(producto);
    }

    /**
     * Metodo privado de soporte (Helper) interno del controlador.
     * Centraliza las validaciones de negocio, nulos y vacios requeridas antes de
     * procesar el VentaDTO.
     * Idealmente, la vista no tiene acceso a este metodo.
     * 
     * @param ventaDTO El objeto con los datos crudos procedentes de la capa de
     *                 presentacion.
     * @return RespuestaControlador indicando (false) qué regla falló exactamente, o
     *         (true) si todos los campos son aptos.
     */
    public RespuestaControlador<Boolean> validarCampos(VentasDTO.VentaDTO ventaDTO) {

        if (ventaDTO.id() == null || ventaDTO.fechaVenta() == null || ventaDTO.totalVenta() == null
                || ventaDTO.metodoPago() == null || ventaDTO.idCliente() == null) {
            return new RespuestaControlador<>(false, "Alguno de los campos son nulos", null);
        }

        if (HelperValidacion.validarUUID(ventaDTO.id()) > 0)
            return new RespuestaControlador<>(false, "el id debe tener un formato valido (UUID)", null);

        if (ventaDTO.fechaVenta().isEmpty())
            return new RespuestaControlador<>(false, "La fecha esta vacia", null);

        if (HelperValidacion.validarNumero(ventaDTO.totalVenta()) > 0)
            return new RespuestaControlador<>(false, "Debe tener solo numeros el total venta", null);

        if (ventaDTO.metodoPago().name().isEmpty())
            return new RespuestaControlador<>(false, "el metodo de pago no debe estar vacio", null);

        if (HelperValidacion.validarUUID(ventaDTO.idCliente()) > 0)
            return new RespuestaControlador<>(false, "El id debe tener un formato valido (UUID)", null);

        return new RespuestaControlador<>(true, "todos los campos estan correctos", null);
    }
}
