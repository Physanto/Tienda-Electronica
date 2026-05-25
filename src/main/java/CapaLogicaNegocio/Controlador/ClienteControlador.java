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

        boolean exito = HelperGestorBD.guardarRegistro(cliente, "Cliente", cliente.getId(),
                () -> new ClienteDAO().agregar(cliente),
                () -> new ClienteOnlineCRUD().registrarNube(cliente)
        );

        return (exito) ? new RespuestaControlador(false, "Cliente agregado con exito")
                : new RespuestaControlador(false, "Cliente no agregado");
    }

    public RespuestaControlador eliminarCliente(){

        return null;
    }
}
