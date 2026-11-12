package utilidades;

import CapaDatos.Logica_Conexion.Conexion;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import com.google.firebase.FirebaseApp;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/** Carga repetible de 100 clientes ficticios, sin modificar registros ajenos. */
public final class CargarDatosDemo {
    public static final int CANTIDAD = 100;
    private static final int TIMEOUT_SEGUNDOS = 30;

    private CargarDatosDemo() { }

    public static List<Map<String, Object>> clientes() {
        String[] nombres = {"Ana", "Carlos", "Lucía", "Diego", "Sofía", "Mateo", "Valentina", "Samuel", "Isabel", "Daniel"};
        String[] apellidos = {"García", "López", "Martínez", "Rodríguez", "Pérez", "Gómez", "Torres", "Díaz", "Ramírez", "Vargas"};
        List<Map<String, Object>> clientes = new ArrayList<>();
        for (int indice = 1; indice <= CANTIDAD; indice++) {
            Map<String, Object> cliente = new LinkedHashMap<>();
            String id = UUID.nameUUIDFromBytes(("tienda-electronica/demo/cliente/" + indice)
                    .getBytes(StandardCharsets.UTF_8)).toString();
            cliente.put("id", id);
            cliente.put("nombre", nombres[(indice - 1) % nombres.length]);
            cliente.put("apellido", apellidos[(indice - 1) / nombres.length]);
            cliente.put("direccion", String.format("Dirección ficticia de prueba %03d", indice));
            cliente.put("cedula", String.format("9900000%03d", indice));
            cliente.put("urlImg", null);
            clientes.add(cliente);
        }
        return clientes;
    }

    private static Map<String, Object> leerLocal(Connection local, String id) throws Exception {
        try (PreparedStatement consulta = local.prepareStatement(
                "SELECT id,nombre,apellido,direccion,cedula,urlImg FROM Cliente WHERE id = ?")) {
            consulta.setString(1, id);
            try (ResultSet filas = consulta.executeQuery()) {
                if (!filas.next()) return null;
                Map<String, Object> cliente = new LinkedHashMap<>();
                for (String campo : new String[]{"id", "nombre", "apellido", "direccion", "cedula", "urlImg"}) {
                    cliente.put(campo, filas.getString(campo));
                }
                return cliente;
            }
        }
    }

    private static void comprobarExistente(Map<String, Object> esperado, Map<String, Object> actual,
            String base) {
        if (actual != null && !esperado.equals(actual)) {
            throw new IllegalStateException("El registro " + esperado.get("id") + " ya existe con otros datos en "
                    + base + "; se conserva y se cancela la carga");
        }
    }

