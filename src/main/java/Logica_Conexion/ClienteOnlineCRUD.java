package Logica_Conexion;

import Helpers.HelperTiempo;
import Logica_Negocio.Cliente;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que se encarga de hacer el CRUD en la coleccion Cliente de la base de datos de la nube,
 * esta implementa la interfaz generica definida en el mismo paquete, ademas hace uso de la clase GeneralOnlineProviderCRUD
 * para la comunicacion y manejo de la base de datos.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class ClienteOnlineCRUD implements IOnlineCRUD<Cliente> {

    /**
     * Agrega un nuevo registro a la base de datos de la nube
     * @param cliente es el registro de tipo cliente que se quiere agregar a la base de datos
     * @return true si inserta el registro correctamente, de lo contrario false;
     */
    @Override
    public boolean registrarNube(Cliente cliente) {

        if(cliente == null) return false;

        String id = cliente.getId();

        if(GeneralOnlineProviderCRUD.existeRegistro("Cliente", cliente.getClass(), id)) return false;

        Map<String, Object> datos = new HashMap<>();

        datos.put("id", id);
        datos.put("nombre", cliente.getNombre());
        datos.put("apellido", cliente.getApellido());
        datos.put("direccion", cliente.getDireccion());
        datos.put("cedula", cliente.getCedula());
        datos.put("urlImg", cliente.getUrlImg());
        long m = System.currentTimeMillis();
        GeneralOnlineProviderCRUD.guardar("Cliente", id, datos);
        long n = System.currentTimeMillis();
        HelperTiempo.RetornarTiempo(n,m);
        return true;
    }

    /**
     * Busca en la base de datos de la nube el registro que esta en la "tabla" cliente y si lo encuentra lo retorna
     * @param cliente es la clase la cual pertenece a los registros, no se espera una instancia sino la clase.
     * @return true si inserta el registro correctamente, de lo contrario false;
     */
    @Override
    public Cliente obtenerNube(Class<Cliente> cliente, String id) {
        if(id.isEmpty()) return null;
        return GeneralOnlineProviderCRUD.obtener("Cliente", id, cliente);
    }

    /**
     * Busca en la base de datos de la nube todos los registros que estan en la "tabla" cliente y si hay los retorna en una lista
     * @param cliente es la clase la cual pertenece a los registros, no se espera una instancia sino la clase.
     * @return una lista de registros de tipo cliente, de lo contrario null
     */
    @Override
    public ArrayList<Cliente> obtenersNube(Class<Cliente> cliente){
        if(cliente == null) return null;
        return GeneralOnlineProviderCRUD.obteners("Cliente", cliente);
    }

    /**
     * Elimina de la base de datos de la nube el registro identificado con el id pasado por argumento
     * @return true si lo elimina correctamente, de lo contrario false;
     */
    @Override
    public boolean eliminarNube(String id){
        return GeneralOnlineProviderCRUD.eliminar("Cliente", id);
    }

    /**
     * Actualiza en la base de datos de la nube el registro de tipo cliente que se pasa por argumento
     * @param cliente es el registro con los datos nuevos que se quieren actualizar.
     * @return true si se actualiza correctamente, de lo contrario false.
     */
    @Override
    public boolean actualizarNube(Cliente cliente){
        if(cliente == null) return false;

        String id = cliente.getId();

        Map<String, Object> datos = new HashMap<>();
        datos.put("id", id);
        datos.put("nombre", id);
        datos.put("apellido", id);
        datos.put("direccion", id);
        datos.put("cedula", id);
        datos.put("urlImg", id);

        return GeneralOnlineProviderCRUD.actualizar("Cliente", id, datos);
    }
}
