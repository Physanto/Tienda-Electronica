package Helpers;

import Logica_Conexion.PersonaProvider;
import Logica_Negocio.Cliente;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Santiago Lopez Patron Adapter
 */


/**
 * Clase encargada de Registrar las personas a la base de datos en la nube
 */
public class HelperRegistro implements IHelperRegistro{

    /**
     * Metodo que registra a una persona en la base de datos de la nube
     * @param objper es la persona que se quiere registrar
     * @param id es el id que se quiere dar a ese usuario
     * @param producto es el producto asignado a esa persona
     */
    @Override
    public void RegistrarPersonaNube(Cliente objper, int id, String producto)
    {
        boolean res = PersonaProvider.RetornarUid(objper.getUid());
        if (!res) {
            try {
                Map<String, Object> datos = new HashMap<>();
                datos.put("uid", objper.getUid());
                datos.put("Nombre", objper.getNombre());
                datos.put("Apellido", objper.getApellido());
                datos.put("Direccion", objper.getDireccion());
                datos.put("Cedula", objper.getCedula());
                datos.put("Productos", producto);
                datos.put("Nom_img", objper.getNom_img());
                long inicio = System.currentTimeMillis();
                PersonaProvider.GuardarPersona("Cliente", String.valueOf(id), datos);
                long fin = System.currentTimeMillis();
                HelperTiempo.RetornarTiempo(fin, inicio);
                System.out.println("Cliente guardada con exito con id"+"\t"+id);
            } catch (Exception e) {
                System.out.println("Error:" + e.getMessage());
            }
        }
        else {
            System.out.println("El uid ya existe");
        }
    }

    /**
     * Metodo statico que se encarga de usar el metodo para registrar una persona en la nube
     * @param objper es la persona que se quiere registrar
     * @param id es el id que se quiere dar a ese usuario
     * @param producto es el producto asignado a esa persona
     */
    public static void RegistrarPersonaNubeI(Cliente objper, int id, String producto) {
        HelperRegistro objHelperRegistro= new HelperRegistro();
        objHelperRegistro.RegistrarPersonaNube(objper, id, producto);
    }
}
