package CapaLogicaNegocio.Helpers;

import CapaDatos.Logica_Conexion.SincronizadoraDAO;
import CapaDatos.Logica_Conexion.GeneralOnlineProviderCRUD;
import CapaDatos.Logica_Conexion.ILocalDAO;
import CapaLogicaNegocio.Logica_Negocio.Sincronizadora;
import CapaDatos.Logica_Conexion.SincronizadoraOnlineCRUD;
import CapaDatos.Logica_Conexion.*;
import CapaLogicaNegocio.Logica_Negocio.*;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Map;

/**
 * Clase encargada de Sincronizar la informacion de las dos bases de datos existentes, esto
 * para la implementacion de un metodo de redundancia optimo
 *
 * @author Manuel Figueroa (Physanto)
 */
public class HelperSincronizador {

    /**
     * Metodo que se encarga de sincronizar la base de datos local a la nube
     * este metodo te hace toda la sincronizacion independientemente de si
     * insertas, eliminas o actualizas un registro
     */
    public static void SincronizarLocalANube(){
        System.out.println("Iniciando vaciado de cola local hacia la nube...");

        try{
            Gson gson = new Gson();
            ArrayList<Sincronizadora> datosPendientes = new SincronizadoraDAO().obtenerNoSincronizados("0");

            if(datosPendientes.isEmpty()){
                System.out.println("No hay datos pendientes por subir");
                return;
            }
            for (Sincronizadora sincronizadora : datosPendientes) {

                String registroJson = sincronizadora.getRegistroJson();
                String tablaAfectada = sincronizadora.getTablaAfectada();
                String idRegistro = sincronizadora.getIdRegistroAfectado();
                Map<String, Object> datos = gson.fromJson(registroJson, Map.class);

                switch (sincronizadora.getAccion().name()) {
                    case "INSERT":
                        GeneralOnlineProviderCRUD.guardar(tablaAfectada, idRegistro, datos);
                        break;
                    case "UPDATE":
                        GeneralOnlineProviderCRUD.actualizar(tablaAfectada, idRegistro, datos);
                        break;
                    case "DELETE":
                        GeneralOnlineProviderCRUD.eliminar(tablaAfectada, idRegistro);
                        break;
                }
                System.out.println("Registro con id: " + idRegistro + " faltante fue sincronizado correctamente ");
                if(new SincronizadoraDAO().actualizarSincronizados(sincronizadora.getId())){
                    System.out.println("Cambio el estado del registro a 1, sincronizado correctamente");
                }
            }
        }
        catch (Exception e){
            System.out.println("Error durante la sincronizacion: " + e.getMessage());
        }
    }

    /**
     * Metodo que se encarga de sincronizar la cola de la nube hacia la base de datos local. Se invoca cuando la base
     * local vuelve a estar disponible, para aplicarle todos los cambios (INSERT, UPDATE, DELETE) que se hicieron
     * directamente contra la nube mientras la local estuvo caida. Cada pendiente aplicado se marca como sincronizado
     * (estado 1) en la coleccion Sincronizadora de la nube.
     */
    public static void SincronizarNubeALocal(){
        System.out.println("Iniciando bajada de cola de la nube hacia local...");

        try{
            ArrayList<Sincronizadora> datosPendientes = new SincronizadoraOnlineCRUD().obtenersNube(Sincronizadora.class);

            if(datosPendientes == null || datosPendientes.isEmpty()){
                System.out.println("No hay datos pendientes por bajar");
                return;
            }
            for (Sincronizadora sincronizadora : datosPendientes) {

                if(!"0".equals(sincronizadora.getEstado())) continue;

                if(aplicarEnLocal(sincronizadora)){
                    System.out.println("Registro con id: " + sincronizadora.getIdRegistroAfectado() + " fue bajado a local correctamente");

                    sincronizadora.setEstado("1");
                    if(new SincronizadoraOnlineCRUD().actualizarNube(sincronizadora)){
                        System.out.println("Cambio el estado del registro a 1 en la nube, sincronizado correctamente");
                    }
                }
            }
        }
        catch (Exception e){
            System.out.println("Error durante la sincronizacion inversa: " + e.getMessage());
        }
    }

    /**
     * Aplica en la base de datos local el evento de sincronizacion proveniente de la nube. Reconstruye el objeto a partir
     * del JSON segun la tabla afectada y delega en el DAO local correspondiente.
     * @param evento el registro de la cola de la nube que se quiere aplicar en local
     * @return true si la operacion se aplico correctamente en local, de lo contrario false
     */
    private static boolean aplicarEnLocal(Sincronizadora evento){
        Gson gson = new Gson();
        String tabla = evento.getTablaAfectada();
        String id = evento.getIdRegistroAfectado();
        String registroJson = evento.getRegistroJson();
        Sincronizadora.Accion accion = evento.getAccion();

        try{
            switch (tabla) {
                case "Cliente": {
                    ClienteDAO dao = new ClienteDAO();
                    if(accion == Sincronizadora.Accion.DELETE) return dao.eliminar(id);
                    Cliente cliente = gson.fromJson(registroJson, Cliente.class);
                    return upsertLocal(dao, id, cliente, accion);
                }
                case "Categoria": {
                    CategoriaDAO categoriaDao = new CategoriaDAO();
                    if (accion == Sincronizadora.Accion.DELETE) return categoriaDao.eliminar(id);
                    Categoria categoria = gson.fromJson(registroJson, Categoria.class);
                    return upsertLocal(categoriaDao, id, categoria, accion);
                }
                case "Producto": {
                    ProductoDAO dao = new ProductoDAO();
                    if(accion == Sincronizadora.Accion.DELETE) return dao.eliminar(id);
                    Producto producto = gson.fromJson(registroJson, Producto.class);
                    return upsertLocal(dao, id, producto, accion);
                }
                case "Venta": {
                    VentaDAO dao = new VentaDAO();
                    if(accion == Sincronizadora.Accion.DELETE) return dao.eliminar(id);
                    Venta venta = gson.fromJson(registroJson, Venta.class);
                    return upsertLocal(dao, id, venta, accion);
                }
                case "DetalleVenta": {
                    DetalleVentaDAO dao = new DetalleVentaDAO();
                    if (accion == Sincronizadora.Accion.DELETE) return dao.eliminar(id);
                    DetalleVenta detalle = gson.fromJson(registroJson, DetalleVenta.class);
                    return upsertLocal(dao, id, detalle, accion);
                }
                default:
                    System.out.println("Tabla no soportada en la sincronizacion inversa: " + tabla);
                    return false;
            }
        }
        catch (Exception e){
            System.out.println("Error aplicando el evento en local (" + tabla + "/" + accion + "): " + e.getMessage());
            return false;
        }
    }

    private static <T> boolean upsertLocal(ILocalDAO<T> dao, String id, T obj,
                                           Sincronizadora.Accion accion){
        boolean existe = dao.obtener(id) != null;
        if (accion == Sincronizadora.Accion.INSERT) {
            return existe ? dao.actualizar(obj) : dao.agregar(obj);
        }
        // UPDATE
        return existe ? dao.actualizar(obj) : dao.agregar(obj);
    }
}
