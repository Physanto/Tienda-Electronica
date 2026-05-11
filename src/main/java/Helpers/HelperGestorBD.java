package Helpers;

import Logica_Conexion.ClienteDAO;
import Logica_Conexion.ClienteOnlineDAO;
import Logica_Conexion.GeneralOnlineProviderCRUD;
import Logica_Conexion.SincronizadoraDAO;
import Logica_Negocio.Cliente;
import Logica_Negocio.Sincronizadora;

import java.util.ArrayList;
import java.util.Date;


/**
 * Clase que se encarga de gestionar la informacion que no se ingresa en las bases de datos cuando la conexion a internet falla
 * esta gestiona los datos y guarda la informacion cuando sea pertinente sin repetirla.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class HelperGestorBD {

    public static void GuardarPersonaGeneral(Cliente cliente, int id, String producto){

        boolean online = HelperMonitorRed.estaUsandoNube();

        try{
            ClienteDAO clienteDAO = new ClienteDAO();
            clienteDAO.agregar(cliente);

            if(!online){
                Date n = new Date();
                SincronizadoraDAO sincronizadoraDAO = new SincronizadoraDAO();
                sincronizadoraDAO.agregar(new Sincronizadora("1", Sincronizadora.Accion.INSERT, "Cliente", String.valueOf(id), n));
            }
            if(online){
                ClienteOnlineDAO clienteOnlineDAO = new ClienteOnlineDAO();
                if (clienteOnlineDAO.registrarNube(cliente)) {
                    System.out.println("Guardado exitoso en Local y Nube.");
                }
                else {
                    System.out.println("Fallo en Firebase. El registro se quedó en la cola local (Estado 0).");
                }
            }
            else {
                System.out.println("Modo Offline activo. Guardado solo en Local (Estado 0).");
            }
        }
        catch(Exception e){
            System.out.println("Error faltal, guardando datos " + e.getMessage());
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

                if(GeneralOnlineProviderCRUD.eliminar(coleccion, codigo, Cliente.class)){
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
}
