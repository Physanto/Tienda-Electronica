package CapaDatos.Logica_Conexion;

import CapaLogicaNegocio.Logica_Negocio.PromocionCliente;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que se encarga de hacer el CRUD en la coleccion PromocionCliente de la base de datos de
 * la nube. Replica en Firestore el vinculo Promocion->Cliente que en local vive en la tabla
 * homonima, para que el dato tenga paridad entre ambas bases y pueda sincronizarse local->nube.
 * Sigue el mismo patron generico que el resto de *OnlineCRUD apoyandose en GeneralOnlineProviderCRUD.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class PromocionClienteOnlineCRUD implements IOnlineCRUD<PromocionCliente> {

    @Override
    public boolean registrarNube(PromocionCliente promocionCliente) {
        if (promocionCliente == null) return false;

        String id = promocionCliente.getId();

        if (GeneralOnlineProviderCRUD.existeRegistro("PromocionCliente", promocionCliente.getClass(), id))
            return false;

        return GeneralOnlineProviderCRUD.guardar("PromocionCliente", id, aMapa(promocionCliente));
    }

    @Override
    public PromocionCliente obtenerNube(Class<PromocionCliente> clase, String id) {
        if (id == null || id.isEmpty()) return null;
        return GeneralOnlineProviderCRUD.obtener("PromocionCliente", id, clase);
    }

    @Override
    public ArrayList<PromocionCliente> obtenersNube(Class<PromocionCliente> clase) {
        if (clase == null) return new ArrayList<>();
        return GeneralOnlineProviderCRUD.obteners("PromocionCliente", clase);
    }

    @Override
    public boolean eliminarNube(String id) {
        return GeneralOnlineProviderCRUD.eliminar("PromocionCliente", id);
    }

    @Override
    public boolean actualizarNube(PromocionCliente promocionCliente) {
        if (promocionCliente == null) return false;
        return GeneralOnlineProviderCRUD.actualizar("PromocionCliente", promocionCliente.getId(),
                aMapa(promocionCliente));
    }

    private Map<String, Object> aMapa(PromocionCliente promocionCliente) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("id", promocionCliente.getId());
        datos.put("idPromocion", promocionCliente.getIdPromocion());
        datos.put("idCliente", promocionCliente.getIdCliente());
        return datos;
    }
}
