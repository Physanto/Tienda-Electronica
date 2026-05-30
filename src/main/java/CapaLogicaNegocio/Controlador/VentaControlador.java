package CapaLogicaNegocio.Controlador;

import CapaDatos.Logica_Conexion.VentaDAO;
import CapaDatos.Logica_Conexion.VentaOnlineCRUD;
import CapaLogicaNegocio.DTOS.VentasDTO;
import CapaLogicaNegocio.Helpers.HelperGestorBD;
import CapaLogicaNegocio.Helpers.HelperValidacion;
import CapaLogicaNegocio.Logica_Negocio.Venta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Clase Controladora encargada de ser la intermediaria y la comunicadora entre la vista y el modelo
 * para la gestión de Ventas. CADA COSA del negocio debe pasar por el controlador, la vista solo conoce el controlador. nada mas.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class VentaControlador {

    private VentaDAO ventaDAO;
    private VentaOnlineCRUD ventaOnlineCRUD;

    public VentaControlador(){
        this.ventaDAO = new VentaDAO();
        this.ventaOnlineCRUD = new VentaOnlineCRUD();
    }

    /**
     * Este metodo se encarga de agregar una venta a la base de datos local y nube
     * @param ventaDTO es un objeto generico (DTO) para la transferencia de datos de la venta desde la vista
     * @return un record con su campo .exito() de tipo booleano y mensaje de tipo String
     * para observar el mensaje personalizado
     *
     * @example Ejemplo de uso
     * <pre>{@code
     * // Instancias el controlador
     * VentaControlador vControlador = new VentaControlador();
     *
     * // Construyes el objeto DTO con los datos de la vista
     * VentaDTO dto = new VentaDTO(txtId.getText(), txtFecha.getText(), txtTotal.getText(), metodoPagoEnum, txtIdCliente.getText());
     *
     * // Le pasas el objeto al controlador y validas
     * RespuestaControlador<Venta> respuesta = vControlador.agregarVenta(dto);
     * if(!respuesta.exito()){
     * JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Error", JOptionPane.ERROR_MESSAGE);
     * } else {
     * JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
     * }
     * }
     * </pre>
     */
    public RespuestaControlador<Boolean> agregarVenta(VentasDTO.VentaDTO ventaDTO){

        if(ventaDTO == null) return new RespuestaControlador<>(false, "el objeto de la venta es nulo", null);

        RespuestaControlador<Boolean> respuestaControlador = validarCampos(ventaDTO);

        if(!respuestaControlador.exito()) return respuestaControlador;

        Date fechaVenta;
        Double totalVenta;

        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            fechaVenta = simpleDateFormat.parse(ventaDTO.fechaVenta());
            totalVenta = Double.valueOf(ventaDTO.totalVenta());
        }
        catch(Exception e){
            return new RespuestaControlador<>(false, "la fecha o total de venta no tienen un formato valido", null);
        }

        Venta venta = new Venta(ventaDTO.id(), fechaVenta, totalVenta,
                ventaDTO.metodoPago(), ventaDTO.idCliente());

        boolean exito = HelperGestorBD.guardarRegistro(venta, "Venta", ventaDTO.id(),
                () -> ventaDAO.agregar(venta),
                () -> ventaOnlineCRUD.registrarNube(venta)
        );

        return exito
                ? new RespuestaControlador<>(true, "Se ingresa la venta correctamente", null)
                : new RespuestaControlador<>(false, "No se puedo agregar la venta al sistema", null);
    }

    /**
     * Este metodo se encarga de buscar una venta especifica en la base de datos (local o nube) mediante su ID.
     * @param id El identificador unico (UUID) de la venta a buscar (capturado desde la vista).
     * @return un record RespuestaControlador con su campo .exito() en true si la encontro, un mensaje descriptivo
     * y en su campo .datos() la instancia de la Venta encontrada (o null si falla/no existe).
     * * @example Ejemplo de uso
     * <pre>{@code
     * // Obtienes el ID de la vista (ej. de un JTextField de busqueda) y llamas al controlador
     * RespuestaControlador<Venta> respuesta = vControlador.buscarVentaId(txtBuscarId.getText());
     *
     * if(respuesta.exito()){
     * // Extraes los datos del modelo encapsulado y llenas los campos de la pantalla
     * Venta ventaEncontrada = respuesta.datos();
     * txtTotal.setText(String.valueOf(ventaEncontrada.getTotalVenta()));
     * // ... demas campos
     * } else {
     * // Muestras el error devuelto por el controlador
     * JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Búsqueda", JOptionPane.WARNING_MESSAGE);
     * }
     * }
     * </pre>
     */
    public RespuestaControlador<Venta> buscarVentaId(String id){

        if(id == null) return new RespuestaControlador<>(false, "el objeto no debe ser nulo", null);

        if(id.isEmpty()) return new RespuestaControlador<>(false, "El id no debe estar vacio", null);

        Venta venta = HelperGestorBD.cargarRegistro(
                () -> ventaDAO.obtener(id),
                () -> ventaOnlineCRUD.obtenerNube(Venta.class, id)
        );

        return venta != null
                ? new RespuestaControlador<>(true, "Se encontro el registro en la venta", venta)
                : new RespuestaControlador<>(false, "No se pudo encontrar la venta", null);

    }

    /**
     * Este metodo se encarga de validar y actualizar la informacion de una venta existente,
     * impactando tanto la base de datos local como la nube.
     * @param ventaDTO Objeto de transferencia de datos con la nueva informacion capturada desde la vista.
     * @return un record RespuestaControlador indicando en su campo .exito() si la operacion fue exitosa, junto a un mensaje.
     *
     * @example Ejemplo de uso
     * <pre>{@code
     * // Construyes el DTO con los datos modificados en la pantalla
     * VentaDTO dtoActualizado = new VentaDTO(txtId.getText(), txtFecha.getText(), txtTotal.getText()...);
     *
     * // Llamas al controlador para procesar la actualizacion
     * RespuestaControlador<Venta> respuesta = vControlador.actualizarVenta(dtoActualizado);
     *
     * if(respuesta.exito()){
     * JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);
     * } else {
     * JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Error", JOptionPane.ERROR_MESSAGE);
     * }
     * }
     * </pre>
     */
    public RespuestaControlador<Boolean> actualizarVenta(VentasDTO.VentaDTO ventaDTO){

        if(ventaDTO == null) return new RespuestaControlador<>(false, "el objeto de la venta es nulo", null);

        RespuestaControlador<Boolean> respuestaControlador = validarCampos(ventaDTO);

        if(!respuestaControlador.exito()) return respuestaControlador;

        Date fechaVenta;
        Double totalVenta;

        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            fechaVenta = simpleDateFormat.parse(ventaDTO.fechaVenta());
            totalVenta = Double.valueOf(ventaDTO.totalVenta());
        }
        catch(Exception e){
            return new RespuestaControlador<>(false, "la fecha o total de venta no tienen un formato valido", null);
        }

        Venta venta = new Venta(ventaDTO.id(), fechaVenta, totalVenta,
                ventaDTO.metodoPago(), ventaDTO.idCliente());

        boolean exito = HelperGestorBD.actualizarRegistro(venta, "Venta", ventaDTO.id(),
                () -> ventaDAO.actualizar(venta),
                () -> ventaOnlineCRUD.actualizarNube(venta)
        );

        return exito
                ? new RespuestaControlador<>(true, "Se actualiza correctamente la venta", null)
                : new RespuestaControlador<>(false, "No se puede ingresar la venta al sistema", null);
    }

    /**
     * Este metodo se encarga de eliminar permanentemente un registro de venta,
     * sincronizando el borrado en la base de datos local y en la nube.
     * @param id El identificador unico (UUID) de la venta que se desea eliminar.
     * @return un record RespuestaControlador confirmando mediante .exito() el resultado de la operacion.
     * * @example Ejemplo de uso
     * <pre>{@code
     * // Es buena practica pedir confirmacion en la vista antes de enviar al controlador
     * int confirmacion = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar la venta?");
     * if (confirmacion == JOptionPane.YES_OPTION) {
     * RespuestaControlador<Venta> respuesta = vControlador.eliminarVenta(txtId.getText());
     * if(respuesta.exito()){
     * JOptionPane.showMessageDialog(this, respuesta.mensaje());
     * // Aqui la vista podria limpiar los campos o refrescar la tabla de ventas
     * } else {
     * JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Error al Eliminar", JOptionPane.ERROR_MESSAGE);
     * }
     * }
     * }
     * </pre>
     */
    public RespuestaControlador<Venta> eliminarVenta(String id){

        if(id == null) return new RespuestaControlador<>(false, "El id es un objeto nulo", null);

        if(id.trim().isEmpty()) return new RespuestaControlador<>(false, "El campo id esta vacio", null);

        boolean exito = HelperGestorBD.eliminarRegistro(id, "Venta",
                () -> ventaDAO.eliminar(id),
                () -> ventaOnlineCRUD.eliminarNube(id)
        );

        return exito
                ? new RespuestaControlador<>(true, "Venta eliminada con exito", null)
                : new RespuestaControlador<>(false, "Error al intentar eliminar la venta en el sistema.", null);
    }

    /**
     * Este metodo solicita y extrae la lista completa de todas las ventas registradas en el sistema.
     * @return un record RespuestaControlador que, de ser exitoso, contiene un ArrayList de objetos Venta en su propiedad .datos().
     * * @example Ejemplo de uso
     * <pre>{@code
     * RespuestaControlador<ArrayList<Venta>> respuesta = vControlador.buscarTodos();
     * if(respuesta.exito()){
     * ArrayList<Venta> listaVentas = respuesta.datos();
     * // Llenar tabla en la vista...
     * }
     * }
     * </pre>
     */
    public RespuestaControlador<ArrayList<Venta>> buscarTodos(){

        ArrayList<Venta> listaVentas = HelperGestorBD.cargarRegistros(
                () -> ventaDAO.obteners(),
                () -> ventaOnlineCRUD.obtenersNube(Venta.class)
        );

        return !listaVentas.isEmpty()
                ? new RespuestaControlador<>(true, "Imprimiendo todas las ventas", listaVentas)
                : new RespuestaControlador<>(false, "Error en la consulta de todas de las ventas", null); // se podria retornar la lista vacia en vez de null
    }

    /**
     * Genera un reporte de ventas agrupadas por categoria.
     * @return un record RespuestaControlador que contiene una lista de VentaPorCategoriaDTO.
     * * @example Ejemplo de uso
     * <pre>{@code
     * RespuestaControlador<ArrayList<VentaPorCategoriaDTO>> respuesta = vControlador.ventasPorCategoria();
     * if(respuesta.exito()){
     * ArrayList<VentaPorCategoriaDTO> reporte = respuesta.datos();
     * // Usar los datos para dibujar un grafico o llenar una tabla de estadisticas en la vista
     * }
     * }
     * </pre>
     */
     // NOTA: Este metodo NO usa sincronizacion con la nube, ya que se esta en la primera version de la APP.

    public RespuestaControlador<ArrayList<VentasDTO.VentaPorCategoriaDTO>> ventasPorCategoria(){

        ArrayList<VentasDTO.VentaPorCategoriaDTO> ventaPorCategoriaDTO = ventaDAO.ventasPorCategoria();

        return !ventaPorCategoriaDTO.isEmpty()
                ? new RespuestaControlador<>(true, "datos cargados correctamente", ventaPorCategoriaDTO)
                : new RespuestaControlador<>(false, "no hay datos", ventaPorCategoriaDTO);
    }

    public RespuestaControlador<Double> totalVentas(){

        Double totalVentas = ventaDAO.totalVentas();

        return totalVentas != null
                ? new RespuestaControlador<>(true, "se ejecuto correctamente la consulta",totalVentas)
                : new RespuestaControlador<>(false, "fallo la consulta", null);
    }

    public RespuestaControlador<Long> cantidadVentas(){

        Long cantidadVentas = ventaDAO.cantidadVentas();

        return cantidadVentas != null
                ? new RespuestaControlador<>(true, "se ejecuto correctamente la consulta", cantidadVentas)
                : new RespuestaControlador<>(false, "Fallo la consulta", null);
    }

    /**
     * Metodo privado de soporte (Helper) interno del controlador.
     * Centraliza las validaciones de negocio, nulos y vacios requeridas antes de procesar el VentaDTO.
     * Idealmente, la vista no tiene acceso a este metodo.
     * @param ventaDTO El objeto con los datos crudos procedentes de la capa de presentacion.
     * @return RespuestaControlador indicando (false) qué regla falló exactamente, o (true) si todos los campos son aptos.
     */
    public RespuestaControlador<Boolean> validarCampos(VentasDTO.VentaDTO ventaDTO){

        if(ventaDTO.id() == null || ventaDTO.fechaVenta() == null || ventaDTO.totalVenta() == null
                || ventaDTO.metodoPago() == null || ventaDTO.idCliente() == null){
            return new RespuestaControlador<>(false, "Alguno de los campos son nulos", null);
        }

        if(HelperValidacion.validarUUID(ventaDTO.id()) > 0) return new RespuestaControlador<>(false, "el id debe tener un formato valido (UUID)", null);

        if(ventaDTO.fechaVenta().isEmpty())return new RespuestaControlador<>(false, "La fecha esta vacia", null);

        if(HelperValidacion.validarNumero(ventaDTO.totalVenta()) > 0) return new RespuestaControlador<>(false, "Debe tener solo numeros el total venta", null);

        if(ventaDTO.metodoPago().name().isEmpty()) return new RespuestaControlador<>(false, "el metodo de pago no debe estar vacio", null);

        if(HelperValidacion.validarUUID(ventaDTO.idCliente()) > 0) return new RespuestaControlador<>(false, "El id debe tener un formato valido (UUID)", null);

        return new RespuestaControlador<>(true, "todos los campos estan correctos", null);
    }
}
