package Helpers;

import Logica_Conexion.ClienteDAO;
import Logica_Conexion.PersonaProvider;
import Logica_Negocio.Cliente;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase encargada de Sincronizar datos no reflejados en las dos bases de datos
 * @author Manuel Escobar (Physanto)
 */
public class HelperSincronizador {

    /**
     * Metodo que se encarga de sincronizar la base de datos local a la nube
     * - valida que haya datos sin sincronizar (con ayuda de un metodo personalizado ClienteDAO.getNoSincronizados())
     * - si hay datos que no estan sincronizados entonces los agrega a la nube
     * - de lo contrario sino hay nada pendiente entonces hace una parada anticipada
     */
    public static void SincronizarLocalANube(){
        System.out.println("Iniciando vaciado de cola local hacia la nube...");

        try{
            ClienteDAO clienteDAO = new ClienteDAO();
            ArrayList<Cliente> pendientes = clienteDAO.getNoSincronizados();

            if(pendientes.isEmpty()){
                System.out.println("No hay datos pendientes por subir");
                return;
            }

            for (Cliente p : pendientes) {
                Map<String, Object> datos = new HashMap<>();
                datos.put("uid", p.getUid());
                datos.put("Nombre", p.getNombre());
                datos.put("Apellido", p.getApellido());
                datos.put("Direccion", p.getDireccion());
                datos.put("Cedula", p.getCedula());
                datos.put("Productos", p.getProducto());
                datos.put("Nom_img", p.getNom_img());

                boolean exito = PersonaProvider.GuardarPersona("Cliente", p.getUid(), datos);

                if (exito) {
                    clienteDAO.marcarSincronizado(p.getUid());
                    System.out.println("Cliente " + p.getUid() + " sincronizada con exito.");
                }
            }
        }
        catch (Exception e){
            System.out.println("Error durante la sincronizacion: " + e.getMessage());
        }
    }
}
