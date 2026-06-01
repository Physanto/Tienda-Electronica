package CapaDatos.Logica_Conexion;

import CapaLogicaNegocio.Excepciones.ExcepcionSQL;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Santiago Lopez Patron Singleton
 */
public class Conexion {

    private static Firestore db;
    private static Connection conexion;

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
                + "&serverTimezone=UTC";
        String user = "init";
        String pass = "root";

        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(url, user, pass);
                System.out.println("conexion a la bd local efectiva");
            }
        } catch (SQLException e) {
            System.out.println("Error en la conexion" + e.getMessage());
            return null;
        }
        return conexion;
    }

    /**
     * metodo encargado de cerrar la conexion al cerrar la app
     */
    public static void cerrarConexionLocal() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexion: " + e.getMessage());
        } finally {
            conexion = null;
        }
    }
}
