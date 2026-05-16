/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logica_Conexion;

import Logica_Negocio.DetalleVenta;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que se encarga de hacer el CRUD en la coleccion
 * DetalleVenta de la base de datos de la nube.
 * Implementa la interfaz IOnlineCRUD.
 * 
 * @author hiTiimmy
 */
public class DetalleVentaOnlineCRUD implements IOnlineCRUD<DetalleVenta> {

    /**
     * Registra un detalle de venta en la nube.
     */
    @Override
    public boolean registrarNube(DetalleVenta detalleVenta) {

        if(detalleVenta == null) return false;

        String id = detalleVenta.getId();

        if(GeneralOnlineProviderCRUD.existeRegistro("DetalleVenta", detalleVenta.getClass(), id))
            return false;

        Map<String, Object> datos = new HashMap<>();

        datos.put("id", detalleVenta.getId());
        datos.put("cantidad", detalleVenta.getCantidad());
        datos.put("subtotal", detalleVenta.getSubtotal());
        datos.put("precioVenta", detalleVenta.getPrecioVenta());
        datos.put("idProducto", detalleVenta.getIdProducto());
        datos.put("idVenta", detalleVenta.getIdVenta());

        return GeneralOnlineProviderCRUD.guardar("DetalleVenta", id, datos);
    }

    /**
     * Obtiene un detalle de venta por id.
     */
    @Override
    public DetalleVenta obtenerNube(Class<DetalleVenta> detalleVenta, String id) {

        if(id.isEmpty()) return null;

        return GeneralOnlineProviderCRUD.obtener("DetalleVenta", id, detalleVenta);
    }

    /**
     * Obtiene todos los detalles de venta.
     */
    @Override
    public ArrayList<DetalleVenta> obtenersNube(Class<DetalleVenta> detalleVenta) {

        if(detalleVenta == null) return null;

        return GeneralOnlineProviderCRUD.obteners("DetalleVenta", detalleVenta);
    }

    /**
     * Elimina un detalle de venta.
     */
    @Override
    public boolean eliminarNube(String id) {

        return GeneralOnlineProviderCRUD.eliminar("DetalleVenta", id);
    }

    /**
     * Actualiza un detalle de venta.
     */
    @Override
    public boolean actualizarNube(DetalleVenta detalleVenta) {

        if(detalleVenta == null) return false;

        String id = detalleVenta.getId();

        Map<String, Object> datos = new HashMap<>();

        datos.put("id", detalleVenta.getId());
        datos.put("cantidad", detalleVenta.getCantidad());
        datos.put("subtotal", detalleVenta.getSubtotal());
        datos.put("precioVenta", detalleVenta.getPrecioVenta());
        datos.put("idProducto", detalleVenta.getIdProducto());
        datos.put("idVenta", detalleVenta.getIdVenta());

        return GeneralOnlineProviderCRUD.actualizar("DetalleVenta", id, datos);
    }
}
