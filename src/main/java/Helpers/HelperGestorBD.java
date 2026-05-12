package Helpers;

import Logica_Conexion.ClienteDAO;
import Logica_Conexion.ClienteOnlineCRUD;
import Logica_Conexion.GeneralOnlineProviderCRUD;
import Logica_Conexion.SincronizadoraDAO;
import Logica_Negocio.Cliente;
import Logica_Negocio.Sincronizadora;
import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Clase que se encarga de gestionar la informacion que no se ingresa en las bases de datos cuando la conexion a internet falla
 * esta gestiona los datos y guarda la informacion cuando sea pertinente sin repetirla.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class HelperGestorBD {

    public static void GuardarPersonaGeneral(Cliente cliente){

        boolean online = HelperMonitorRed.estaUsandoNube();

        try{
            if(!new ClienteDAO().agregar(cliente)){
                guardarRedundancia(cliente);
                return;
            }
            if(!online){
                String registroJson = new Gson().toJson(cliente);
                new SincronizadoraDAO().agregar(new Sincronizadora(String.valueOf((int) (Math.random() * 100000)), Sincronizadora.Accion.INSERT, "Cliente", cliente.getId(), registroJson, "0"));
            }
            if(online && new ClienteOnlineCRUD().registrarNube(cliente)){
                System.out.println("Guardado exitoso en Local y Nube.");
            }
            else {
                System.out.println("Modo Offline activo. Guardado solo en Local (Estado 0).");
            }
        }
        catch(Exception e){
            System.out.println("Error faltal en la insercion de datos " + e.getMessage());
        }
    }

    public static ArrayList<Cliente> CargarPersonaGeneral(){
        ArrayList<Cliente> listaClientes = new ArrayList<>();
        boolean online = HelperMonitorRed.estaUsandoNube();

        try{
            if(online) {
                System.out.println("Listando desde la nube");
                listaClientes = GeneralOnlineProviderCRUD.obteners("Cliente", Cliente.class);
            }
            else {
                ClienteDAO clienteDAO = new ClienteDAO();
                listaClientes = clienteDAO.obteners();
                System.out.println("Listando desde local");
            }
        }
        catch(Exception e){
            System.out.println("Error faltal listando los datos " + e.getMessage());
        }
        return listaClientes;
    }

    public static Cliente CargarPersonaGeneral(String codigo){
        Cliente cliente = new Cliente();
        ClienteDAO clienteDAO = new ClienteDAO();
        boolean online = HelperMonitorRed.estaUsandoNube();

        try{
            if(online) {
                System.out.println("Listando desde la nube");
                cliente = GeneralOnlineProviderCRUD.obtener("Cliente", codigo, Cliente.class);
            }
            else {
                cliente = clienteDAO.obtener(codigo);
                System.out.println("Listando desde local");
            }
        }
        catch(Exception e){
            System.out.println("Error faltal listando los datos " + e.getMessage());
        }
        return cliente;
    }

    public static boolean EliminarPersonaGeneral(String coleccion, String codigo){
        boolean exito = true;
        boolean online = HelperMonitorRed.estaUsandoNube();

        try{
            ClienteDAO clienteDAO = new ClienteDAO();
            if(clienteDAO.eliminar(codigo)){ System.out.println("eliminado desde local"); }

            if(online) {
                System.out.println("Sistema online y listo para eliminar");

                if(GeneralOnlineProviderCRUD.eliminar(coleccion, codigo)){
                    System.out.println("Eliminado desde la nube.");
                }
                else{
                    System.out.println("Fallo en la eliminacion de la nube");
                }
            }
            else{
                System.out.println("No se elimino en nube ya que no hay conexion");
            }
        }
        catch(Exception e){
            System.out.println("Error eliminando los datos " + e.getMessage());
            exito = false;
        }
        return exito;
    }

    public static <T> void guardarRedundancia(T object){
        try(PrintWriter printWitter = new PrintWriter(
                new FileWriter("src/main/java/Metodo_Redundancia/bdRespaldo.txt", true)
        )){
            printWitter.println(new Gson().toJson(object));
        }
        catch (Exception ex){
            System.out.println("Error al guardar en el metodo de redundancia: " + ex.getMessage());
        }
    }
}
