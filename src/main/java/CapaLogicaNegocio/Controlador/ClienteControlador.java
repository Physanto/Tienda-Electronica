package CapaLogicaNegocio.Controlador;

import CapaDatos.Logica_Conexion.ClienteDAO;
import CapaDatos.Logica_Conexion.ClienteOnlineCRUD;
import CapaLogicaNegocio.DTOS.ClienteDTO;
import CapaLogicaNegocio.Helpers.HelperGestorBD;
import CapaLogicaNegocio.Helpers.HelperValidacion;
import CapaLogicaNegocio.Logica_Negocio.Cliente;


/**
 * Clase Controladora encargada de ser la intermediaria y la comunicadora entre la vista y el modelo
 * CADA COSA del negocio debe pasar por el controlador, la vista solo conoce el controlddor. nada mas.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class ClienteControlador {

    private ClienteDAO clienteDAO;
    private ClienteOnlineCRUD clienteOnlineCRUD;

    public ClienteControlador(ClienteDAO clienteDAO, ClienteOnlineCRUD clienteOnlineCRUD){
       this.clienteDAO = new ClienteDAO();
       this.clienteOnlineCRUD = new ClienteOnlineCRUD();
    }

    /**
     * Este metodo se encarga de agregar un cliente a la base de datos local y nube
     * @param clienteDTO es un objeto generico para la transferencia de datos al modelo
     * @return un record con su campo .exito() de tipo booleano y mensaje de tipo String
     * para observar el mensaje personalizado
     *
     * <p>
     *   Ejemplo de uso:
     *   <code>
     *       //Instancias el controlador y le pasas las instancias necesarias
     *       ClienteControlador clControlador = new ClienteControlador();
     *
     *       // validas todos los campos del formulario antes de construir el objeto, es decir
     *       if(txtId.getText().isEmpty())...
     *
     *       //construyes el objeto
     *       ClienteDTO dto = new ClienteDTO(txtId.getText(), txtNombre.getText(), txtApellido.getText()...n-campos);
     *
     *       //le pasas el objeto al controlador y validas, esto es un ejemplo
     *       if(!clControlador.agregarCliente(dto).exito()){
     *           JOptionPane.showMessageDialog(this, clControlador.mensaje(), "Error", JOptionPane.ERROR_MESSAGE); // puedes tambien anhadir mas contexto
     *       }
     *   </code>
     * </p>
     */
    public RespuestaControlador<Cliente> agregarCliente(ClienteDTO clienteDTO) {

        if(clienteDTO == null) return new RespuestaControlador<>(false, "El Objeto es nulo", null);

        RespuestaControlador<Cliente> respuestaControlador = validarCamposCliente(clienteDTO);

        if(!respuestaControlador.exito()){
           return respuestaControlador;
        }

        Cliente cliente = new Cliente(clienteDTO.id(), clienteDTO.nombre(),
                clienteDTO.apellido(), clienteDTO.cedula(), clienteDTO.direccion());

        boolean exito = HelperGestorBD.guardarRegistro(cliente, "Cliente", cliente.getId(),
                () -> clienteDAO.agregar(cliente),
                () -> clienteOnlineCRUD.registrarNube(cliente)
        );

        return (exito)
                ? new RespuestaControlador<>(true, "Cliente agregado con exito", null)
                : new RespuestaControlador<>(false, "Error al intentar guardar el cliente al sistema", null);
    }

    public RespuestaControlador<Cliente> buscarClienteId(String id){

        if(id == null) return new RespuestaControlador<>(false, "El id es nulo", null);

        if(id.isEmpty()) return new RespuestaControlador<>(false, "El campo id esta vacio", null);

        Cliente cliente = HelperGestorBD.cargarRegistro(
                () -> clienteDAO.obtener(id),
                () -> clienteOnlineCRUD.obtenerNube(Cliente.class, id)
        );

        return cliente != null
                ? new RespuestaControlador<>(true, "Cliente encontrado con exito", cliente)
                : new RespuestaControlador<>(false, "Cliente no encontrado con ese id", null);
    }

    public RespuestaControlador<Cliente> actualizarCliente(ClienteDTO clienteDTO){

        if(clienteDTO == null) return new RespuestaControlador<>(false, "El objeto es nulo", null);

        RespuestaControlador<Cliente> respuestaControlador = validarCamposCliente(clienteDTO);

        if(!respuestaControlador.exito()) return respuestaControlador;

        Cliente cliente = new Cliente(clienteDTO.id(), clienteDTO.nombre(),
                clienteDTO.apellido(), clienteDTO.cedula(), clienteDTO.direccion());

        boolean exito = HelperGestorBD.actualizarRegistro(cliente, "Cliente", cliente.getId(),
                ()-> clienteDAO.actualizar(cliente),
                ()-> clienteOnlineCRUD.actualizarNube(cliente)
        );

        return (exito)
                ? new RespuestaControlador<>(true, "Cliente actualizado con exito", null)
                : new RespuestaControlador<>(false, "Error al intentar actualizar el cliente en el sistema", null);
    }

    private RespuestaControlador<Cliente> validarCamposCliente(ClienteDTO clienteDTO) {

        if (HelperValidacion.ValidarVacio(clienteDTO.nombre()) > 0) return new RespuestaControlador<>(false, "El nombre está vacío", null);
        if (HelperValidacion.ValidarTodoLetra(clienteDTO.nombre()) > 0) return new RespuestaControlador<>(false, "El nombre solo debe contener letras", null);

        if (HelperValidacion.ValidarVacio(clienteDTO.apellido()) > 0) return new RespuestaControlador<>(false, "El apellido está vacío", null);
        if (HelperValidacion.ValidarTodoLetra(clienteDTO.apellido()) > 0) return new RespuestaControlador<>(false, "El apellido solo debe contener letras", null);

        if (HelperValidacion.ValidarVacio(clienteDTO.cedula()) > 0) return new RespuestaControlador<>(false, "La cédula es obligatoria", null);
        if (HelperValidacion.validarNumero(clienteDTO.cedula()) > 0) return new RespuestaControlador<>(false, "La cédula debe contener solo números", null);

        if (HelperValidacion.ValidarVacio(clienteDTO.direccion()) > 0) return new RespuestaControlador<>(false, "La dirección es obligatoria", null);
        if (HelperValidacion.ValidarTodoDireccion(clienteDTO.direccion()) > 0) return new RespuestaControlador<>(false, "La dirección es obligatoria", null);

        return new RespuestaControlador<>(true, "Validación exitosa", null);
    }

    public RespuestaControlador<Cliente> eliminarCliente(){

        return null;
    }
}
