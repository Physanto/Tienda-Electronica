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
import java.util.function.Supplier;

// AQUI EN ESTA CLASE SOLO SE ESTA IMPLEMENTANDO UNA SINCRONIZACION UNIDIRECCIONAL, DEJO UN EJEMPLO DE COMO IMPLEMENTAR BIRECCIONAL PARA MEJORAS FUTURAS

/**
 * Clase que se encarga de gestionar las dos bases de datos (Local y nube), lo
 * que hace es que si se quiere por ejemplo
 * guardar, eliminar, actualizar y/o mostrar, gestiona que base de datos esta
 * disponible y funcionando para el CRUD.
 * Tambien deja el registro que no esta sincronizado en una cola para su
 * posterior sincronizacion. Cabe aclarar que esta clase
 * se hizo con un enfoque generico para un uso centralizado.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class HelperGestorBD {

    // metodo especifico para guardar un cliente en la nube, se va reemplazar por el
    // generico guardarNube
    public static void GuardarPersonaGeneral(Cliente cliente) {
        guardarRegistro(cliente, "Cliente", cliente.getId(),
                () -> new ClienteDAO().agregar(cliente),
                () -> new ClienteOnlineCRUD().registrarNube(cliente));
    }

    /**
     * Metodo que se encarga de guardar el registro especificado por argumento en la
     * base de datos que se encuentra disponible y funcionando en el momento
     * este metodo es generico por ende se pide especificamente el registro, id y
     * coleccion aunque suene redundante
     *
     * @param object             representa el registro que se desea guardar en la
     *                           base de datos (Cliente, Producto...)
     * @param coleccion          representa la coleccion y/o tabla de la base de
     *                           datos
     * @param id                 es el id que va tener el registro en la coleccion
     *                           y/o tabla
     * @param metodoGuardarLocal es el metodo para guardar un registro en la base de
     *                           datos local
     * @param metodoGuardarNube  es el metodo para guardar un registro en la base de
     *                           datos de la nube
     * @example Ejemplo de como usar el metodo
     *
     *          <pre>{@code
     * guardarNube(object, // es el registro que se quiere almacenar
     * coleccion, // es la coleccion donde se va a almacenar el registro en la base de datos de la nube
     * id, // es el id del registro dentro de la coleccion
     * () -> new ClienteDAO().agregar(cliente), // es cualquier metodo para almacenar un registro en local
     * () -> new ClienteOnlineCRUD().registrarNube(cliente)); // es cualquier metodo para almacenar un registro en la nube
     * }
     * </pre>
     */
    // tratando de realizar una implementacion generica para guardar cualquier tipo
    // de registro
    // este metodo se puede refactorizar haciendo submetodos para tareas especificas
    // ya que aqui hay code smell
    public static <T> boolean guardarRegistro(T object, String coleccion, String id,
            Supplier<Boolean> metodoGuardarLocal, Supplier<Boolean> metodoGuardarNube) {
        return modificarYEncolar(object, coleccion, id, Sincronizadora.Accion.INSERT, metodoGuardarLocal);
    }

    /**
     * Metodo que se encarga de cargar todos los registros de la base de datos que
     * este disponible y en operacion en ese momento
     *
     * @param metodoCargarLocal es el metodo para cargar todos registros de la base
     *                          de datos local
     * @param metodoCargarNube  es el metodo para cargar todos registros de la base
     *                          de datos de la nube
     * @return una lista la cual contiene los registros consultados a una de la base
     *         de datos
     * @example Ejemplo de como usar el metodo
     *
     *          <pre>{@code
     * cargarRegistros(() -> new ClienteDAO().obteners(), // es cualquier metodo para cargar registros de la bd local
     * () -> new ClienteOnlineCRUD().obtenersNube(Cliente.class)); // es cualquier metodo para cargar registros de la bd nube
     * }
     * </pre>
     */
    public static <T> ArrayList<T> cargarRegistros(Supplier<ArrayList<T>> metodoCargarLocal,
            Supplier<ArrayList<T>> metodoCargarNube) {
        try {
            ArrayList<T> locales = metodoCargarLocal.get();
            if (locales != null && !locales.isEmpty()) return locales;
            // Una tabla local vacía es autoritativa: evita resucitar borrados pendientes.
            if (Conexion.getConexionLocal() != null) return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("No se pudo consultar MySQL: " + e.getMessage());
        }
        if (HelperMonitorRed.estaUsandoNube()) {
            try {
                ArrayList<T> nube = metodoCargarNube.get();
                if (nube != null) return nube;
            } catch (Exception e) {
                System.err.println("No se pudo consultar Firestore: " + e.getMessage());
            }
        }
        return new ArrayList<>();
    }

    /**
     * Metodo que se encarga de cargar el registro con id especificado por argumento
     * de la base de datos que este disponible y en operacion en ese momento
     *
     * @param metodoCargarLocal es el metodo para cargar el registro de la base de
     *                          datos local
     * @param metodoCargarNube  es el metodo para cargar el registro de la base de
     *                          datos de la nube
     * @example Ejemplo de como usar el metodo
     *
     *          <pre>{@code
     * cargarRegistro(() -> new ClienteDAO().obtener(id), // es cualquier metodo para cargar un registro de la bd local
     * () -> new ClienteOnlineCRUD().obtenerNube(Cliente.class, id)); // es cualquier metodo para cargar un registro de la bd nube
     * }
     * </pre>
     *
     * @return el objeto con el id asignado
     */
    public static <T> T cargarRegistro(Supplier<T> metodoCargarLocal, Supplier<T> metodoCargarNube) {
        try {
            T local = metodoCargarLocal.get();
            if (local != null || Conexion.getConexionLocal() != null) return local;
        } catch (Exception e) {
            System.err.println("No se pudo consultar MySQL: " + e.getMessage());
        }
        if (HelperMonitorRed.estaUsandoNube()) {
            try {
                return metodoCargarNube.get();
            } catch (Exception e) {
                System.err.println("No se pudo consultar Firestore: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * Metodo que se encarga de eliminar el registro especificado por id de la base
     * de datos que se encuentra disponible y funcionando en el momento
     *
     * @param id                  es el id del registro a eliminar
     * @param coleccion           representa la coleccion y/o tabla de la base de
     *                            datos
     * @param metodoEliminarLocal es el metodo para eliminar un registro de la base
     *                            de datos local
     * @param metodoEliminarNube  es el metodo para eliminar un registro de la base
     *                            de datos de la nube
     * @example Ejemplo de como usar el metodo
     *
     *          <pre>{@code
     * eliminarRegistro(id, // es el id del registro que se quiere eliminar
     * coleccion, // es la coleccion y/o tabla de la base de datos
     * () -> new ClienteDAO().eliminar(id), // es cualquier metodo para eliminar un registro de local
     * () -> new ClienteOnlineCRUD().eliminarNube(id)); // es cualquier metodo para eliminar un registro de la nube
     * }
     * </pre>
     */
    public static boolean eliminarRegistro(String id, String coleccion, Supplier<Boolean> metodoEliminarLocal,
            Supplier<Boolean> metodoEliminarNube) {
        return modificarYEncolar(null, coleccion, id, Sincronizadora.Accion.DELETE, metodoEliminarLocal);
    }

    /**
     * Metodo que se encarga de actualizar el registro especificado por argumento en
     * la base de datos que se encuentre disponible
     * y funcionando en ese momento.
     *
     * @param object                representa el registro que se desea actualizar
     *                              (Cliente, Producto...)
     * @param coleccion             representa la coleccion y/o tabla de la base de
     *                              datos disponible en el momento
     * @param id                    representa el id del registro actualizar
     * @param metodoActualizarLocal es el metodo para actualizar un registro en la
     *                              base de datos local
     * @param metodoActualizarNube  es el metodo para actualizar un registro en la
     *                              base de datos de la nube
     * @example Ejemplo de como usar el metodo
     *
     *          <pre>{@code
     * guardarNube(object, // es el registro que se quiere actualizar
     * coleccion, // es la coleccion y/o tabla donde se va a actualizar el registro en la base de datos disponible
     * id, // es el id del registro que se quiere actualizar
     * () -> new ClienteDAO().actualizar(object), // es cualquier metodo para actualizar un registro en local
     * () -> new ClienteOnlineCRUD().actualizarNube(object)); // es cualquier metodo para actualizar un registro en la nube
     * }
     * </pre>
     */
    public static <T> boolean actualizarRegistro(T object, String coleccion, String id,
            Supplier<Boolean> metodoActualizarLocal, Supplier<Boolean> metodoActualizarNube) {
        return modificarYEncolar(object, coleccion, id, Sincronizadora.Accion.UPDATE, metodoActualizarLocal);
    }

    /** El dato y su evento se confirman juntos; nunca se informa éxito sin respaldo. */
    private static <T> boolean modificarYEncolar(T object, String coleccion, String id,
            Sincronizadora.Accion accion, Supplier<Boolean> modificacion) {
        return guardarOperacion(modificacion, new CambioPendiente(object, coleccion, id, accion));
    }

    public record CambioPendiente(Object registro, String coleccion, String id, Sincronizadora.Accion accion) { }

    /** Confirma varias escrituras relacionadas y todos sus eventos en una transacción. */
    public static boolean guardarOperacion(Supplier<Boolean> modificacion, CambioPendiente... cambios) {
        synchronized (HelperSincronizador.class) {
            Connection conexion = Conexion.getConexionLocal();
            if (conexion == null || cambios.length == 0) return false;
            try {
                if (!conexion.getAutoCommit()) return false;
            } catch (SQLException e) {
                return false;
            }
            try {
                conexion.setAutoCommit(false);
                if (!Boolean.TRUE.equals(modificacion.get())) {
                    conexion.rollback();
                    return false;
                }
                for (CambioPendiente cambio : cambios) {
                    String json = cambio.registro() == null ? "{}" : HelperSincronizador.gsonSync().toJson(cambio.registro());
                    Sincronizadora evento = new Sincronizadora(java.util.UUID.randomUUID().toString(),
                            cambio.accion(), cambio.coleccion(), cambio.id(), json, "0");
                    if (!new SincronizadoraDAO().agregar(evento)) {
                        throw new SQLException("No se pudo guardar la cola de sincronización");
                    }
                }
                conexion.commit();
            } catch (Exception e) {
                try { conexion.rollback(); } catch (SQLException error) { e.addSuppressed(error); }
                System.err.println("No se guardó el cambio local: " + e.getMessage());
                return false;
            } finally {
                try { conexion.setAutoCommit(true); } catch (SQLException e) { Conexion.cerrarConexionLocal(); }
            }
            if (HelperMonitorRed.estaUsandoNube()) HelperSincronizador.SincronizarLocalANube();
            return true;
        }
    }

    /**
     * Metodo encargado de guardar el registro que falto sincronizar en las dos
     * bases de datos
     *
     * @param object es el registro que falto sincronizar en las dos bases de datos
     */
    public static <T> void guardarRedundancia(T object) {
        try (PrintWriter printWitter = new PrintWriter(
                new FileWriter("src/main/java/Metodo_Redundancia/bdRespaldo.txt", true))) {
            printWitter.println(new Gson().toJson(object));
        } catch (Exception ex) {
            System.out.println("Error al guardar en el metodo de redundancia: " + ex.getMessage());
        }
    }

    /**
     * Implementacion de eliminacion cuando se quiera hacer una sincronizacion
     * bidireccional, esto para futuras mejoras
     *
     * @param id
     * @param coleccion
     * @param metodoEliminarLocal
     * @param metodoEliminarNube
     * @return
     */
    public static boolean eliminarRegistroBidireccional(String id, String coleccion,
            Supplier<Boolean> metodoEliminarLocal,
            Supplier<Boolean> metodoEliminarNube) {
        boolean online = HelperMonitorRed.estaUsandoNube();
        boolean exitoLocal = false;
        boolean exitoNube = false;

        try {
            if (Conexion.getConexionLocal() != null) {
                exitoLocal = metodoEliminarLocal.get();
            }
        } catch (Exception e) {
            System.out.println("Fallo en BD Local durante DELETE: " + e.getMessage());
        }

        if (online) {
            try {
                exitoNube = metodoEliminarNube.get();
            } catch (Exception e) {
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
            // Aquí se puede manejar el mecanismo de redundancia
            return false;
        }

        if (exitoLocal && !exitoNube) {
            try {
                Sincronizadora evento = new Sincronizadora(idSync, Sincronizadora.Accion.DELETE, coleccion, id,
                        payloadVacio, "0");
                new SincronizadoraDAO().agregar(evento);
                System.out.println("Eliminado en Local. Comando enviado a la COLA LOCAL para Firebase.");
            } catch (Exception e) {
                System.out.println("Error guardando en cola local: " + e.getMessage());
            }
            return true;
        }

        if (!exitoLocal && exitoNube) {
            try {
                Sincronizadora evento = new Sincronizadora(idSync, Sincronizadora.Accion.DELETE, coleccion, id,
                        payloadVacio, "0");
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
