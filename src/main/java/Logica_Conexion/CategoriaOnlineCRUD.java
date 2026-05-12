package Logica_Conexion;

import Logica_Negocio.Categoria;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que se encarga de hacer el CRUD en la coleccion Categoria de la base de datos de la nube,
 * esta implementa la interfaz generica definida en el mismo paquete, ademas hace uso de la clase GeneralOnlineProviderCRUD
 * para la comunicacion y manejo de la base de datos.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class CategoriaOnlineCRUD implements IOnlineCRUD<Categoria> {

    /**
     * Agrega un nuevo registro a la base de datos de la nube
     * @param categoria es el registro de tipo categoria que se quiere agregar a la base de datos
     * @return true si inserta el registro correctamente, de lo contrario false;
     */
    @Override
    public boolean registrarNube(Categoria categoria) {

        if(categoria == null) return false;

        String id = categoria.getId();

        if(GeneralOnlineProviderCRUD.existeRegistro("Categoria", categoria.getClass(), id)) return false;

        Map<String, Object> datos = new HashMap<>();
        datos.put("id", id);
        datos.put("nombre", categoria.getNombre());

        return GeneralOnlineProviderCRUD.guardar("Categoria", id, datos);
    }

    /**
     * Busca en la base de datos de la nube el registro que esta en la "tabla" categoria y si lo encuentra lo retorna
     * @param categoria es la clase la cual pertenece a los registros, no se espera una instancia sino la clase.
     * @return true si inserta el registro correctamente, de lo contrario false;
     */
    @Override
    public Categoria obtenerNube(Class<Categoria> categoria, String id) {
        if(id.isEmpty()) return null;
        return GeneralOnlineProviderCRUD.obtener("Categoria", id, categoria);
    }

    /**
     * Busca en la base de datos de la nube todos los registros que estan en la "tabla" categoria y si hay los retorna en una lista
     * @param categoria es la clase la cual pertenece a los registros, no se espera una instancia sino la clase.
     * @return una lista de registros de tipo categoria, de lo contrario null
     */
    @Override
    public ArrayList<Categoria> obtenersNube(Class<Categoria> categoria){
        if(categoria == null) return null;
        return GeneralOnlineProviderCRUD.obteners("Categoria", categoria);
    }

    /**
     * Elimina de la base de datos de la nube el registro identificado con el id pasado por argumento
     * @return true si lo elimina correctamente, de lo contrario false;
     */
    @Override
    public boolean eliminarNube(String id){
        return GeneralOnlineProviderCRUD.eliminar("Categoria", id);
    }

    /**
     * Actualiza en la base de datos de la nube el registro de tipo categoria que se pasa por argumento
     * @param categoria es el registro con los datos nuevos que se quieren actualizar.
     * @return true si se actualiza correctamente, de lo contrario false.
     */
    @Override
    public boolean actualizarNube(Categoria categoria){
        if(categoria == null) return false;

        String id = categoria.getId();

        Map<String, Object> datos = new HashMap<>();
        datos.put("id", id);
        datos.put("nombre", id);

        return GeneralOnlineProviderCRUD.actualizar("Categoria", id, datos);
    }
}
