package CapaDatos.Logica_Conexion;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream;
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

    private Conexion() {
    }

    /**
     * Intenta establecer la conexión con Firebase.
     * Empuja la excepción IOException hacia arriba si el archivo de credenciales
     * falla.
     */
    public static Firestore getConexionNube() {
        if (db != null)
            return db;

        try {
            if (FirebaseApp.getApps().isEmpty()) {
                System.out.println("Inicializando Firebase por primera vez...");
                try (FileInputStream as = new FileInputStream("tienda-electronica-v2.json")) {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(as))
                            .build();
                    FirebaseApp.initializeApp(options);
                }
            }
            db = FirestoreClient.getFirestore();
            System.out.println("Conexión Exitosa a Firestore.");
            return db;
        } catch (Exception e) {
            System.out.println("Falló la conexión con la nube: " + e.getMessage());
            return null;
        }
    }

    public static Connection getConexionLocal() {
        String url = "jdbc:mysql://localhost:3306/Tienda_Electronica"
                + "?useSSL=false"
                + "&serverTimezone=America/Bogota";
        String user = "init";
        String pass = "root";

        try {
            Connection conexion = conexionLocal.get();
            if (conexion == null || conexion.isClosed() || !conexion.isValid(2)) {
                conexion = DriverManager.getConnection(url, user, pass);
                conexionLocal.set(conexion);
                System.out.println("conexion a la bd local efectiva");
            }
            return conexion;
        } catch (SQLException e) {
            System.out.println("Error en la conexion" + e.getMessage());
            return null;
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
