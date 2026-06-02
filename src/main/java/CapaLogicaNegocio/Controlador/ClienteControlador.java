package CapaLogicaNegocio.Controlador;

import CapaDatos.Logica_Conexion.ClienteDAO;
import CapaDatos.Logica_Conexion.ClienteOnlineCRUD;
import CapaLogicaNegocio.DTOS.ClientesDTO;
import CapaLogicaNegocio.Helpers.HelperGestorBD;
import CapaLogicaNegocio.Helpers.HelperValidacion;
import CapaLogicaNegocio.Logica_Negocio.Cliente;
import CapaLogicaNegocio.Logica_Negocio.CredencialesClienteDemo;

import java.util.ArrayList;

/**
 * Clase Controladora encargada de ser la intermediaria y la comunicadora entre
 * la vista y el modelo
 * CADA COSA del negocio debe pasar por el controlador, la vista solo conoce el
 * controlddor. nada mas.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class ClienteControlador {

    private ClienteDAO clienteDAO;
    private ClienteOnlineCRUD clienteOnlineCRUD;

    public ClienteControlador() {
        this.clienteDAO = new ClienteDAO();
        this.clienteOnlineCRUD = new ClienteOnlineCRUD();
    }

    /**
     * Este metodo se encarga de agregar un cliente a la base de datos local y nube
     * 
     * @param clienteDTO es un objeto generico para la transferencia de datos al
     *                   modelo
     * @return un record con su campo .exito() de tipo booleano y mensaje de tipo
     *         String
     *         para observar el mensaje personalizado
     *
     * @example Ejemplo de uso
     * 
     *          <pre>{@code
     * //Instancias el controlador y le pasas las instancias necesarias
     * ClienteControlador clControlador = new ClienteControlador();
     *
     * // validas todos los campos del formulario antes de construir el objeto, es decir
     * if(txtId.getText().isEmpty())...
     *
     * //construyes el objeto
     * ClienteDTO dto = new ClienteDTO(txtId.getText(), txtNombre.getText(), txtApellido.getText()...n-campos);
     *
     * //le pasas el objeto al controlador y validas, esto es un ejemplo
     * if(!clControlador.agregarCliente(dto).exito()){
     * JOptionPane.showMessageDialog(this, clControlador.mensaje(), "Error", JOptionPane.ERROR_MESSAGE); // puedes tambien anhadir mas contexto
     * }
     * }
     * </pre>
     */
    public RespuestaControlador<Cliente> agregarCliente(ClientesDTO.ClienteDTO clienteDTO) {

        if (clienteDTO == null)
            return new RespuestaControlador<>(false, "El Objeto es nulo", null);

        RespuestaControlador<Cliente> respuestaControlador = validarCamposCliente(clienteDTO);

        if (!respuestaControlador.exito()) {
            return respuestaControlador;
        }

        Cliente cliente = new Cliente(clienteDTO.id(), clienteDTO.nombre(),
                clienteDTO.apellido(), clienteDTO.cedula(), clienteDTO.direccion());

        boolean exito = HelperGestorBD.guardarRegistro(cliente, "Cliente", cliente.getId(),
                () -> clienteDAO.agregar(cliente),
                () -> clienteOnlineCRUD.registrarNube(cliente));

        return (exito)
                ? new RespuestaControlador<>(true, "Cliente agregado con exito", null)
                : new RespuestaControlador<>(false, "Error al intentar guardar el cliente al sistema", null);
    }

    /**
     * Este metodo se encarga de buscar un cliente especifico en la base de datos
     * (local o nube) mediante su ID.
     * 
     * @param id El identificador unico del cliente a buscar (capturado desde la
     *           vista).
     * @return un record RespuestaControlador con su campo .exito() en true si lo
     *         encontro, un mensaje descriptivo
     *         y en su campo .datos() la instancia del Cliente encontrado (o null si
     *         falla/no existe).
     * @example Ejemplo de uso
     * 
     *          <pre>{@code
     * // Obtienes el ID de la vista (ej. de un JTextField de busqueda) y llamas al controlador
     * RespuestaControlador<Cliente> respuesta = clControlador.buscarClienteId(txtBuscarId.getText());
     *
     * if(respuesta.exito()){
     * // Extraes los datos del modelo encapsulado y llenas los campos de la pantalla
     * Cliente clienteEncontrado = respuesta.datos();
     * txtNombre.setText(clienteEncontrado.getNombre());
     * txtApellido.setText(clienteEncontrado.getApellido());
     * // ... demas campos
     * } else {
     * // Muestras el error devuelto por el controlador
     * JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Búsqueda", JOptionPane.WARNING_MESSAGE);
     * }
     * }
     * </pre>
     */
    public RespuestaControlador<Cliente> buscarClienteId(String id) {

        if (id == null)
            return new RespuestaControlador<>(false, "El id es nulo", null);

        if (id.isEmpty())
            return new RespuestaControlador<>(false, "El campo id esta vacio", null);

        Cliente cliente = HelperGestorBD.cargarRegistro(
                () -> clienteDAO.obtener(id),
                () -> clienteOnlineCRUD.obtenerNube(Cliente.class, id));

        return cliente != null
                ? new RespuestaControlador<>(true, "Cliente encontrado con exito", cliente)
                : new RespuestaControlador<>(false, "Cliente no encontrado con ese id", null);
    }

    /**
     * Este metodo se encarga de validar y actualizar la informacion de un cliente
     * existente,
     * impactando tanto la base de datos local como la nube.
     * 
     * @param clienteDTO Objeto de transferencia de datos con la nueva informacion
     *                   capturada desde la vista.
     * @return un record RespuestaControlador indicando en su campo .exito() si la
     *         operacion fue exitosa, junto a un mensaje.
     *
     * @example Ejemplo de uso
     * 
     *          <pre>{@code
     * // Construyes el DTO con los datos modificados en la pantalla
     * ClientesDTO.ClienteDTO dto = new ClientesDTO.ClienteDTO(txtId.getText(), txtNombre.getText(), txtApellido.getText()...);
     *
     * // Llamas al controlador para procesar la actualizacion
     * RespuestaControlador<Cliente> respuesta = clControlador.actualizarCliente(dto);
     *
     * if(respuesta.exito()){
     * JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);
     * } else {
     * JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Error", JOptionPane.ERROR_MESSAGE);
     * }
     * }
     * </pre>
     */
    public RespuestaControlador<Cliente> actualizarCliente(ClientesDTO.ClienteDTO clienteDTO) {

        if (clienteDTO == null)
            return new RespuestaControlador<>(false, "El objeto es nulo", null);

        RespuestaControlador<Cliente> respuestaControlador = validarCamposCliente(clienteDTO);

        if (!respuestaControlador.exito())
            return respuestaControlador;

        Cliente cliente = new Cliente(clienteDTO.id(), clienteDTO.nombre(),
                clienteDTO.apellido(), clienteDTO.cedula(), clienteDTO.direccion());

        boolean exito = HelperGestorBD.actualizarRegistro(cliente, "Cliente", cliente.getId(),
                () -> clienteDAO.actualizar(cliente),
                () -> clienteOnlineCRUD.actualizarNube(cliente));

        return (exito)
                ? new RespuestaControlador<>(true, "Cliente actualizado con exito", null)
                : new RespuestaControlador<>(false, "Error al intentar actualizar el cliente en el sistema", null);
    }

    /**
     * Este metodo se encarga de eliminar permanentemente un registro de cliente
     * especifico,
     * sincronizando el borrado en la base de datos local y en la nube.
     * 
     * @param id El identificador unico del cliente que se desea eliminar.
     * @return un record RespuestaControlador confirmando mediante .exito() el
     *         resultado de la operacion.
     * @example Ejemplo de uso
     * 
     *          <pre>{@code
     * // Es buena practica pedir confirmacion en la vista antes de enviar al controlador
     * int confirmacion = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar al cliente?");
     * * if (confirmacion == JOptionPane.YES_OPTION) {
     * RespuestaControlador<Cliente> respuesta = clControlador.eliminarCliente(txtId.getText());
     * * if(respuesta.exito()){
     * JOptionPane.showMessageDialog(this, respuesta.mensaje());
     * // Aqui la vista podria limpiar los campos o refrescar la tabla de clientes
     * } else {
     * JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Error al Eliminar", JOptionPane.ERROR_MESSAGE);
     * }
     * }
     * </pre>
     */
    public RespuestaControlador<Cliente> eliminarCliente(String id) {

        if (id == null)
            return new RespuestaControlador<>(false, "el id es un objeto nulo", null);

        if (id.trim().isEmpty())
            return new RespuestaControlador<>(false, "El campo id esta vacio", null);

        boolean exito = HelperGestorBD.eliminarRegistro(id, "Cliente",
                () -> clienteDAO.eliminar(id),
                () -> clienteOnlineCRUD.eliminarNube(id));

        return exito
                ? new RespuestaControlador<>(true, "Cliente eliminado con exito", null)
                : new RespuestaControlador<>(false, "Error al intentar eliminar el cliente en el sistema.", null);
    }

    /**
     * Este metodo solicita y extrae la lista completa de todos los clientes
     * registrados en el sistema.
     * 
     * @return un record RespuestaControlador que, de ser exitoso, contiene un
     *         ArrayList de objetos Cliente en su propiedad .datos().
     * @example Ejemplo de uso
     *
     *          <pre>{@code
     * RespuestaControlador<ArrayList<Cliente>> respuesta = clControlador.buscarTodos();
     * if(respuesta.exito()){
     * </pre>
     */
    public RespuestaControlador<ArrayList<Cliente>> buscarTodos() {

        ArrayList<Cliente> listaClientes = new ArrayList<>();
        listaClientes = HelperGestorBD.cargarRegistros(
                () -> clienteDAO.obteners(),
                () -> clienteOnlineCRUD.obtenersNube(Cliente.class));

        return !listaClientes.isEmpty()
                ? new RespuestaControlador<>(true, "Imprimiendo todos los clientes", listaClientes)
                : new RespuestaControlador<>(false, "Error en la consulta de todos los clientes", null);
    }

    /**
     * Inicia sesion de un cliente de demostracion. Como el modulo Cliente opera
     * UNICAMENTE contra la
     * nube (ver arquitectura solo-nube), este metodo valida las credenciales
     * predefinidas en el
     * programa ({@link CredencialesClienteDemo}) y luego busca en la nube el
     * Cliente cuyo nombre
     * coincide con el usuario, para asociar la sesion al cliente real (con su id
     * real). Asi, por
     * ejemplo, el usuario "juan" queda asociado al Cliente llamado "juan" en la
     * base de datos.
     *
     * @param usuario     nombre de usuario ingresado en el login
     * @param contrasenha contrasenha ingresada en el login
     * @return RespuestaControlador con .exito()=true y el Cliente real en .dato()
     *         si todo salio bien
     */
    public RespuestaControlador<Cliente> iniciarSesionCliente(String usuario, String contrasenha) {

        if (!CredencialesClienteDemo.validar(usuario, contrasenha))
            return new RespuestaControlador<>(false, "Usuario o contraseña inválida", null);

        String nombreBuscado = usuario.trim();

        // El cliente solo lee la nube: se traen los clientes de Firestore y se busca
        // por nombre.
        ArrayList<Cliente> clientes = clienteOnlineCRUD.obtenersNube(Cliente.class);

        if (clientes != null) {
            for (Cliente cliente : clientes) {
                if (cliente != null && cliente.getNombre() != null
                        && cliente.getNombre().trim().equalsIgnoreCase(nombreBuscado)) {
                    return new RespuestaControlador<>(true, "Bienvenido " + cliente.getNombre(), cliente);
                }
            }
        }

        return new RespuestaControlador<>(false,
                "No se encontró un cliente llamado '" + nombreBuscado + "' en la base de datos", null);
    }

    /**
     * Devuelve la cantidad de clientes que existen en la base de datos, este los
     * saca de la base de datos local
     * 
     * @return objeto con un campo booleano si se ejecuto correctamente la consulta
     *         o no, un mensaje informativo y por ultimo un campo
     *         que contiene la cantidad de clientes que existen en la base de datos
     */
    public RespuestaControlador<Long> cantidadClientes() {

        Long cantidadClientes = clienteDAO.cantidadClientes();

        return cantidadClientes != null
                ? new RespuestaControlador<>(true, "Se hizo la consulta satisfactoriamente", cantidadClientes)
                : new RespuestaControlador<>(false,
                        "Error en la consulta a la base de datos para la cantidad de clientes", null);
    }

    /**
     * Metodo privado de soporte (Helper) interno del controlador.
     * Centraliza las validaciones de negocio, nulos y vacios requeridas antes de
     * procesar el ClienteDTO.
     * La vista no tiene acceso a este metodo.
     * * @param clienteDTO El objeto con los datos crudos procedentes de la capa de
     * presentacion.
     * 
     * @return RespuestaControlador indicando (false) qué regla falló exactamente, o
     *         (true) si todos los campos son aptos.
     */
    private RespuestaControlador<Cliente> validarCamposCliente(ClientesDTO.ClienteDTO clienteDTO) {

        if (clienteDTO.id() == null || clienteDTO.nombre() == null || clienteDTO.apellido() == null
                || clienteDTO.cedula() == null || clienteDTO.direccion() == null)
            return new RespuestaControlador<>(false, "Algun campo es nulo", null);

        if (clienteDTO.id().isEmpty())
            return new RespuestaControlador<>(false, "el id no puede estar vacio", null);
        if (HelperValidacion.validarUUID(clienteDTO.id()) > 0)
            return new RespuestaControlador<>(false, "el id no del tipo esperado (UUID)", null);

        if (clienteDTO.nombre().isEmpty())
            return new RespuestaControlador<>(false, "El nombre está vacío", null);
        if (HelperValidacion.ValidarTodoLetra(clienteDTO.nombre()) > 0)
            return new RespuestaControlador<>(false, "El nombre solo debe contener letras", null);

        if (clienteDTO.apellido().isEmpty())
            return new RespuestaControlador<>(false, "El apellido está vacío", null);
        if (HelperValidacion.ValidarTodoLetra(clienteDTO.apellido()) > 0)
            return new RespuestaControlador<>(false, "El apellido solo debe contener letras", null);

        if (clienteDTO.cedula().isEmpty())
            return new RespuestaControlador<>(false, "La cédula es obligatoria", null);
        if (HelperValidacion.validarNumero(clienteDTO.cedula()) > 0)
            return new RespuestaControlador<>(false, "La cédula debe contener solo números", null);

        if (clienteDTO.direccion().isEmpty())
            return new RespuestaControlador<>(false, "La dirección es obligatoria", null);
        if (HelperValidacion.ValidarTodoDireccion(clienteDTO.direccion()) > 0)
            return new RespuestaControlador<>(false, "La dirección es obligatoria", null);

        return new RespuestaControlador<>(true, "Validación exitosa", null);
    }

    // metodo para efectos de prueba

    public void crearClientesNube() {
        ClienteOnlineCRUD clienteOnlineCRUD = new ClienteOnlineCRUD();
        ArrayList<Cliente> clientes = new ArrayList<>();

        clientes.add(new Cliente(
                "3b7a5b82-9c4d-4f12-ba73-6d51e2f890ab",
                "juan",
                "martinez",
                "1234",
                "cra 2"));

        clientes.add(new Cliente(
                "3d3a5b82-9c4d-4f12-ba73-6d51e2f890ab",
                "pedro",
                "lopez",
                "12345",
                "cra 6"));

        clientes.add(new Cliente(
                "3o9425b82-9c4d-4f12-ba73-6d51e2f890ab",
                "jordan",
                "suiza",
                "01234",
                "cra 57"));

        clientes.add(new Cliente(
                "3p0j225b82-9c4d-4f12-ba73-6d51e2f890ab",
                "lucas",
                "perez",
                "01234567",
                "cra 557"));

        clientes.add(new Cliente(
                "3uj425b82-9c4d-4f12-ba73-6d51e2f890ab",
                "matias",
                "frescano",
                "123456",
                "cra 54"));
        clienteOnlineCRUD.registrarNube(new Cliente(
                "3b7a5b82-9c4d-4f12-ba73-6d51e2f890ab",
                "juan",
                "martinez",
                "1234",
                "cra 2"));

        for (Cliente cliente : clientes) {
            clienteOnlineCRUD.registrarNube(cliente);
        }
    }
}