    public static void cargar(boolean soloLocal) throws Exception {
        Firestore nube = null;
        if (!soloLocal) {
            nube = Conexion.getConexionNube();
            if (nube == null) throw new IllegalStateException("No se pudo inicializar Firestore");
            // Una inicialización del SDK no prueba acceso real a la base de datos.
            nube.collection("Cliente").limit(1).get().get(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS);
            System.out.println("Consulta de conexión a Firestore: correcta");
        }
        Connection local = Conexion.getConexionLocal();
        if (local == null) throw new IllegalStateException("No se pudo conectar a MySQL");
        if (!local.getAutoCommit()) throw new IllegalStateException("La conexión ya tiene una transacción activa");

        List<Map<String, Object>> datos = clientes();
        Map<String, Map<String, Object>> encontradosNube = new LinkedHashMap<>();
        if (nube != null) {
            DocumentReference[] referencias = datos.stream()
                    .map(cliente -> Conexion.getConexionNube().collection("Cliente").document((String) cliente.get("id")))
                    .toArray(DocumentReference[]::new);
            for (DocumentSnapshot documento : nube.getAll(referencias).get(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)) {
                if (documento.exists()) encontradosNube.put(documento.getId(), documento.getData());
            }
        }
        int existentesLocal = 0;
        List<Map<String, Object>> insertarLocal = new ArrayList<>();
        for (Map<String, Object> esperado : datos) {
            String id = (String) esperado.get("id");
            Map<String, Object> actual = leerLocal(local, id);
            comprobarExistente(esperado, actual, "MySQL");
            comprobarExistente(esperado, encontradosNube.get(id), "Firestore");
            if (actual == null) insertarLocal.add(esperado);
            else existentesLocal++;
        }
        System.out.println("Antes: clientes demo en MySQL=" + existentesLocal
                + (soloLocal ? "; Firestore no consultado" : "; Firestore=" + encontradosNube.size()));

        local.setAutoCommit(false);
        try {
            try (PreparedStatement insertar = local.prepareStatement(
                    "INSERT INTO Cliente (id,nombre,apellido,direccion,cedula,urlImg) VALUES (?,?,?,?,?,?)")) {
                for (Map<String, Object> cliente : insertarLocal) {
                    int parametro = 1;
                    for (String campo : new String[]{"id", "nombre", "apellido", "direccion", "cedula", "urlImg"}) {
                        insertar.setString(parametro++, (String) cliente.get(campo));
                    }
                    insertar.addBatch();
                }
                if (!insertarLocal.isEmpty()) insertar.executeBatch();
            }
            if (nube != null && encontradosNube.size() < CANTIDAD) {
                WriteBatch lote = nube.batch();
                for (Map<String, Object> cliente : datos) {
                    String id = (String) cliente.get("id");
                    if (!encontradosNube.containsKey(id)) lote.create(nube.collection("Cliente").document(id), cliente);
                }
                lote.commit().get(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS);
            }
            local.commit();
        } catch (Exception error) {
            local.rollback();
            throw error;
        } finally {
            local.setAutoCommit(true);
        }
        verificar(local, nube, datos);
    }

    /** Comprobación de solo lectura: no crea ni cambia filas o documentos. */
    public static void verificar(boolean soloLocal) throws Exception {
        Connection local = Conexion.getConexionLocal();
        if (local == null) throw new IllegalStateException("No se pudo conectar a la base local");
        Firestore nube = soloLocal ? null : Conexion.getConexionNube();
        if (!soloLocal && nube == null) throw new IllegalStateException("No se pudo inicializar Firestore");
        verificar(local, nube, clientes());
    }

    private static void verificar(Connection local, Firestore nube, List<Map<String, Object>> datos) throws Exception {
        Map<String, Map<String, Object>> leidosNube = new LinkedHashMap<>();
        if (nube != null) {
            DocumentReference[] referencias = datos.stream()
                    .map(cliente -> nube.collection("Cliente").document((String) cliente.get("id")))
                    .toArray(DocumentReference[]::new);
            for (DocumentSnapshot documento : nube.getAll(referencias).get(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)) {
                if (documento.exists()) leidosNube.put(documento.getId(), documento.getData());
            }
        }
        for (Map<String, Object> esperado : datos) {
            String id = (String) esperado.get("id");
            if (!esperado.equals(leerLocal(local, id))) throw new IllegalStateException("Verificación MySQL fallida: " + id);
            if (nube != null && !esperado.equals(leidosNube.get(id))) {
                throw new IllegalStateException("Verificación Firestore fallida: " + id);
            }
        }
        System.out.println("Después: 100/100 clientes demo verificados en MySQL"
                + (nube == null ? "; Firestore pendiente" : " y Firestore; IDs y los seis campos idénticos"));
    }

    public static void main(String[] args) {
        boolean soloLocal = false;
        boolean soloVerificar = false;
        for (String argumento : args) {
            if ("--solo-local".equals(argumento)) soloLocal = true;
            else if ("--verificar".equals(argumento)) soloVerificar = true;
            else throw new IllegalArgumentException("Uso: CargarDatosDemo [--solo-local] [--verificar]");
        }
        int resultado = 0;
        try {
            if (soloVerificar) verificar(soloLocal);
            else cargar(soloLocal);
        } catch (Exception error) {
            resultado = 1;
            Throwable causa = error;
            while (causa.getCause() != null) causa = causa.getCause();
            System.err.println("Carga incompleta: " + causa.getClass().getSimpleName() + ": " + causa.getMessage());
        } finally {
            Conexion.cerrarConexionLocal();
            for (FirebaseApp app : FirebaseApp.getApps()) app.delete();
        }
        if (resultado != 0) System.exit(resultado);
    }
}
