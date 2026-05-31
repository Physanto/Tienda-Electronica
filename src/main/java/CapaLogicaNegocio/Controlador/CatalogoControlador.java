package CapaLogicaNegocio.Controlador;

import CapaDatos.Logica_Conexion.ProductoOnlineCRUD;
import CapaLogicaNegocio.Logica_Negocio.Producto;

import java.util.ArrayList;

/**
 * Clase Controladora del modulo Cliente encargada de exponer el catalogo de productos.
 * A diferencia de {@link ProductoControlador} (que pasa por HelperGestorBD y prioriza la base
 * local), este controlador consulta UNICAMENTE la base de datos de la nube, ya que el cliente
 * solo opera en linea y nunca toca la base local de la tienda. CADA COSA del negocio debe pasar
 * por el controlador, la vista solo conoce el controlador. nada mas.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class CatalogoControlador {

    private ProductoOnlineCRUD productoOnlineCRUD;

    public CatalogoControlador(){
        this.productoOnlineCRUD = new ProductoOnlineCRUD();
    }

    /**
     * Lista todos los productos del catalogo directamente desde la nube.
     * @return un record RespuestaControlador que, de ser exitoso, contiene un ArrayList de objetos Producto en su propiedad .dato()
     *
     * @example Ejemplo de uso desde la vista
     * <pre>{@code
     * CatalogoControlador catalogoControlador = new CatalogoControlador();
     * RespuestaControlador<ArrayList<Producto>> respuesta = catalogoControlador.listarCatalogo();
     *
     * if(respuesta.exito()){
     *     ArrayList<Producto> productos = respuesta.dato();
     *     // Llenar la tabla/grid del catalogo en la vista...
     * } else {
     *     JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Catalogo", JOptionPane.WARNING_MESSAGE);
     * }
     * }
     * </pre>
     */
    public RespuestaControlador<ArrayList<Producto>> listarCatalogo(){

        ArrayList<Producto> productos = productoOnlineCRUD.obtenersNube(Producto.class);

        if(productos == null) return new RespuestaControlador<>(false, "No se pudo conectar con la nube", null);

        return !productos.isEmpty()
                ? new RespuestaControlador<>(true, "Catalogo cargado correctamente", productos)
                : new RespuestaControlador<>(false, "El catalogo esta vacio", productos);
    }

    /**
     * Lista solo los productos que tienen stock disponible (stock mayor a 0), util para mostrarle
     * al cliente unicamente lo que realmente puede comprar.
     * @return un record RespuestaControlador con la lista de productos disponibles en su propiedad .dato()
     */
    public RespuestaControlador<ArrayList<Producto>> listarDisponibles(){

        RespuestaControlador<ArrayList<Producto>> respuesta = listarCatalogo();

        if(!respuesta.exito()) return respuesta;

        ArrayList<Producto> disponibles = new ArrayList<>();
        for(Producto producto : respuesta.dato()){
            if(producto.getStock() != null && producto.getStock() > 0) disponibles.add(producto);
        }

        return !disponibles.isEmpty()
                ? new RespuestaControlador<>(true, "Productos disponibles cargados correctamente", disponibles)
                : new RespuestaControlador<>(false, "No hay productos con stock disponible", disponibles);
    }

    /**
     * Busca un producto especifico del catalogo mediante su id, directamente desde la nube.
     * @param id El identificador unico (UUID) del producto a buscar (capturado desde la vista).
     * @return un record RespuestaControlador con el Producto encontrado en su propiedad .dato() (o null si no existe).
     *
     * @example Ejemplo de uso desde la vista
     * <pre>{@code
     * RespuestaControlador<Producto> respuesta = catalogoControlador.buscarProductoId(txtBuscarId.getText());
     * if(respuesta.exito()){
     *     Producto producto = respuesta.dato();
     *     // Mostrar el detalle del producto...
     * }
     * }
     * </pre>
     */
    public RespuestaControlador<Producto> buscarProductoId(String id){

        if(id == null) return new RespuestaControlador<>(false, "El id es nulo", null);

        if(id.isEmpty()) return new RespuestaControlador<>(false, "El campo id esta vacio", null);

        Producto producto = productoOnlineCRUD.obtenerNube(Producto.class, id);

        return producto != null
                ? new RespuestaControlador<>(true, "Producto encontrado con exito", producto)
                : new RespuestaControlador<>(false, "Producto no encontrado con ese id", null);
    }
}
