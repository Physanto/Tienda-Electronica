package Helpers;

import Logica_Conexion.ClienteDAO;
import Logica_Conexion.GeneralOnlineCRUD;
import Logica_Negocio.Cliente;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HelperGestorBD {

    public static void GuardarPersonaGeneral(Cliente cliente, int id, String producto){

        boolean online = HelperMonitorRed.estaUsandoNube();

        try{
            ClienteDAO clienteDAO = new ClienteDAO();
            clienteDAO.agregar(cliente);

            if(online){
//                //aqui podemos usar el metodo de guardarPersonaNube pero se debe modificar
//                // esto con el fin de no repetir codigo aqui
//                Map<String, Object> datos = new HashMap<>();
//                datos.put("id", cliente.getId());
//                datos.put("nombre", cliente.getNombre());
//                datos.put("apellido", cliente.getApellido());
//                datos.put("direccion", cliente.getDireccion());
//                datos.put("cedula", cliente.getCedula());
//                datos.put("urlImg", cliente.getUrlImg());
                boolean exito = HelperRegistro.RegistrarPersonaNubeI(cliente);

                if (exito) {
                    clienteDAO.marcarResultSetincronizado(cliente.getId());
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
                listaClientes = GeneralOnlineCRUD.obteners("Cliente", Cliente.class);
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
                cliente = GeneralOnlineCRUD.obtener("Cliente", codigo, Cliente.class);
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

                if(GeneralOnlineCRUD.eliminar(coleccion, codigo, Cliente.class)){
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
