package Logica_Conexion;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import java.util.ArrayList;
import java.util.Map;


/* LEER ESTO ANTES DE ANALIZAR EL CODIGO CON EL FIN DE PODER ENTENDERLO MEJOR:

- En firebase se le llama coleccion al contenedor donde estan almacenado los registros de ese tipo,
es como las tablas en SQL. coleccion = tabla (bd relacionales)

- En firebase documento es el identificador del registro dentro del contenedor
en terminos mas simples y haciendo analogia a bd relaciones, es la llave primaria de la tabla

- El parametro *clase* es precisamente la clase que nosotros le queremos pasar Ejm: (Producto, Cliente...), este parametro se pide
porque despues de que firebase me retorne el registro que yo le pedi entonces lo debo almacenar en el objeto correspondiente (Producto, Cliente...)
y esto lo hago con el fin de que toda esta clase sea generica, ayuda a que no se repita codigo y sea mas pequena la implementacion.
Sino se hace esto se deberia hacer basicamente un CRUD para cada objeto, con esta sola clase ya hacemos todos los crud.
 */

/**
 * Clase encargada de manejar el CRUD a la base de datos en la nube (Firebase)
 * Esta hace implementacion de metodos genericos propios para un eficiente manejo de recursos y
 * disminuir la complejidad del manejo de varias clases
 *
 * @author Manuel Figueroa (Physanto)
 */
public class GeneralOnlineProviderCRUD {

    CollectionReference reference;
    public static Firestore db;

    /**
     * Metodo generico que se encarga de guardar un registro en el contenedor especificado por argumento en la base de datos de firebase
     * @param coleccion es el contenedor donde se almacenan los datos ("la tabla")
     * @param documento es el identificador unico que va ser asignado por cada registro dentro del contenedor (identificador unico de cada registro)
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

    /**
     * Metodo generico que carga los datos de la coleccion especificada por argumento en Firebase
     * @param coleccion es el contenedor de donde se van a traer los registros
     * @return los datos cargados previamente si estos existen, de lo contrario retorna null
     */
    private static QuerySnapshot cargarDatos(String coleccion){
        try {
            CollectionReference clienteFirestore = Conexion.db.collection(coleccion);
            return clienteFirestore.get().get(); // tener cuidado: operacion asincrona sin get()
        }
        catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
        return null;
    }

    /**
     * Metodo generico que se encarga de buscar en Firebase dentro de la coleccion el registro especificado por argumento,
     * al igual que la coleccion especificada
     * @param coleccion es el contenedor donde se almacenan los registros
     * @param documento es el identificador del registro especifico que se desea buscar
     * @param clase es el tipo de objeto donde se desea guardar el registro encontrado.
     * @return el registro encontrado, de lo contrario null
     */
    public static <T> T obtener(String coleccion, String documento, Class<T> clase){
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

    /**
     * Metodo generico que busca en Firebase todos los registros que contiene el documento pasado por argumento
     * @param coleccion es el contenedor donde se almacenan los registros
     * @param clase es el tipo de objeto donde se desea guardar el registro encontrado.
     * @return una lista del tipo clase con todos los registros hallados, de lo contrario una lista vacia.
     * @param <T> es generico, asi que puede ser de cualquier tipo
     */
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

    /**
     * Hace uso del metodo *obtener* para verificar si existe o no un registro especificado mediante el documento
     * @param coleccion es el contenedor donde se almacenan los registros
     * @param clase es el tipo de objeto donde se desea guardar el registro encontrado.
     * @param documento es el identifiador del registro especifico que se desea buscar
     * @return true si el registro existe, de lo contrario false
     * @param <T> es generico, asi que puede ser de cualquier tipo
     */
    public static <T> boolean existeRegistro(String coleccion, Class<T> clase, String documento){
       return obtener(coleccion, documento, clase) != null;
    }

    /**
     * Metodo generico que elimina de Firebase un registro dentro de la coleccion especificada por argumento
     * @param coleccion es el contenedor donde se almacenan los registros
     * @param clase es el tipo de objeto donde se desea guardar el registro encontrado.
     * @param documento es el identificador del registro especifico que se desea buscar
     * @return true si el registro fue eliminado correctamente, de lo contrario false
     * @param <T> es generico, asi que puede ser de cualquier tipo
     */
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

    /**
     * Metodo generico que se encarga de actualizar un registro en el contenedor especificado por argumento en la base de datos de firebase
     * @param coleccion es el contenedor donde se almacenan los datos ("la tabla")
     * @param documento es el identificador unico que va ser asignado por cada registro dentro del contenedor (identificador unico de cada registro)
     * @param registro son los datos que se van almacenar
     * @return true si actualiza correctamente el registro, de lo contrario false;
     */
    public static boolean actualizar(String coleccion, String documento, Map<String, Object> registro){
        db = FirestoreClient.getFirestore();
        try{
            DocumentReference documentReference = db.collection(coleccion) .document(documento);
            WriteResult result = documentReference.update(registro).get(); // tener cuidado: operacion asincrona sin get()
            return true;
        }
        catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }
}
