package CapaLogicaNegocio.Controlador;

import CapaDatos.Logica_Conexion.ProductoDAO;
import CapaDatos.Logica_Conexion.ProductoOnlineCRUD;
import CapaLogicaNegocio.DTOS.ProductoDTO;
import CapaLogicaNegocio.Helpers.HelperGestorBD;
import CapaLogicaNegocio.Helpers.HelperTiempo;
import CapaLogicaNegocio.Helpers.HelperValidacion;
import CapaLogicaNegocio.Logica_Negocio.Producto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Clase Controladora encargada de ser la intermediaria y la comunicadora entre
 * la vista y el modelo
 * CADA COSA del negocio debe pasar por el controlador, la vista solo conoce el
 * controlador. nada mas.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class ProductoControlador {

    private ProductoDAO productoDAO;
    private ProductoOnlineCRUD productoOnlineCRUD;

    public ProductoControlador() {
        this.productoDAO = new ProductoDAO();
        this.productoOnlineCRUD = new ProductoOnlineCRUD();
    }

    /**
     * Este metodo se encarga de agregar un producto a la base de datos local y nube
     * 
     * @param productoDTO es un objeto generico para la transferencia de datos al
     *                    modelo
     * @return un record con su campo .exito() de tipo booleano y mensaje de tipo
     *         String
     *         para observar el mensaje personalizado
     *
     * @example Ejemplo de uso
     * 
     *          <pre>{@code
     * //Instancias el controlador y le pasas las instancias necesarias
     * ProductoControlador prControlador = new ProductoControlador();
     *
     * // validas todos los campos del formulario antes de construir el objeto, es decir
     * if(txtId.getText().isEmpty())...
     *
     * //construyes el objeto
     * ProductoDTO dto = new ProductoDTO(txtId.getText(), txtCodigo.getText(), txtNombre.getText()...n-campos);
     *
     * //le pasas el objeto al controlador y validas, esto es un ejemplo
     * if(!prControlador.agregarProducto(dto).exito()){
     * JOptionPane.showMessageDialog(this, prControlador.mensaje(), "Error", JOptionPane.ERROR_MESSAGE); // puedes tambien anhadir mas contexto
     * }
     * }
     * </pre>
     */
    public RespuestaControlador<Producto> agregarProducto(ProductoDTO productoDTO) {

        if (productoDTO == null)
            return new RespuestaControlador<>(false, "objeto nulo", null);

        RespuestaControlador<Producto> respuestaControlador = validarCampos(productoDTO);

        if (!respuestaControlador.exito())
            return respuestaControlador;

        Long stock = 0L;
        Double precioActual = 0D;
        Date fechaVencimiento = null;

        // por simplicidad y tiempo se va manejar esto en un solo try-catch y un solo
        // mensaje informativo
        // sin embargo se puede hacer mas elegante y mejor estructurado
        try {
            stock = Long.valueOf(productoDTO.stock());
            precioActual = Double.valueOf(productoDTO.precioActual());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            fechaVencimiento = simpleDateFormat.parse(productoDTO.fechaVencimiento());
        } catch (Exception e) {
            return new RespuestaControlador<>(false, "Error de formato", null);
        }

        Producto producto = new Producto(productoDTO.id(), productoDTO.codigo(), productoDTO.nombre(),
                productoDTO.marca(), productoDTO.serie(), stock, precioActual,
                fechaVencimiento, productoDTO.idCategoria());

        Long i = System.currentTimeMillis();
        boolean exito = HelperGestorBD.guardarRegistro(producto, "Producto", producto.getId(),
                () -> productoDAO.agregar(producto),
                () -> productoOnlineCRUD.registrarNube(producto));
        Long f = System.currentTimeMillis();
        HelperTiempo.RetornarTiempo(f, i);
        return exito
                ? new RespuestaControlador<>(true, "Producto agregado correctamente", producto)
                : new RespuestaControlador<>(false, "Producto no fue agregado como se esperaba", null);

    }

    /**
     * Este metodo se encarga de buscar un producto especifico en la base de datos
     * (local o nube) mediante su ID.
     * 
     * @param id El identificador unico del producto a buscar (capturado desde la
     *           vista).
     * @return un record RespuestaControlador con su campo .exito() en true si lo
     *         encontro, un mensaje descriptivo
     *         y en su campo .datos() la instancia del Producto encontrado (o null
     *         si falla/no existe).
     * @example Ejemplo de uso
     * 
     *          <pre>{@code
     * // Obtienes el ID de la vista (ej. de un JTextField de busqueda) y llamas al controlador
     * RespuestaControlador<Producto> respuesta = prControlador.buscarProductoId(txtBuscarId.getText());
     *
     * if(respuesta.exito()){
     * // Extraes los datos del modelo encapsulado y llenas los campos de la pantalla
     * Producto productoEncontrado = respuesta.datos();
     * txtNombre.setText(productoEncontrado.getNombre());
     * txtMarca.setText(productoEncontrado.getMarca());
     * // ... demas campos
     * } else {
     * // Muestras el error devuelto por el controlador
     * JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Búsqueda", JOptionPane.WARNING_MESSAGE);
     * }
     * }
     * </pre>
     */
    public RespuestaControlador<Producto> buscarProductoId(String id) {

        if (id == null)
            return new RespuestaControlador<>(false, "El id es nulo", null);

        if (id.isEmpty())
            return new RespuestaControlador<>(false, "El campo id esta vacio", null);

        Producto producto = HelperGestorBD.cargarRegistro(
                () -> productoDAO.obtener(id),
                () -> productoOnlineCRUD.obtenerNube(Producto.class, id));

        return producto != null
                ? new RespuestaControlador<>(true, "Producto encontrado con exito", producto)
                : new RespuestaControlador<>(false, "Producto no encontrado con ese id", null);
    }

    /**
     * Este metodo se encarga de validar y actualizar la informacion de un producto
     * existente,
     * impactando tanto la base de datos local como la nube.
     * 
     * @param productoDTO Objeto de transferencia de datos con la nueva informacion
     *                    capturada desde la vista.
     * @return un record RespuestaControlador indicando en su campo .exito() si la
     *         operacion fue exitosa, junto a un mensaje.
     *
     * @example Ejemplo de uso
     * 
     *          <pre>{@code
     * // Construyes el DTO con los datos modificados en la pantalla
     * ProductoDTO dto = new ProductoDTO(txtId.getText(), txtCodigo.getText(), txtNombre.getText()...);
     *
     * // Llamas al controlador para procesar la actualizacion
     * RespuestaControlador<Producto> respuesta = prControlador.actualizarProducto(dto);
     *
     * if(respuesta.exito()){
     * JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);
     * } else {
     * JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Error", JOptionPane.ERROR_MESSAGE);
     * }
     * }
     * </pre>
     */

    // los metodos de guardar y actualizar se pueden refactorizar para que quede uno
    // solo ya que
    // hay mucho codigo redundante
    public RespuestaControlador<Producto> actualizarProducto(ProductoDTO productoDTO) {

        if (productoDTO == null)
            return new RespuestaControlador<>(false, "El objeto es nulo", null);

        RespuestaControlador<Producto> respuestaControlador = validarCampos(productoDTO);

        if (!respuestaControlador.exito())
            return respuestaControlador;

        Long stock = 0L;
        Double precioActual = 0D;
        Date fechaVencimiento = null;

        try {
            stock = Long.valueOf(productoDTO.stock());
            precioActual = Double.valueOf(productoDTO.precioActual());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            fechaVencimiento = simpleDateFormat.parse(productoDTO.fechaVencimiento());
        } catch (Exception e) {
            return new RespuestaControlador<>(false, "Error de formato en números o fechas", null);
        }

        Producto producto = new Producto(productoDTO.id(), productoDTO.codigo(), productoDTO.nombre(),
                productoDTO.marca(), productoDTO.serie(), stock, precioActual,
                fechaVencimiento, productoDTO.idCategoria());

        boolean exito = HelperGestorBD.actualizarRegistro(producto, "Producto", producto.getId(),
                () -> productoDAO.actualizar(producto),
                () -> productoOnlineCRUD.actualizarNube(producto));

        return (exito)
                ? new RespuestaControlador<>(true, "Producto actualizado con exito", null)
                : new RespuestaControlador<>(false, "Error al intentar actualizar el producto en el sistema", null);
    }

    /**
     * Este metodo se encarga de eliminar permanentemente un registro de producto
     * especifico,
     * sincronizando el borrado en la base de datos local y en la nube.
     * 
     * @param id El identificador unico del producto que se desea eliminar.
     * @return un record RespuestaControlador confirmando mediante .exito() el
     *         resultado de la operacion.
     * @example Ejemplo de uso
     * 
     *          <pre>{@code
     * // Es buena practica pedir confirmacion en la vista antes de enviar al controlador
     * int confirmacion = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar el producto?");
     * if (confirmacion == JOptionPane.YES_OPTION) {
     * RespuestaControlador<Producto> respuesta = prControlador.eliminarProducto(txtId.getText());
     * if(respuesta.exito()){
     * JOptionPane.showMessageDialog(this, respuesta.mensaje());
     * // Aqui la vista podria limpiar los campos o refrescar la tabla de productos
     * } else {
     * JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Error al Eliminar", JOptionPane.ERROR_MESSAGE);
     * }
     * }
     * </pre>
     */
    public RespuestaControlador<Producto> eliminarProducto(String id) {

        if (id == null)
            return new RespuestaControlador<>(false, "El id es un objeto nulo", null);

        if (id.trim().isEmpty())
            return new RespuestaControlador<>(false, "El campo id esta vacio", null);

        boolean exito = HelperGestorBD.eliminarRegistro(id, "Producto",
                () -> productoDAO.eliminar(id),
                () -> productoOnlineCRUD.eliminarNube(id));

        return exito
                ? new RespuestaControlador<>(true, "Producto eliminado con exito", null)
                : new RespuestaControlador<>(false, "Error al intentar eliminar el producto en el sistema.", null);
    }

    /**
     * Este metodo solicita y extrae la lista completa de todos los productos
     * registrados en el sistema.
     * 
     * @return un record RespuestaControlador que, de ser exitoso, contiene un
     *         ArrayList de objetos Producto en su propiedad .datos().
     * @example Ejemplo de uso
     *
     *          <pre>{@code
     * RespuestaControlador<ArrayList<Producto>> respuesta = prControlador.buscarTodos();
     * if(respuesta.exito()){
     * ArrayList<Producto> lista = respuesta.datos();
     * // Llenar tabla en la vista...
     * }
     * </pre>
     */
    public RespuestaControlador<ArrayList<Producto>> obtenerTodos() {

        ArrayList<Producto> listaProductos = HelperGestorBD.cargarRegistros(
                () -> productoDAO.obteners(),
                () -> productoOnlineCRUD.obtenersNube(Producto.class));

        return !listaProductos.isEmpty()
                ? new RespuestaControlador<>(true, "Imprimiendo todos los productos", listaProductos)
                : new RespuestaControlador<>(false, "La lista esta vacia", null); // se podria retornar la propia lista
                                                                                  // vacia ya que en si no es un error
    }

    /**
     * Metodo privado de soporte (Helper) interno del controlador.
     * Centraliza las validaciones de negocio, nulos y vacios requeridas antes de
     * procesar el ProductoDTO.
     * La vista no tiene acceso a este metodo.
     * 
     * @param productoDTO El objeto con los datos crudos procedentes de la capa de
     *                    presentacion.
     * @return RespuestaControlador indicando (false) qué regla falló exactamente, o
     *         (true) si todos los campos son aptos.
     */
    private RespuestaControlador<Producto> validarCampos(ProductoDTO productoDTO) {

        if (productoDTO.id() == null || productoDTO.nombre() == null || productoDTO.codigo() == null
                || productoDTO.marca() == null || productoDTO.serie() == null || productoDTO.fechaVencimiento() == null
                || productoDTO.stock() == null || productoDTO.idCategoria() == null
                || productoDTO.precioActual() == null)
            return new RespuestaControlador<>(false, "Algun campo del objeto es nulo", null);

        if (productoDTO.id().isEmpty())
            return new RespuestaControlador<>(false, "El id esta vacio", null);
        if (HelperValidacion.validarUUID(productoDTO.id()) > 0)
            return new RespuestaControlador<>(false, "El id no tiene el formato (UUID)", null);

        if (HelperValidacion.validarTodoNombres(productoDTO.nombre()) > 0)
            return new RespuestaControlador<>(false, "El nombre debe tener solo letras", null);

        if (HelperValidacion.validarTodoNombres(productoDTO.codigo()) > 0)
            return new RespuestaControlador<>(false, "codigo con formato incorrecto", null);

        if (HelperValidacion.validarTodoNombres(productoDTO.marca()) > 0)
            return new RespuestaControlador<>(false, "marca con formato incorrecto", null);

        if (HelperValidacion.validarTodoNombres(productoDTO.serie()) > 0)
            return new RespuestaControlador<>(false, "serie con formato incorrecto", null);

        if (productoDTO.fechaVencimiento().isEmpty())
            return new RespuestaControlador<>(false, "la fecha no debe estar vacia", null);

        if (HelperValidacion.ValidarTodoNumero(productoDTO.stock()) > 0)
            return new RespuestaControlador<>(false, "El stock solo debe contener numeros", null);

        if (HelperValidacion.ValidarTodoNumeroDecimal(productoDTO.precioActual()) > 0)
            return new RespuestaControlador<>(false, "El precio actual solo debe contener numeros", null);

        if (productoDTO.idCategoria().isEmpty())
            return new RespuestaControlador<>(false, "El idCategoria no debe estar vacio", null);

        if (HelperValidacion.validarUUID(productoDTO.idCategoria()) > 0)
            return new RespuestaControlador<>(false, "El idCategoria debe tener el formato valido (UUID)", null);

        return new RespuestaControlador<>(true, "todos los campos tienen el formato correcto", null);
    }
}
