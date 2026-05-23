package CapaLogicaNegocio.Controlador;

import CapaDatos.Logica_Conexion.ClienteDAO;
import CapaDatos.Logica_Conexion.ClienteOnlineCRUD;
import CapaLogicaNegocio.Excepciones.ExcepcionSQL;
import CapaLogicaNegocio.Helpers.HelperGestorBD;
import CapaLogicaNegocio.Logica_Negocio.Cliente;

public class ClienteControlador {

    private ClienteDAO clienteDAO;
    private ClienteOnlineCRUD clienteOnlineCRUD;

    public ClienteControlador(){
       this.clienteDAO = new ClienteDAO();
       this.clienteOnlineCRUD = new ClienteOnlineCRUD();
    }

    public RespuestaControlador agregarCliente(Cliente cliente) {
        try {
            HelperGestorBD.guardarRegistro(cliente, "Cliente", cliente.getId(),
                    () -> new ClienteDAO().agregar(cliente),
                    () -> new ClienteOnlineCRUD().registrarNube(cliente));
        }
        catch (ExcepcionSQL e) {
            return new RespuestaControlador(false, e.getMessage());
        }
    }

    public RespuestaControlador eliminarCliente(){

    }
}
