package CapaDatos.Logica_Conexion;

import CapaLogicaNegocio.Logica_Negocio.PromocionProducto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que se encarga de hacer el CRUD en la coleccion PromocionProducto de la base de datos de
 * la nube. Replica en Firestore el vinculo Promocion->Producto que en local vive en la tabla
 * homonima, para que el dato tenga paridad entre ambas bases y pueda sincronizarse local->nube.
 * Sigue el mismo patron generico que el resto de *OnlineCRUD apoyandose en GeneralOnlineProviderCRUD.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class PromocionProductoOnlineCRUD implements IOnlineCRUD<PromocionProducto> {

    @Override
    public boolean registrarNube(PromocionProducto promocionProducto) {
        if (promocionProducto == null) return false;

        String id = promocionProducto.getId();

        if (GeneralOnlineProviderCRUD.existeRegistro("PromocionProducto", promocionProducto.getClass(), id))
            return false;

        return GeneralOnlineProviderCRUD.guardar("PromocionProducto", id, aMapa(promocionProducto));
    }

    @Override
    public PromocionProducto obtenerNube(Class<PromocionProducto> clase, String id) {
        if (id == null || id.isEmpty()) return null;
        return GeneralOnlineProviderCRUD.obtener("PromocionProducto", id, clase);
    }

    @Override
    public ArrayList<PromocionProducto> obtenersNube(Class<PromocionProducto> clase) {
        if (clase == null) return new ArrayList<>();
        return GeneralOnlineProviderCRUD.obteners("PromocionProducto", clase);
    }

    @Override
    public boolean eliminarNube(String id) {
        return GeneralOnlineProviderCRUD.eliminar("PromocionProducto", id);
    }

    @Override
    public boolean actualizarNube(PromocionProducto promocionProducto) {
        if (promocionProducto == null) return false;
        return GeneralOnlineProviderCRUD.actualizar("PromocionProducto", promocionProducto.getId(),
                aMapa(promocionProducto));
    }

    private Map<String, Object> aMapa(PromocionProducto promocionProducto) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("id", promocionProducto.getId());
        datos.put("idPromocion", promocionProducto.getIdPromocion());
        datos.put("idProducto", promocionProducto.getIdProducto());
        return datos;
    }
}
