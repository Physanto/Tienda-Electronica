package Helpers;

import Logica_Conexion.*;
import Logica_Negocio.*;
import com.google.gson.Gson;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.function.Supplier;

// AQUI EN ESTA CLASE SOLO SE ESTA IMPLEMENTANDO UNA SINCRONIZACION UNIDIRECCIONAL, DEJO UN EJEMPLO DE COMO IMPLEMENTAR BIRECCIONAL PARA MEJORAS FUTURAS

/**
 * Clase que se encarga de gestionar las dos bases de datos (Local y nube), lo que hace es que si se quiere por ejemplo
 * guardar, eliminar, actualizar y/o mostrar, gestiona que base de datos esta disponible y funcionando para el CRUD.
 * Tambien deja el registro que no esta sincronizado en una cola para su posterior sincronizacion. Cabe aclarar que esta clase
 * se hizo con un enfoque generico para un uso centralizado.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class HelperGestorBD {

    //metodo especifico para guardar un cliente en la nube, se va reemplazar por el generico guardarNube
    public static void GuardarPersonaGeneral(Cliente cliente){

        boolean online = HelperMonitorRed.estaUsandoNube();

        try{
            if(!new ClienteDAO().agregar(cliente)){
                guardarRedundancia(cliente);
                return;
            }
            if(!online){
                String registroJson = new Gson().toJson(cliente);
                new SincronizadoraDAO().agregar(new Sincronizadora(String.valueOf((int) (Math.random() * 100000)), Sincronizadora.Accion.INSERT, "Cliente", cliente.getId(), registroJson, "0"));
            }
            else if(new ClienteOnlineCRUD().registrarNube(cliente)){
                System.out.println("Guardado exitoso en Local y Nube.");
            }
            else {
                System.out.println("Modo Offline activo. Guardado solo en Local (Estado 0).");
            }
        }
        catch(Exception e){
            System.out.println("Error faltal en la insercion de datos " + e.getMessage());
        }
    }

    /**
     * Metodo que se encarga de guardar el registro especificado por argumento en la base de datos que se encuentra disponible y funcionando en el momento
     * este metodo es generico por ende se pide especificamente el registro, id y coleccion aunque suene redundante
     * @param object representa el registro que se desea guardar en la base de datos (Cliente, Producto...)
     * @param coleccion representa la coleccion y/o tabla de la base de datos
     * @param id es el id que va tener el registro en la coleccion y/o tabla
     * @param metodoGuardarLocal es el metodo para guardar un registro en la base de datos local
     * @param metodoGuardarNube es el metodo para guardar un registro en la base de datos de la nube
     * @example Ejemplo de como usar el metodo
     * <pre>{@code
     * guardarNube(object, // es el registro que se quiere almacenar
     * coleccion, // es la coleccion donde se va a almacenar el registro en la base de datos de la nube
     * id, // es el id del registro dentro de la coleccion
     * () -> new ClienteDAO().agregar(cliente), // es cualquier metodo para almacenar un registro en local
     * () -> new ClienteOnlineCRUD().registrarNube(cliente)); // es cualquier metodo para almacenar un registro en la nube
     * }
     * </pre>
     */
     //tratando de realizar una implementacion generica para guardar cualquier tipo de registro
    public static <T> boolean guardarRegistro(T object, String coleccion, String id, Supplier<Boolean> metodoGuardarLocal, Supplier<Boolean> metodoGuardarNube){
        boolean exito = true;
        boolean online = HelperMonitorRed.estaUsandoNube();

        try{
            if(Conexion.conexion == null && Conexion.db == null){
               guardarRedundancia(object);
               return false;
            }
            if(!metodoGuardarLocal.get()){
                guardarRedundancia(object);
                exito = false;
            }
            boolean subidoExitosamente = false;
            if (online) {
                subidoExitosamente = metodoGuardarNube.get();
                if (subidoExitosamente) {
                    System.out.println("Guardado exitoso en Local y Nube.");
                }
            }
            if (!online || !subidoExitosamente) {
                String registroJson = new Gson().toJson(object);
                String idSync = java.util.UUID.randomUUID().toString();
                new SincronizadoraDAO().agregar(new Sincronizadora(idSync, Sincronizadora.Accion.INSERT,
                        coleccion, id, registroJson, "0"));
                System.out.println("Registro enviado a la cola de sincronización (INSERT).");
            }
        }
        catch(Exception e){
            System.out.println("Error faltal en la insercion de datos " + e.getMessage());
        }
        return exito;
    }
    /**
     * Metodo que se encarga de cargar todos los registros de la base de datos que este disponible y en operacion en ese momento
     * @param metodoCargarLocal es el metodo para cargar todos registros de la base de datos local
     * @param metodoCargarNube es el metodo para cargar todos registros de la base de datos de la nube
     * @example Ejemplo de como usar el metodo
     * <pre>{@code
     * cargarRegistros(() -> new ClienteDAO().obteners(), // es cualquier metodo para cargar registros de la bd local
     * () -> new ClienteOnlineCRUD().obtenersNube(Cliente.class)); // es cualquier metodo para cargar registros de la bd nube
     * }
     * </pre>
     */
    public static <T> ArrayList<T> cargarRegistros(Supplier<ArrayList<T>> metodoCargarLocal, Supplier<ArrayList<T>> metodoCargarNube){
        boolean online = HelperMonitorRed.estaUsandoNube();

        ArrayList<T> listaRegistros = new ArrayList<>();
        try{
            listaRegistros = metodoCargarLocal.get();

            if(!listaRegistros.isEmpty()){
                System.out.println("Listando desde local");
               return listaRegistros;
            }
            if(online) {
                System.out.println("Listando desde la nube");
                listaRegistros = metodoCargarNube.get();
            }
        }
        catch(Exception e){
            System.out.println("Error ex faltal listando los datos " + e.getMessage());
        }
        return listaRegistros;
    }
    /**
     * Metodo que se encarga de cargar el registro con id especificado por argumento de la base de datos que este disponible y en operacion en ese momento
     * @param metodoCargarLocal es el metodo para cargar el registro de la base de datos local
     * @param metodoCargarNube es el metodo para cargar el registro de la base de datos de la nube
     * @example Ejemplo de como usar el metodo
     * <pre>{@code
     * cargarRegistro(() -> new ClienteDAO().obtener(id), // es cualquier metodo para cargar un registro de la bd local
     * () -> new ClienteOnlineCRUD().obtenerNube(Cliente.class, id)); // es cualquier metodo para cargar un registro de la bd nube
     * }
     * </pre>
     */
    public static <T> T cargarRegistro(Supplier<T> metodoCargarLocal, Supplier<T> metodoCargarNube){
        boolean online = HelperMonitorRed.estaUsandoNube();
        T object = null;

        try{
            object = metodoCargarLocal.get();

            if(object != null){
                System.out.println("Listando desde local");
                return object;
            }
            if(online) {
                System.out.println("Listando desde la nube");
                object = metodoCargarNube.get();
            }
        }
        catch(Exception e){
            System.out.println("Error Ex faltal listando los datos " + e.getMessage());
        }
        return object;
    }

    /**
     * Metodo que se encarga de eliminar el registro especificado por id de la base de datos que se encuentra disponible y funcionando en el momento
     * @param id es el id del registro a eliminar
     * @param coleccion representa la coleccion y/o tabla de la base de datos
     * @param metodoEliminarLocal es el metodo para eliminar un registro de la base de datos local
     * @param metodoEliminarNube es el metodo para eliminar un registro de la base de datos de la nube
     * @example Ejemplo de como usar el metodo
     * <pre>{@code
     * eliminarRegistro(id, // es el id del registro que se quiere eliminar
     * coleccion, // es la coleccion y/o tabla de la base de datos
     * () -> new ClienteDAO().eliminar(id), // es cualquier metodo para eliminar un registro de local
     * () -> new ClienteOnlineCRUD().eliminarNube(id)); // es cualquier metodo para eliminar un registro de la nube
     * }
     * </pre>
     */
    public static boolean eliminarRegistro(String id, String coleccion, Supplier<Boolean> metodoEliminarLocal, Supplier<Boolean> metodoEliminarNube) {
        boolean exito = false;
        boolean online = HelperMonitorRed.estaUsandoNube();

        try {
            if (metodoEliminarLocal.get()) {
                System.out.println("Eliminado desde local");
                exito = true;
            }
            else {
                //esto se hace porque no estamos manejando sincronizacion bidireccional
                return false;
            }
            boolean subidoExitosamente = false;
            if (online) {
                subidoExitosamente = metodoEliminarNube.get();
                if (subidoExitosamente) {
                    System.out.println("Registro eliminado correctamente en las dos bases de datos");
                }
            }
            if (!online || !subidoExitosamente) {
                String idSync = java.util.UUID.randomUUID().toString();
                new SincronizadoraDAO().agregar(new Sincronizadora(idSync, Sincronizadora.Accion.DELETE,
                        coleccion, id, "{}", "0"
                ));
                System.out.println("Registro enviado a la cola de sincronización (DELETE).");
            }
        }
        catch(Exception e) {
            System.out.println("Error eliminando los datos " + e.getMessage());
        }

        return exito;
    }


    /**
     * Metodo que se encarga de actualizar el registro especificado por argumento en la base de datos que se encuentre disponible
     * y funcionando en ese momento.
     * @param object representa el registro que se desea actualizar (Cliente, Producto...)
     * @param coleccion representa la coleccion y/o tabla de la base de datos disponible en el momento
     * @param id representa el id del registro actualizar
     * @param metodoActualizarLocal es el metodo para actualizar un registro en la base de datos local
     * @param metodoActualizarNube es el metodo para actualizar un registro en la base de datos de la nube
     * @example Ejemplo de como usar el metodo
     * <pre>{@code
     * guardarNube(object, // es el registro que se quiere actualizar
     * coleccion, // es la coleccion y/o tabla donde se va a actualizar el registro en la base de datos disponible
     * id, // es el id del registro que se quiere actualizar
     * () -> new ClienteDAO().actualizar(object), // es cualquier metodo para actualizar un registro en local
     * () -> new ClienteOnlineCRUD().actualizarNube(object)); // es cualquier metodo para actualizar un registro en la nube
     * }
     * </pre>
     */
    public static <T> boolean actualizarRegistro(T object, String coleccion, String id, Supplier<Boolean> metodoActualizarLocal, Supplier<Boolean> metodoActualizarNube){
        boolean exito = false;
        boolean online = HelperMonitorRed.estaUsandoNube();

        try{
            if(metodoActualizarLocal.get()){
                System.out.println("Actualizado desde local");
                exito = true;
            }
            else {
                return false;
            }

            boolean subidoExitosamente = false;
            if(online){
                subidoExitosamente = metodoActualizarNube.get();
                if(subidoExitosamente) {
                    System.out.println("Se actualizó desde la nube");
                }
            }

            if(!online || !subidoExitosamente){
                String registroActualizar = new Gson().toJson(object);
                String idSync = java.util.UUID.randomUUID().toString();
                new SincronizadoraDAO().agregar(new Sincronizadora(idSync, Sincronizadora.Accion.UPDATE,
                        coleccion, id, registroActualizar, "0"));
                System.out.println("Registro enviado a la cola de sincronización (UPDATE).");
            }
        }
        catch(Exception e){
            System.out.println("Error actualizando los datos " + e.getMessage());
        }
        return exito;
    }
    /**
     * Metodo encargado de guardar el registro que falto sincronizar en las dos bases de datos
     * @param object es el registro que falto sincronizar en las dos bases de datos
     */
    public static <T> void guardarRedundancia(T object){
        try(PrintWriter printWitter = new PrintWriter(
                new FileWriter("src/main/java/Metodo_Redundancia/bdRespaldo.txt", true)
        )){
            printWitter.println(new Gson().toJson(object));
        }
        catch (Exception ex){
            System.out.println("Error al guardar en el metodo de redundancia: " + ex.getMessage());
        }
    }


    /**
     * Implementacion de eliminacion cuando se quiera hacer una sincronizacion bidireccional, esto para futuras mejoras
     * @param id
     * @param coleccion
     * @param metodoEliminarLocal
     * @param metodoEliminarNube
     * @return
     */
    public static boolean eliminarRegistroBidireccional(String id, String coleccion, Supplier<Boolean> metodoEliminarLocal,
                                                        Supplier<Boolean> metodoEliminarNube) {

        boolean online = HelperMonitorRed.estaUsandoNube();
        boolean exitoLocal = false;
        boolean exitoNube = false;

        try {
            if (Conexion.conexion != null) {
                exitoLocal = metodoEliminarLocal.get();
            }
        }
        catch (Exception e) {
            System.out.println("Fallo en BD Local durante DELETE: " + e.getMessage());
        }

        if (online) {
            try {
                exitoNube = metodoEliminarNube.get();
            }
            catch (Exception e) {
                System.out.println("Fallo en BD Nube durante DELETE: " + e.getMessage());
            }
        }

        String idSync = java.util.UUID.randomUUID().toString();
        String payloadVacio = "{}";

        if (exitoLocal && exitoNube) {
            System.out.println("Registro eliminado exitosamente en AMBAS bases de datos.");
            return true;
        }

        if (!exitoLocal && !exitoNube) {
            System.out.println("Error Crítico: No se pudo eliminar en ninguna base de datos.");
            //Aquí se puede manejar el mecanismo de redundancia
            return false;
        }

        if (exitoLocal && !exitoNube) {
            try {
                Sincronizadora evento = new Sincronizadora(idSync, Sincronizadora.Accion.DELETE, coleccion, id, payloadVacio, "0");
                new SincronizadoraDAO().agregar(evento);
                System.out.println("Eliminado en Local. Comando enviado a la COLA LOCAL para Firebase.");
            } catch (Exception e) {
                System.out.println("Error guardando en cola local: " + e.getMessage());
            }
            return true;
        }

        if (!exitoLocal && exitoNube) {
            try {
                Sincronizadora evento = new Sincronizadora(idSync, Sincronizadora.Accion.DELETE, coleccion, id, payloadVacio, "0");
                new SincronizadoraOnlineCRUD().registrarNube(evento);

                System.out.println("Eliminado en Nube. Comando enviado a la COLA EN LA NUBE para MySQL.");
            } catch (Exception e) {
                System.out.println("Error guardando en cola nube: " + e.getMessage());
            }
            return true;
        }
        return false;
    }
}
