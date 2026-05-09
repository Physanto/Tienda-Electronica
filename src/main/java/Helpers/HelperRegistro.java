package Helpers;

import Logica_Conexion.GeneralOnlineCRUD;
import Logica_Negocio.Cliente;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Santiago Lopez Patron Adapter
 */


/**
 * Clase encargada de Registrar las personas a la base de datos en la nube
 */
public class HelperRegistro implements IHelperRegistro {

    /**
     * Metodo que registra a una persona en la base de datos de la nube
     *
     * @param object es el registro que se quiere guardar
     */
//    @Override
//    public <T> void registrarNube(Class<T> clase) {
//        Field[] metodos = clase.getDeclaredFields();
//
//        if (!GeneralOnlineCRUD.existeRegistro(clase.getName(), clase, clase.getDeclaredMethod("getId", clase))) {
//            try {
//                Map<String, Object> datos = new HashMap<>();
//                datos.put(metodos[0].getName(), clase.getDeclaredMethod("getId", clase));
//                datos.put(metodos[1].getName(), clase.getDeclaredMethod("getNombre", clase));
//                datos.put(metodos[2].getName(), clase.getDeclaredMethod("getApellido", clase));
//                datos.put(metodos[3].getName(), clase.getDeclaredMethod("getCedula", clase));
//                datos.put(metodos[4].getName(), clase.getDeclaredMethod("getDireccion", clase));
//                datos.put(metodos[5].getName(), clase.getDeclaredMethod("getUrlImg", clase);
//                long inicio = System.currentTimeMillis();
//                GeneralOnlineCRUD.guardar("Cliente", cliente.getId(), datos);
//                long fin = System.currentTimeMillis();
//                HelperTiempo.RetornarTiempo(fin, inicio);
//                System.out.println("Cliente guardada con exito con idCliente"+"\t"+id);
//            }
//            catch (Exception e) {
//                System.out.println("Error:" + e.getMessage());
//            }
//        }
//        else {
//            System.out.println("El id ya existe");
//        }
//    }
    @Override
    public <T> boolean registrarNube(T object) {

        try {
            Class<?> instanciaObjeto = object.getClass();
            String id = instanciaObjeto.getDeclaredMethod("getId").invoke(object).toString();

            if (!GeneralOnlineCRUD.existeRegistro(instanciaObjeto.getName(), instanciaObjeto, id)) {
                return false;
            }

            Field[] atributos = instanciaObjeto.getDeclaredFields();

            Map<String, Object> datos = new HashMap<>();
            datos.put(atributos[0].getName(), id);
            datos.put(atributos[1].getName(), instanciaObjeto.getDeclaredMethod("getNombre").invoke(object).toString());
            datos.put(atributos[2].getName(), instanciaObjeto.getDeclaredMethod("getApellido").invoke(object).toString());
            datos.put(atributos[3].getName(), instanciaObjeto.getDeclaredMethod("getCedula").invoke(object).toString());
            datos.put(atributos[4].getName(), instanciaObjeto.getDeclaredMethod("getDireccion").invoke(object).toString());
            datos.put(atributos[5].getName(), instanciaObjeto.getDeclaredMethod("getUrlImg").invoke(object).toString());

            long inicio = System.currentTimeMillis();
            GeneralOnlineCRUD.guardar(instanciaObjeto.getName(), id, datos);
            long fin = System.currentTimeMillis();
            HelperTiempo.RetornarTiempo(fin, inicio);

            System.out.println("Cliente guardada con exito con idCliente" + "\t" + id);
        } catch (ReflectiveOperationException e) {
            System.out.println("Error:" + e.getMessage());
        }
        return true;
    }

    public void eliminarCliente(String coleccion, Cliente cliente) {
        Cliente.class.getDeclaredFields();
    }

    /**
     * Metodo statico que se encarga de usar el metodo para registrar una persona en la nube
     *
     * @param objper   es la persona que se quiere registrar
     * @param id       es el idCliente que se quiere dar a ese usuario
     * @param producto es el producto asignado a esa persona
     */
//    public static void RegistrarPersonaNubeI(Cliente objper, int id, String producto) {
//        HelperRegistro objHelperRegistro = new HelperRegistro();
//        objHelperRegistro.RegistrarPersonaNube(objper, id, producto);
//    }

    public static <T> boolean RegistrarPersonaNubeI(T object) {
        HelperRegistro objHelperRegistro = new HelperRegistro();
        objHelperRegistro.registrarNube(object);
        return true;
    }
}
