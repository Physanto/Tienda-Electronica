/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logica_Conexion;

import Logica_Negocio.Venta;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que se encarga de hacer el CRUD en la coleccion Venta
 * de la base de datos de la nube.
 * Implementa la interfaz IOnlineCRUD.
 * 
 * @author hiTiimmy
 */
public class VentaOnlineCRUD implements IOnlineCRUD<Venta> {

    /**
     * Registra una venta en la nube.
     */
    @Override
    public boolean registrarNube(Venta venta) {

        if(venta == null) return false;

        String id = venta.getId();

        if(GeneralOnlineProviderCRUD.existeRegistro("Venta", venta.getClass(), id))
            return false;

        Map<String, Object> datos = new HashMap<>();

        datos.put("id", venta.getId());
        datos.put("fechaVenta", venta.getFechaVenta());
        datos.put("totalVenta", venta.getTotalVenta());
        datos.put("metodoPago", venta.getMetodoPago().toString());
        datos.put("idCliente", venta.getIdCliente());

        return GeneralOnlineProviderCRUD.guardar("Venta", id, datos);
    }

    /**
     * Obtiene una venta de la nube por su id.
     */
    @Override
    public Venta obtenerNube(Class<Venta> venta, String id) {

        if(id.isEmpty()) return null;

        return GeneralOnlineProviderCRUD.obtener("Venta", id, venta);
    }

    /**
     * Obtiene todas las ventas de la nube.
     */
    @Override
    public ArrayList<Venta> obtenersNube(Class<Venta> venta) {

        if(venta == null) return null;

        return GeneralOnlineProviderCRUD.obteners("Venta", venta);
    }

    /**
     * Elimina una venta de la nube.
     */
    @Override
    public boolean eliminarNube(String id) {

        return GeneralOnlineProviderCRUD.eliminar("Venta", id);
    }

    /**
     * Actualiza una venta en la nube.
     */
    @Override
    public boolean actualizarNube(Venta venta) {

        if(venta == null) return false;

        String id = venta.getId();

        Map<String, Object> datos = new HashMap<>();

        datos.put("id", venta.getId());
        datos.put("fechaVenta", venta.getFechaVenta());
        datos.put("totalVenta", venta.getTotalVenta());
        datos.put("metodoPago", venta.getMetodoPago().toString());
        datos.put("idCliente", venta.getIdCliente());

        return GeneralOnlineProviderCRUD.actualizar("Venta", id, datos);
    }
}