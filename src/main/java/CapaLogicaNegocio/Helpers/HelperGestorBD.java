package CapaLogicaNegocio.Helpers;

import CapaLogicaNegocio.Logica_Negocio.Sincronizadora;
import CapaLogicaNegocio.Logica_Negocio.Cliente;
import CapaDatos.Logica_Conexion.SincronizadoraDAO;
import CapaDatos.Logica_Conexion.Conexion;
import CapaDatos.Logica_Conexion.SincronizadoraOnlineCRUD;
import CapaDatos.Logica_Conexion.ClienteOnlineCRUD;
import CapaDatos.Logica_Conexion.ClienteDAO;
import com.google.gson.Gson;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
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

    /**
     * Metodo que se encarga de guardar el registro especificado en la base de datos disponible (local, nube o ambas).
     * Si solo se pudo guardar en una de las dos, deja el pendiente encolado en la cola de sincronizacion correspondiente.
     * @param object representa el registro que se desea guardar en la base de datos (Cliente, Producto...)
     * @param coleccion representa la coleccion y/o tabla de la base de datos
     * @param id es el id que va tener el registro en la coleccion y/o tabla
     * @param metodoGuardarLocal es el metodo para guardar un registro en la base de datos local
     * @param metodoGuardarNube es el metodo para guardar un registro en la base de datos de la nube
     * @example Ejemplo de como usar el metodo
     * <pre>{@code
     * guardarRegistro(object, // es el registro que se quiere almacenar
     * coleccion, // es la coleccion y/o tabla donde se va a almacenar el registro
     * id, // es el id del registro dentro de la coleccion
     * () -> new ClienteDAO().agregar(cliente), // metodo para almacenar un registro en local
     * () -> new ClienteOnlineCRUD().registrarNube(cliente)); // metodo para almacenar un registro en la nube
     * }
     * </pre>
     */
    //tratando de realizar una implementacion generica para guardar cualquier tipo de registro
    // este metodo se puede refactorizar haciendo submetodos para tareas especificas ya que aqui hay code smell
    public static <T> boolean guardarRegistro(T object, String coleccion, String id, Supplier<Boolean> metodoGuardarLocal, Supplier<Boolean> metodoGuardarNube){
        return ejecutarEscritura(object, coleccion, id, Sincronizadora.Accion.INSERT, metodoGuardarLocal, metodoGuardarNube);
    }

    /**
     * Metodo que se encarga de actualizar el registro especificado en la base de datos disponible (local, nube o ambas).
     * Si solo se pudo actualizar en una de las dos, deja el pendiente encolado en la cola de sincronizacion correspondiente.
     * @param object representa el registro que se desea actualizar (Cliente, Producto...)
     * @param coleccion representa la coleccion y/o tabla de la base de datos
     * @param id representa el id del registro a actualizar
     * @param metodoActualizarLocal es el metodo para actualizar un registro en la base de datos local
     * @param metodoActualizarNube es el metodo para actualizar un registro en la base de datos de la nube
     * @example Ejemplo de como usar el metodo
     * <pre>{@code
     * actualizarRegistro(object, // es el registro que se quiere actualizar
     * coleccion, // es la coleccion y/o tabla donde se va a actualizar el registro
     * id, // es el id del registro que se quiere actualizar
     * () -> new ClienteDAO().actualizar(object), // metodo para actualizar un registro en local
     * () -> new ClienteOnlineCRUD().actualizarNube(object)); // metodo para actualizar un registro en la nube
     * }
     * </pre>
     */
    public static <T> boolean actualizarRegistro(T object, String coleccion, String id, Supplier<Boolean> metodoActualizarLocal, Supplier<Boolean> metodoActualizarNube){
        return ejecutarEscritura(object, coleccion, id, Sincronizadora.Accion.UPDATE, metodoActualizarLocal, metodoActualizarNube);
    }

    /**
     * Metodo que se encarga de eliminar el registro especificado por id de la base de datos disponible (local, nube o ambas).
     * Si solo se pudo eliminar en una de las dos, deja el pendiente encolado en la cola de sincronizacion correspondiente.
     * @param id es el id del registro a eliminar
     * @param coleccion representa la coleccion y/o tabla de la base de datos
     * @param metodoEliminarLocal es el metodo para eliminar un registro de la base de datos local
     * @param metodoEliminarNube es el metodo para eliminar un registro de la base de datos de la nube
     * @example Ejemplo de como usar el metodo
     * <pre>{@code
     * eliminarRegistro(id, // es el id del registro que se quiere eliminar
     * coleccion, // es la coleccion y/o tabla de la base de datos
     * () -> new ClienteDAO().eliminar(id), // metodo para eliminar un registro de local
     * () -> new ClienteOnlineCRUD().eliminarNube(id)); // metodo para eliminar un registro de la nube
     * }
     * </pre>
     */
    public static boolean eliminarRegistro(String id, String coleccion, Supplier<Boolean> metodoEliminarLocal, Supplier<Boolean> metodoEliminarNube) {
        return ejecutarEscritura(null, coleccion, id, Sincronizadora.Accion.DELETE, metodoEliminarLocal, metodoEliminarNube);
    }

    /**
     * Nucleo bidireccional para las operaciones de escritura (INSERT, UPDATE, DELETE). Intenta aplicar la operacion en
     * cada base de datos que este disponible y delega en {@link #reconciliarEscritura} para decidir si hay que encolar
     * algun pendiente.
     */
    private static <T> boolean ejecutarEscritura(T object, String coleccion, String id, Sincronizadora.Accion accion,
                                                 Supplier<Boolean> metodoLocal, Supplier<Boolean> metodoNube) {
        boolean online = HelperMonitorRed.estaUsandoNube();
        boolean hayLocal = localDisponible();
        boolean exitoLocal = false;
        boolean exitoNube = false;

        if (hayLocal) {
            try {
                exitoLocal = Boolean.TRUE.equals(metodoLocal.get());
            }
            catch (Exception e) {
                System.err.println("Fallo " + accion + " en la base local. Causa: " + e.getMessage());
            }
        }

        if (online) {
            try {
                exitoNube = Boolean.TRUE.equals(metodoNube.get());
            }
            catch (Exception e) {
                System.err.println("Fallo " + accion + " en la nube. Causa: " + e.getMessage());
            }
        }

        return reconciliarEscritura(object, coleccion, id, accion, exitoLocal, exitoNube);
    }

    /**
     * Decide, segun en cuales bases de datos se aplico la operacion, si hay que dejar un pendiente en alguna cola de
     * sincronizacion o respaldar en redundancia cuando ninguna base estuvo disponible.
     */
    private static <T> boolean reconciliarEscritura(T object, String coleccion, String id, Sincronizadora.Accion accion,
                                                    boolean exitoLocal, boolean exitoNube) {
        if (exitoLocal && exitoNube) {
            System.out.println("Operacion " + accion + " replicada en Local y Nube.");
            return true;
        }
        if (exitoLocal) {
            encolarLocal(object, coleccion, id, accion);
            return true;
        }
        if (exitoNube) {
            encolarNube(object, coleccion, id, accion);
            return true;
        }

        System.out.println("Operacion " + accion + " no se pudo aplicar en ninguna base de datos.");
        if (object != null) {
            guardarRedundancia(object);
        }
        return false;
    }

    /**
     * Encola el pendiente en la cola LOCAL (tabla ColaSincronizadora) para subirlo a la nube cuando regrese la conexion.
     */
    private static <T> void encolarLocal(T object, String coleccion, String id, Sincronizadora.Accion accion) {
        try {
            String registroJson = (accion == Sincronizadora.Accion.DELETE) ? "{}" : new Gson().toJson(object);
            new SincronizadoraDAO().agregar(new Sincronizadora(UUID.randomUUID().toString(), accion, coleccion, id, registroJson, "0"));
            System.out.println("Pendiente encolado en la cola LOCAL para subir a la nube (" + accion + ").");
        }
        catch (Exception e) {
            System.err.println("Fallo al encolar en la cola local. Causa: " + e.getMessage());
        }
    }

    /**
     * Encola el pendiente en la cola de la NUBE (coleccion Sincronizadora) para bajarlo a local cuando regrese la base local.
     */
    private static <T> void encolarNube(T object, String coleccion, String id, Sincronizadora.Accion accion) {
        try {
            String registroJson = (accion == Sincronizadora.Accion.DELETE) ? "{}" : new Gson().toJson(object);
            new SincronizadoraOnlineCRUD().registrarNube(new Sincronizadora(UUID.randomUUID().toString(), accion, coleccion, id, registroJson, "0"));
            System.out.println("Pendiente encolado en la cola NUBE para bajar a local (" + accion + ").");
        }
        catch (Exception e) {
            System.err.println("Fallo al encolar en la cola nube. Causa: " + e.getMessage());
        }
    }

    /**
     * Metodo que se encarga de cargar todos los registros de la base de datos disponible en ese momento. Prioriza la
     * base local; si esta no esta disponible o no devuelve resultados y hay nube, consulta la nube.
     * @param metodoCargarLocal es el metodo para cargar todos los registros de la base de datos local
     * @param metodoCargarNube es el metodo para cargar todos los registros de la base de datos de la nube
     * @example Ejemplo de como usar el metodo
     * <pre>{@code
     * cargarRegistros(() -> new ClienteDAO().obteners(), // metodo para cargar registros de la bd local
     * () -> new ClienteOnlineCRUD().obtenersNube(Cliente.class)); // metodo para cargar registros de la bd nube
     * }
     * </pre>
     */
    public static <T> ArrayList<T> cargarRegistros(Supplier<ArrayList<T>> metodoCargarLocal, Supplier<ArrayList<T>> metodoCargarNube){
        boolean online = HelperMonitorRed.estaUsandoNube();
        boolean hayLocal = localDisponible();
        ArrayList<T> listaRegistros = new ArrayList<>();

        try{
            if (hayLocal) {
                listaRegistros = metodoCargarLocal.get();
                if (listaRegistros != null && !listaRegistros.isEmpty()) {
                    System.out.println("Listando desde local");
                    return listaRegistros;
                }
            }
            if (online) {
                System.out.println("Listando desde la nube");
                listaRegistros = metodoCargarNube.get();
            }
        }
        catch(Exception e){
            System.out.println("Error fatal listando los datos " + e.getMessage());
        }
        return listaRegistros != null ? listaRegistros : new ArrayList<>();
    }

    /**
     * Metodo que se encarga de cargar el registro con id especificado de la base de datos disponible en ese momento.
     * Prioriza la base local; si esta no esta disponible o no encuentra el registro y hay nube, consulta la nube.
     * @param metodoCargarLocal es el metodo para cargar el registro de la base de datos local
     * @param metodoCargarNube es el metodo para cargar el registro de la base de datos de la nube
     * @example Ejemplo de como usar el metodo
     * <pre>{@code
     * cargarRegistro(() -> new ClienteDAO().obtener(id), // metodo para cargar un registro de la bd local
     * () -> new ClienteOnlineCRUD().obtenerNube(Cliente.class, id)); // metodo para cargar un registro de la bd nube
     * }
     * </pre>
     * @return el objeto con el id asignado
     */
    public static <T> T cargarRegistro(Supplier<T> metodoCargarLocal, Supplier<T> metodoCargarNube){
        boolean online = HelperMonitorRed.estaUsandoNube();
        boolean hayLocal = localDisponible();
        T object = null;

        try{
            if (hayLocal) {
                object = metodoCargarLocal.get();
                if (object != null) {
                    System.out.println("Listando desde local");
                    return object;
                }
            }
            if (online) {
                System.out.println("Listando desde la nube");
                object = metodoCargarNube.get();
            }
        }
        catch(Exception e){
            System.out.println("Error fatal listando los datos " + e.getMessage());
        }
        return object;
    }

    /**
     * Metodo encargado de guardar el registro que no se pudo aplicar en ninguna de las dos bases de datos
     * @param object es el registro que no se pudo sincronizar en ninguna base de datos
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
     * Verifica si la base de datos local esta disponible. Abre una conexion de sondeo y la cierra de inmediato para no
     * dejar conexiones colgadas.
     * @return true si se pudo establecer conexion con la base local, de lo contrario false
     */
    private static boolean localDisponible() {
        try {
            Connection c = Conexion.getConexionLocal();
            return c != null && c.isValid(2);   // 2 s de timeout; NO la cierra
        }
        catch (SQLException e) {
            return false;
        }
    }
}
