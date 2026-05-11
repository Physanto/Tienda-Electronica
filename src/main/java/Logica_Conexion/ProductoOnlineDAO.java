package Logica_Conexion;

import Logica_Negocio.Producto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que se encarga de hacer el CRUD en la coleccion Producto de la base de datos de la nube,
 * esta implementa la interfaz generica definida en el mismo paquete, ademas hace uso de la clase GeneralOnlineProviderCRUD
 * para la comunicacion y manejo de la base de datos.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class ProductoOnlineDAO implements IOnlineCRUD<Producto> {

    /**
     * Agrega un nuevo registro a la base de datos de la nube
     * @param producto es el registro de tipo producto que se quiere agregar a la base de datos
     * @return true si inserta el registro correctamente, de lo contrario false;
     */
    @Override
    public boolean registrarNube(Producto producto) {

        if(producto == null) return false;

        String id = producto.getId();

        if(GeneralOnlineProviderCRUD.existeRegistro("Producto", producto.getClass(), id)) return false;

        Map<String, Object> datos = new HashMap<>();
        datos.put("id", id);
        datos.put("codigo", producto.getCodigo());
        datos.put("nombre", producto.getNombre());
        datos.put("marca", producto.getMarca());
        datos.put("serie", producto.getSerie());
        datos.put("stock", producto.getStock());
        datos.put("precioActual", producto.getPrecioActual());
        datos.put("fechaVencimiento", producto.getFechaVencimiento());
        datos.put("urlImg", producto.getUrlImg());
        datos.put("idCategoria", producto.getIdCategoria());

        return GeneralOnlineProviderCRUD.guardar("Producto", id, datos);
    }

    /**
     * Busca en la base de datos de la nube el registro que esta en la "tabla" producto y si lo encuentra lo retorna
     * @param producto es la clase la cual pertenece a los registros, no se espera una instancia sino la clase.
     * @return true si inserta el registro correctamente, de lo contrario false;
     */
    @Override
    public Producto obtenerNube(Class<Producto> producto, String id) {
        if(id.isEmpty()) return null;
        return GeneralOnlineProviderCRUD.obtener("Producto", id, producto);
    }

    /**
     * Busca en la base de datos de la nube todos los registros que estan en la "tabla" producto y si hay los retorna en una lista
     * @param producto es la clase la cual pertenece a los registros, no se espera una instancia sino la clase.
     * @return una lista de registros de tipo producto, de lo contrario null
     */
    @Override
    public ArrayList<Producto> obtenersNube(Class<Producto> producto){
        if(producto == null) return null;
        return GeneralOnlineProviderCRUD.obteners("Producto", producto);
    }

    /**
     * Elimina de la base de datos de la nube el registro identificado con el id pasado por argumento
     * @param producto es la clase la cual pertenece al registro buscado, no se espera una instancia sino la clase.
     * @return true si lo elimina correctamente, de lo contrario false;
     */
    @Override
    public boolean eliminarNube(Class<Producto> producto, String id){
        if(producto == null) return false;
        return GeneralOnlineProviderCRUD.eliminar("Producto", id, producto);
    }

    /**
     * Actualiza en la base de datos de la nube el registro de tipo producto que se pasa por argumento
     * @param producto es el registro con los datos nuevos que se quieren actualizar.
     * @return true si se actualiza correctamente, de lo contrario false.
     */
    @Override
    public boolean actualizarNube(Producto producto){
        if(producto == null) return false;

        String id = producto.getId();

        Map<String, Object> datos = new HashMap<>();
        datos.put("id", id);
        datos.put("codigo", producto.getCodigo());
        datos.put("nombre", producto.getNombre());
        datos.put("marca", producto.getMarca());
        datos.put("serie", producto.getSerie());
        datos.put("stock", producto.getStock());
        datos.put("precioActual", producto.getPrecioActual());
        datos.put("fechaVencimiento", producto.getFechaVencimiento());
        datos.put("urlImg", producto.getUrlImg());
        datos.put("idCategoria", producto.getIdCategoria());

        return GeneralOnlineProviderCRUD.actualizar("Producto", id, datos);
    }
}
