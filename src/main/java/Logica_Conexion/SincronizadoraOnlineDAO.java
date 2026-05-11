package Logica_Conexion;

import Logica_Negocio.Sincronizadora;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que se encarga de hacer el CRUD en la coleccion Sincronizadora de la base de datos de la nube,
 * esta implementa la interfaz generica definida en el mismo paquete, ademas hace uso de la clase GeneralOnlineProviderCRUD
 * para la comunicacion y manejo de la base de datos.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class SincronizadoraOnlineDAO implements IOnlineCRUD<Sincronizadora> {

    /**
     * Agrega un nuevo registro a la base de datos de la nube
     * @param sincronizadora es el registro de tipo categoria que se quiere agregar a la base de datos
     * @return true si inserta el registro correctamente, de lo contrario false;
     */
    @Override
    public boolean registrarNube(Sincronizadora sincronizadora) {

        if(sincronizadora == null) return false;

        String id = sincronizadora.getId();

        if(GeneralOnlineProviderCRUD.existeRegistro("Sincronizadora", sincronizadora.getClass(), id)) return false;

        Map<String, Object> datos = new HashMap<>();
        datos.put("id", id);
        datos.put("accion", sincronizadora.getAccion().name());
        datos.put("nombre", sincronizadora.getTablaAfectada());
        datos.put("marca", sincronizadora.getIdRegistroAfectado());
        datos.put("serie", sincronizadora.getTiempo());

        return GeneralOnlineProviderCRUD.guardar("Sincronizadora", id, datos);
    }

    /**
     * Busca en la base de datos de la nube el registro que esta en la "tabla" sincronizadora y si lo encuentra lo retorna
     * @param sincronizadora es la clase la cual pertenece a los registros, no se espera una instancia sino la clase.
     * @return true si inserta el registro correctamente, de lo contrario false;
     */
    @Override
    public Sincronizadora obtenerNube(Class<Sincronizadora> sincronizadora, String id) {
        if(id.isEmpty()) return null;
        return GeneralOnlineProviderCRUD.obtener("Sincronizadora", id, sincronizadora);
    }

    /**
     * Busca en la base de datos de la nube todos los registros que estan en la "tabla" sincronizadora y si hay los retorna en una lista
     * @param sincronizadora es la clase la cual pertenece a los registros, no se espera una instancia sino la clase.
     * @return una lista de registros de tipo sincronizadora, de lo contrario null
     */
    @Override
    public ArrayList<Sincronizadora> obtenersNube(Class<Sincronizadora> sincronizadora){
        if(sincronizadora == null) return null;
        return GeneralOnlineProviderCRUD.obteners("Sincronizadora", sincronizadora);
    }

    /**
     * Elimina de la base de datos de la nube el registro identificado con el id pasado por argumento
     * @param sincronizadora es la clase la cual pertenece al registro buscado, no se espera una instancia sino la clase.
     * @return true si lo elimina correctamente, de lo contrario false;
     */
    @Override
    public boolean eliminarNube(Class<Sincronizadora> sincronizadora, String id){
        if(sincronizadora == null) return false;
        return GeneralOnlineProviderCRUD.eliminar("Sincronizadora", id, sincronizadora);
    }

    /**
     * Actualiza en la base de datos de la nube el registro de tipo sincronizadora que se pasa por argumento
     * @param sincronizadora es el registro con los datos nuevos que se quieren actualizar.
     * @return true si se actualiza correctamente, de lo contrario false.
     */
    @Override
    public boolean actualizarNube(Sincronizadora sincronizadora){
        if(sincronizadora == null) return false;

        String id = sincronizadora.getId();

        Map<String, Object> datos = new HashMap<>();
        datos.put("id", id);
        datos.put("accion", sincronizadora.getAccion().name());
        datos.put("nombre", sincronizadora.getTablaAfectada());
        datos.put("marca", sincronizadora.getIdRegistroAfectado());
        datos.put("serie", sincronizadora.getTiempo());

        return GeneralOnlineProviderCRUD.actualizar("Sincronizadora", id, datos);
    }
}
