package CapaLogicaNegocio.Helpers;

import CapaDatos.Logica_Conexion.SincronizadoraDAO;
import CapaDatos.Logica_Conexion.ILocalDAO;
import CapaLogicaNegocio.Logica_Negocio.Sincronizadora;
import CapaDatos.Logica_Conexion.SincronizadoraOnlineCRUD;
import CapaDatos.Logica_Conexion.*;
import CapaLogicaNegocio.Logica_Negocio.*;
import com.google.gson.Gson;
import java.util.ArrayList;

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
            ArrayList<Sincronizadora> datosPendientes = new SincronizadoraDAO().obtenerNoSincronizados("0");

            if(datosPendientes.isEmpty()){
                System.out.println("No hay datos pendientes por subir");
                return;
            }
            for (Sincronizadora sincronizadora : datosPendientes) {

                if(!aplicarEnNube(sincronizadora)) {
                    System.out.println("No se puede sincronizar");
                    continue;
                }
                System.out.println("sincronizado correctamente");
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
                    return upsertLocal(dao, id, cliente);
                }
                case "Categoria": {
                    CategoriaDAO categoriaDao = new CategoriaDAO();
                    if (accion == Sincronizadora.Accion.DELETE) return categoriaDao.eliminar(id);
                    Categoria categoria = gson.fromJson(registroJson, Categoria.class);
                    return upsertLocal(categoriaDao, id, categoria);
                }
                case "Producto": {
                    ProductoDAO dao = new ProductoDAO();
                    if(accion == Sincronizadora.Accion.DELETE) return dao.eliminar(id);
                    Producto producto = gson.fromJson(registroJson, Producto.class);
                    return upsertLocal(dao, id, producto);
                }
                case "Venta": {
                    VentaDAO dao = new VentaDAO();
                    if(accion == Sincronizadora.Accion.DELETE) return dao.eliminar(id);
                    Venta venta = gson.fromJson(registroJson, Venta.class);
                    return upsertLocal(dao, id, venta);
                }
                case "DetalleVenta": {
                    DetalleVentaDAO dao = new DetalleVentaDAO();
                    if (accion == Sincronizadora.Accion.DELETE) return dao.eliminar(id);
                    DetalleVenta detalle = gson.fromJson(registroJson, DetalleVenta.class);
                    return upsertLocal(dao, id, detalle);
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

    private static <T> boolean upsertLocal(ILocalDAO<T> dao, String id, T obj){
        boolean existe = dao.obtener(id) != null;
        return existe ? dao.actualizar(obj) : dao.agregar(obj);
    }

    /**
     * Aplica en la base de datos de la nube el evento de sincronizacion proveniente de la cola local. Reconstruye el objeto
     * tipado a partir del JSON segun la tabla afectada y delega en el OnlineCRUD correspondiente, de modo que las fechas
     * viajan como Date nativo (Firestore Timestamp), igual que en la escritura directa a la nube. Asi ambas bases reciben
     * el mismo formato y no quedan fechas como String.
     * @param evento el registro de la cola local que se quiere aplicar en la nube
     * @return true si la operacion se aplico correctamente en la nube, de lo contrario false
     */
    private static boolean aplicarEnNube(Sincronizadora evento){
        Gson gson = new Gson();
        String tabla = evento.getTablaAfectada();
        String id = evento.getIdRegistroAfectado();
        String registroJson = evento.getRegistroJson();
        Sincronizadora.Accion accion = evento.getAccion();

        try{
            switch (tabla) {
                case "Cliente": {
                    ClienteOnlineCRUD crud = new ClienteOnlineCRUD();
                    if(accion == Sincronizadora.Accion.DELETE) return crud.eliminarNube(id);
                    Cliente cliente = gson.fromJson(registroJson, Cliente.class);
                    return upsertNube(crud, Cliente.class, id, cliente);
                }
                case "Categoria": {
                    CategoriaOnlineCRUD crud = new CategoriaOnlineCRUD();
                    if(accion == Sincronizadora.Accion.DELETE) return crud.eliminarNube(id);
                    Categoria categoria = gson.fromJson(registroJson, Categoria.class);
                    return upsertNube(crud, Categoria.class, id, categoria);
                }
                case "Producto": {
                    ProductoOnlineCRUD crud = new ProductoOnlineCRUD();
                    if(accion == Sincronizadora.Accion.DELETE) return crud.eliminarNube(id);
                    Producto producto = gson.fromJson(registroJson, Producto.class);
                    return upsertNube(crud, Producto.class, id, producto);
                }
                case "Venta": {
                    VentaOnlineCRUD crud = new VentaOnlineCRUD();
                    if(accion == Sincronizadora.Accion.DELETE) return crud.eliminarNube(id);
                    Venta venta = gson.fromJson(registroJson, Venta.class);
                    return upsertNube(crud, Venta.class, id, venta);
                }
                case "DetalleVenta": {
                    DetalleVentaOnlineCRUD crud = new DetalleVentaOnlineCRUD();
                    if(accion == Sincronizadora.Accion.DELETE) return crud.eliminarNube(id);
                    DetalleVenta detalle = gson.fromJson(registroJson, DetalleVenta.class);
                    return upsertNube(crud, DetalleVenta.class, id, detalle);
                }
                // TODO: agregar el case "Promocion" cuando exista PromocionOnlineCRUD (sincronizacion de promociones a la nube pendiente)
                default:
                    System.out.println("Tabla no soportada en la sincronizacion hacia la nube: " + tabla);
                    return false;
            }
        }
        catch (Exception e){
            System.out.println("Error aplicando el evento en la nube (" + tabla + "/" + accion + "): " + e.getMessage());
            return false;
        }
    }

    private static <T> boolean upsertNube(IOnlineCRUD<T> crud, Class<T> clase, String id, T obj){
        boolean existe = crud.obtenerNube(clase, id) != null;
        return existe ? crud.actualizarNube(obj) : crud.registrarNube(obj);
    }
}
