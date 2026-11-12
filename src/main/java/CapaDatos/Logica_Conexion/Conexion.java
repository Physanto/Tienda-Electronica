package CapaDatos.Logica_Conexion;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Santiago Lopez Patron Singleton
 */
public class Conexion {

    private static Firestore db;
    private static final ThreadLocal<Connection> conexionLocal = new ThreadLocal<>();
    private static Boolean usarEmbebida;

    private Conexion() {
    }

    /** Inicializa el cliente; una operación real verifica permisos y disponibilidad. */
    public static synchronized Firestore getConexionNube() {
        if (db != null)
            return db;

        try {
            if (FirebaseApp.getApps().isEmpty()) {
                System.out.println("Inicializando Firebase por primera vez...");
                Path archivo = ConfiguracionBD.credencialesFirebase();
                GoogleCredentials credenciales;
                if (archivo == null) {
                    credenciales = GoogleCredentials.getApplicationDefault();
                } else try (InputStream as = Files.newInputStream(archivo)) {
                    credenciales = GoogleCredentials.fromStream(as);
                }
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(credenciales)
                        .build();
                FirebaseApp.initializeApp(options);
            }
            db = FirestoreClient.getFirestore();
            System.out.println("Cliente Firestore inicializado; la conexión se verifica al realizar una consulta.");
            return db;
        } catch (Exception e) {
            System.out.println("Falló la conexión con la nube: " + e.getMessage());
            return null;
        }
    }

    public static Connection getConexionLocal() {
        String url = ConfiguracionBD.valor("db.url", "DB_URL", "jdbc:mysql://localhost:3306/Tienda_Electronica"
                + "?useSSL=false"
                + "&serverTimezone=America/Bogota&connectTimeout=3000&socketTimeout=10000");
        String user = ConfiguracionBD.valor("db.user", "DB_USER", "root");
        String pass = ConfiguracionBD.valor("db.password", "DB_PASSWORD", "root");

        try {
            Connection conexion = conexionLocal.get();
            if (conexion == null || conexion.isClosed() || !conexion.isValid(2)) {
                conexion = abrirLocal(url, user, pass);
                conexionLocal.set(conexion);
                System.out.println("conexion a la bd local efectiva");
            }
            return conexion;
        } catch (SQLException e) {
            cerrarConexionLocal();
            System.out.println("Error en la conexion" + e.getMessage());
            return null;
        }
    }

    /** Se fija el almacén durante la sesión para no repartir datos entre dos bases. */
    private static synchronized Connection abrirLocal(String url, String user, String pass) throws SQLException {
        String modo = ConfiguracionBD.valor("db.mode", "DB_MODE", "auto").trim().toLowerCase(java.util.Locale.ROOT);
        if (!modo.equals("auto") && !modo.equals("mysql") && !modo.equals("embedded")) {
            throw new SQLException("db.mode debe ser auto, mysql o embedded");
        }
        if (Boolean.TRUE.equals(usarEmbebida) || modo.equals("embedded")) {
            Connection conexion = BaseLocalEmbebida.abrir();
            usarEmbebida = true;
            return conexion;
        }
        try {
            Connection conexion = DriverManager.getConnection(url, user, pass);
            usarEmbebida = false;
            return conexion;
        } catch (SQLException e) {
            if (modo.equals("mysql") || Boolean.FALSE.equals(usarEmbebida)) throw e;
            System.err.println("MySQL no disponible; se utilizará la base local embebida durante esta sesión.");
            Connection conexion = BaseLocalEmbebida.abrir();
            usarEmbebida = true;
            return conexion;
        }
    }

    /**
     * metodo encargado de cerrar la conexion al cerrar la app
     */
    public static void cerrarConexionLocal() {
        Connection conexion = conexionLocal.get();
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexion: " + e.getMessage());
        } finally {
            conexionLocal.remove();
        }
    }
}
