package Logica_Conexion;

import Logica_Negocio.Cliente;
import Logica_Negocio.Producto;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Precondition;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.google.protobuf.Api;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;

/**
 *
 * @author Santiago Lopez
 */

public class PersonaProvider {

    CollectionReference reference;
    public static Firestore db;

    /**
     * Se encarga de guardar un Cliente en la base de datos de firebase
     * @param coleccion es el contenedor donde se almacenan los datos ("nombre de la tabla")
     * @param documento es el identificador del registro dentro del contenedor (identificador unico de cada registro)
     * @param data son los datos que se van almacenar
     * @return true si guarda correctamente el registro, de lo contrario false;
     */
    public static boolean guardarCliente(String coleccion, String documento, Map<String, Object> data) {
        db = FirestoreClient.getFirestore();
        try {
            DocumentReference docRef = db.collection(coleccion).document(documento);
            ApiFuture<WriteResult> result = docRef.set(data);
            System.out.println("Guardado Correctamente");
            return true;
        }
        catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
        return false;
    }

    /**
     * Carga la informacion completa del cliente desde la base de datos en la nube firebase
     * @return una lista con todos los registros existentes en la base de datos.
     */
    public static ArrayList<Cliente> cargarInfoCliente() {

        Cliente cliente = null;

        ArrayList<Cliente> listaClientes = new ArrayList<>();

        try {
            CollectionReference clienteFirestore = Conexion.db.collection("Cliente");
            ApiFuture<QuerySnapshot> querySnap = clienteFirestore.get();

            for (DocumentSnapshot document : querySnap.get().getDocuments()) {

                cliente = new Cliente(Integer.parseInt(Objects.requireNonNull(document.getString("id"))),
                        document.getString("nombre"),
                        document.getString("apellido"),
                        document.getString("cedula"),
                        document.getString("direccion"),
                        document.getString("nom_img")
                );
                listaClientes.add(cliente);
            }
        }
        catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
        return listaClientes;
    }
    public static ApiFuture<QuerySnapshot> cargarTodos(String coleccion){
        ApiFuture<QuerySnapshot> querySnap = null;
        try {
            CollectionReference clienteFirestore = Conexion.db.collection(coleccion);
            return querySnap = clienteFirestore.get();
        }
        catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
        return querySnap;
    }

    public static ArrayList<Cliente> c (String coleccion) {

        ApiFuture<QuerySnapshot> datos = cargarTodos(coleccion);
        Cliente cliente = null;
        ArrayList<Cliente> listaClientes = new ArrayList<>();

        try{
            for(DocumentSnapshot document : datos.get().getDocuments()) {

                cliente = new Cliente(Integer.parseInt(Objects.requireNonNull(document.getString("id"))),
                        document.getString("nombre"),
                        document.getString("apellido"),
                        document.getString("cedula"),
                        document.getString("direccion"),
                        document.getString("nom_img")
                );
                listaClientes.add(cliente);
            }
        }

        return listaClientes;
    }

    /**
     * Metodo encargado de buscar en la base de datos de la nube por medio del id
     * y devolver el registro asociado
     * @param id es el id del registro que se quiere encontrar y mostrar
     * @return el registro completo, de lo contrario null
     */
    public static Cliente cargarInfoClienteCodigo(String id) {
        try {
            CollectionReference persona = Conexion.db.collection("Cliente");
            ApiFuture<QuerySnapshot> querySnap = persona.get(); // tener cuidado: operacion asincrona

            for (DocumentSnapshot document : querySnap.get().getDocuments()) {

                if(document.getString("id").equals(id)){

                    return new Cliente(Integer.parseInt(Objects.requireNonNull(document.getString("id"))),
                            document.getString("nombre"),
                            document.getString("apellido"),
                            document.getString("cedula"),
                            document.getString("direccion"),
                            document.getString("nom_img")
                    );
                }
            }
        }
        catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
        return null;
    }

    /**
     * metodo encargado de verificar si en la base de datos en la nube existe el registro con el id
     * pasado por argumento
     * Este metodo hace uso de cargarInfoClienteCodigo para hacer la busqueda
     * @param id registro a buscar en la base de datos
     * @return true si existe el registro, de lo contrario false
     */
    public static boolean retornarId(String id) {
        return cargarInfoClienteCodigo(id) != null;
    }

    public static boolean eliminarCliente(String coleccion, String documento) {
        db = FirestoreClient.getFirestore();
        boolean res = RetornarUid(documento);
        System.out.println("Respuesta" + res);

        try {
            if (res != false) {
                DocumentReference docref = db.collection(coleccion).document(documento);
                ApiFuture<WriteResult> result = docref.delete();  // tener cuidado: operacion asincrona
                System.out.println("Eliminado exitosamente");
                return true;
            }

        }
        catch (Exception e) {
            System.out.println("Persona no encontrado");
        }
        return false;
    }
    public static boolean actualizar(String coleccion, String documento, Map<String, Object> data){
       db = FirestoreClient.getFirestore();

       try{
          DocumentReference documentReference = db.collection("Cliente") .document("d");
          ApiFuture<WriteResult> resultApiFuture = documentReference.update(data);
       }
    }

}
