package CapaDatos.Logica_Conexion;

import CapaLogicaNegocio.DTOS.Promociones;
import com.google.cloud.firestore.Firestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que se encarga de hacer el CRUD en la coleccion Promocion de la base de datos de la nube.
 * Permite que el Admin publique las promociones a la nube y que el modulo Cliente (que solo opera
 * en linea) pueda leerlas. Implementa la interfaz generica definida en el mismo paquete y se apoya
 * en GeneralOnlineProviderCRUD para la comunicacion con Firebase.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class PromocionOnlineCRUD implements IOnlineCRUD<Promociones> {

    /**
     * Agrega o sobrescribe una promocion en la base de datos de la nube.
     * @param promocion la promocion que se quiere publicar en la nube
     * @return true si se guarda correctamente, de lo contrario false
     */
    @Override
    public boolean registrarNube(Promociones promocion) {
        if (promocion == null) return false;
        return GeneralOnlineProviderCRUD.guardar("Promocion", promocion.getId(), aMapa(promocion));
    }

    /**
     * Busca en la nube la promocion identificada con el id pasado por argumento.
     * @param clase la clase de los registros (Promociones.class)
     * @param id el identificador de la promocion a buscar
     * @return la promocion encontrada, de lo contrario null
     */
    @Override
    public Promociones obtenerNube(Class<Promociones> clase, String id) {
        if (id == null || id.isEmpty()) return null;
        return GeneralOnlineProviderCRUD.obtener("Promocion", id, clase);
    }

    /**
     * Trae de la nube todas las promociones registradas.
     * @param clase la clase de los registros (Promociones.class)
     * @return una lista con todas las promociones, de lo contrario null
     */
    @Override
    public ArrayList<Promociones> obtenersNube(Class<Promociones> clase) {
        if (clase == null) return null;
        return GeneralOnlineProviderCRUD.obteners("Promocion", clase);
    }

    /**
     * Elimina de la nube la promocion identificada con el id pasado por argumento.
     * @return true si se elimina correctamente, de lo contrario false
     */
    @Override
    public boolean eliminarNube(String id) {
        return GeneralOnlineProviderCRUD.eliminar("Promocion", id);
    }

    /**
     * Actualiza en la nube la promocion pasada por argumento.
     * @param promocion la promocion con los datos nuevos
     * @return true si se actualiza correctamente, de lo contrario false
     */
    @Override
    public boolean actualizarNube(Promociones promocion) {
        if (promocion == null) return false;
        return GeneralOnlineProviderCRUD.actualizar("Promocion", promocion.getId(), aMapa(promocion));
    }

    /**
     * Desactiva en Firestore la promocion poniendo su fechaFin al momento actual (partial update).
     * @param idPromocion id de la promocion a desactivar
     * @param nuevaFechaFin fecha que se usara como nuevo fechaFin (normalmente la fecha actual)
     * @return true si se actualizo correctamente, false si fallo o la nube no esta disponible
     */
    public boolean desactivarNube(String idPromocion, java.util.Date nuevaFechaFin) {
        try {
            Firestore db = Conexion.getConexionNube();
            if (db == null) return false;
            Map<String, Object> campos = new HashMap<>();
            campos.put("fechaFin", nuevaFechaFin);
            db.collection("Promocion").document(idPromocion).update(campos).get();
            return true;
        } catch (Exception e) {
            System.out.println("Error desactivando promo en nube: " + e.getMessage());
            return false;
        }
    }

    /**
     * Construye el mapa de datos que se envia a Firebase. El tipo (enum) se guarda como texto para
     * que sea serializable y se reconstruya al leerlo de vuelta.
     */
    private Map<String, Object> aMapa(Promociones promocion) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("id", promocion.getId());
        datos.put("nombre", promocion.getNombre());
        datos.put("descuento", promocion.getDescuento());
        datos.put("fechaInicio", promocion.getFechaInicio());
        datos.put("fechaFin", promocion.getFechaFin());
        datos.put("tipo", promocion.getTipo() != null ? promocion.getTipo().name() : null);
        // Vinculos denormalizados para el read model del Cliente: a que producto/cliente aplica.
        datos.put("idProducto", promocion.getIdProducto());
        datos.put("idCliente", promocion.getIdCliente());
        return datos;
    }
}
