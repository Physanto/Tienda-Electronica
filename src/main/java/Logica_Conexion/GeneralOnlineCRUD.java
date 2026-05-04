package Logica_Conexion;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import java.util.ArrayList;
import java.util.Map;

public class GeneralOnlineCRUD {

    CollectionReference reference;
    public static Firestore db;

    /**
     * Se encarga de guardar un Cliente en la base de datos de firebase
     * @param coleccion es el contenedor donde se almacenan los datos ("nombre de la tabla")
     * @param documento es el identificador del registro dentro del contenedor (identificador unico de cada registro)
     * @param registro son los datos que se van almacenar
     * @return true si guarda correctamente el registro, de lo contrario false;
     */
    public static boolean guardar(String coleccion, String documento, Map<String, Object> registro) {
        db = FirestoreClient.getFirestore();
        try {
            DocumentReference docRef = db.collection(coleccion).document(documento);
            WriteResult result = docRef.set(registro).get(); // tener cuidado: operacion asincrona sin get()
            System.out.println("Guardado Correctamente");
            return true;
        }
        catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
        return false;
    }

    public static QuerySnapshot cargarDatos(String coleccion){
        try {
            CollectionReference clienteFirestore = Conexion.db.collection(coleccion);
            return clienteFirestore.get().get(); // tener cuidado: operacion asincrona sin get()
        }
        catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
        return null;
    }

    public static <T> T obtener(String coleccion, Class<T> clase, String documento){
       QuerySnapshot datos = cargarDatos(coleccion);

       if(datos != null) {
           try {
               for (DocumentSnapshot documentSnapshot : datos.getDocuments()) {
                   if (documentSnapshot.getString("id").equals(documento)) {
                       return documentSnapshot.toObject(clase);
                   }
               }
           }
           catch (Exception e) {
               System.out.println("Error: " + e.getMessage());
           }
       }
       return null;
    }

    public static <T> ArrayList<T> obteners(String coleccion, Class<T> clase) {

        ArrayList<T> listaClientes = new ArrayList<>();
        QuerySnapshot datos = cargarDatos(coleccion);

        if(datos != null) {
            try {
                for (DocumentSnapshot document : datos.getDocuments()) {
                    T registro = document.toObject(clase);
                    listaClientes.add(registro);
                }
            }
            catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        return listaClientes;
    }

    public static <T> boolean existeRegistro(String coleccion, Class<T> clase, String documento){
       return obtener(coleccion, clase, documento) != null;
    }

    public static <T> boolean eliminar(String coleccion, String documento, Class<T> clase) {
        db = FirestoreClient.getFirestore();
        try {
            DocumentReference docref = db.collection(coleccion).document(documento);
            WriteResult result = docref.delete().get(); // tener cuidado: operacion asincrona sin get()
            System.out.println("Eliminado exitosamente");
            return true;
        }
        catch (Exception e) {
            System.out.println("Persona no encontrado");
        }
        return false;
    }

    public static boolean actualizar(String coleccion, String documento, Map<String, Object> data){
        db = FirestoreClient.getFirestore();
        try{
            DocumentReference documentReference = db.collection(coleccion) .document(documento);
            WriteResult result = documentReference.update(data).get(); // tener cuidado: operacion asincrona sin get()
            return true;
        }
        catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }
}
