package CapaLogicaNegocio.Controlador;

import CapaDatos.Logica_Conexion.PromocionDAO;
import CapaLogicaNegocio.DTOS.AnalisisCliente;
import CapaLogicaNegocio.DTOS.PromocionesDTO;
import CapaLogicaNegocio.Helpers.HelperIAPromociones;
import CapaLogicaNegocio.Helpers.HelperIASegmentadorClientes;
import CapaLogicaNegocio.Helpers.HelperValidacion;
import CapaLogicaNegocio.Logica_Negocio.Promocion;
import CapaLogicaNegocio.DTOS.Promociones;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Date;

/**
 * Clase que implementa las buenas practicas del MVC para disenar un controlador
 * el cual es capaz de hacer la comunicacion efectiva entre la vista y el modelo
 * Este es para el movimiento general de las promociones del sistema
 *
 * @author Manuel Figueroa (Physanto)
 */
public class PromocionControlador {

    private PromocionDAO promocionDAO;

    public PromocionControlador(){
        this.promocionDAO = new PromocionDAO();
    }

    /**
     * Metodo que devuelve una lista de productos que se pueden aplicarle una promocion arbitraria
     * Este es el metodo que se llama apenas el admin presiona el boton del modulo de Promociones
     * @return una lista con los productos tambien un campo exit() para verificar si fue correcta la consulta o no
     * y el mensaje descriptivo, de lo contrario retorna null si la lista esta vacia o ha habido un problema
     */
    public RespuestaControlador<ArrayList<PromocionesDTO.PromocionAplicadaDTO>> obtenerProductosPromocion(){

        ArrayList<Promocion> listaPromociones = HelperIAPromociones.agruparProductos(promocionDAO.getDataset());

        ArrayList<Promocion> listaPerfiles = HelperIAPromociones.clasificarInventario();

        int idClusterEstancado = -1;
        for (Promocion perfil : listaPerfiles) {
            if (perfil.getClasificacion().equals("ESTANCADO")) {
                idClusterEstancado = perfil.getCluster();
                break;
            }
        }

        if (idClusterEstancado == -1) {
            return new RespuestaControlador<>(false, "Error en IA: No se detectó un grupo de productos estancados.", null);
        }

        ArrayList<PromocionesDTO.PromocionAplicadaDTO> lista = promocionDAO.datosPromociones(); //listado de todos los productos de la bd
        //lista preparada para setear los productos que si tienen promocion
        ArrayList<PromocionesDTO.PromocionAplicadaDTO> listaProductos = new ArrayList<>();

        for (PromocionesDTO.PromocionAplicadaDTO promocionAplicadaDTO : lista) {
            for (Promocion promocion : listaPromociones) {

                if (promocionAplicadaDTO.id().equals(promocion.getId()) && promocion.getCluster() == idClusterEstancado) {
                    listaProductos.add(promocionAplicadaDTO);
                }
            }
        }
        return !listaProductos.isEmpty()
                ? new RespuestaControlador<>(true, "Lista de productos para la promocion", listaProductos)
                : new RespuestaControlador<>(false, "No hay productos en promocion", null);
    }

    /**
     * aplica la promocion al producto seleccionado de la tabla
     * @param idProductoSeleccionado es el id del producto que el cliente selecciono de la tabla para aplicarle la promocion
     * @param promocionesDTO objeto que contiene los datos (fechaInicio, fechaFin...) para agregar la promocion
     * el campo de tipo() PONER "ESPECIFICA" ya que es para un producto en especial.
     * @return un record en el cual contiene un campo .exito() de true si aplico correctamente la promocion, un campo de mensaje
     */
    public RespuestaControlador<Boolean> aplicarPromocionProducto(String idProductoSeleccionado, PromocionesDTO.PromocionessDTO promocionesDTO){

        RespuestaControlador<Boolean> respuestaControlador = validarCampos(promocionesDTO);

        if(!respuestaControlador.exito()) return respuestaControlador;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Promociones promociones;
        try{
            promociones = new Promociones(promocionesDTO.id(), promocionesDTO.nombre(),
                    Double.valueOf(promocionesDTO.descuento()), simpleDateFormat.parse(promocionesDTO.fechaInicio()),
                    simpleDateFormat.parse(promocionesDTO.fechaInicio()), Promociones.TipoPromocion.valueOf(promocionesDTO.tipo().toUpperCase())
            );
        }
        catch (Exception e){
            return new RespuestaControlador<>(false, "Error al tratar de realizar algun tipo de conversion", null);
        }

        if(!promocionDAO.agregarPromocionPersonalizada(promociones)) return new RespuestaControlador<>(false, "Error al tratar de insertar en promociones", null);

        if(!promocionDAO.agregarPromocionProducto(new PromocionesDTO.PromocionProductoDTO(UUID.randomUUID().toString(),
                promociones.getId(), idProductoSeleccionado)
        )) return new RespuestaControlador<>(false, "No se pudo aplicar la promocion al producto especifico", null);

        return new RespuestaControlador<>(true, "promocion agregada al producto con exito", null);
    }

    /**
     * metodo para obtener todas las promociones que estan activas en el sistema
     * @return un record con un campo de exito() para verificar si fue correcta la operacion o no,
     * un mensaje informativo y por ultimo la lista de las promociones activas.
     */
    public RespuestaControlador<ArrayList<Promociones>> obtenerPromocionesActivas(){

        ArrayList<Promociones> listaPromociones = promocionDAO.obtenerPromocionesPersonalizadas();

        return listaPromociones.isEmpty()
                ? new RespuestaControlador<>(true, "Mostrando promociones",listaPromociones)
                : new RespuestaControlador<>(false, "Lista vacia", null);
    }

    /**
     * metodo que desactiva la promocion que ha sido seleccionada por el usuario
     * @param fechaActual es la fecha actual para que pueda desactivarse la promocion, campo que se va quitar en futuras mejoras
     * @param idPromocion id de la promocion seleccionada por el cliente
     * @return un record co un campo de exito() el cual me dice si fue correcta o no la desactivacion,
     * un mensaje informativo.
     */
    public RespuestaControlador<Boolean> desactivarPromocion(Date fechaActual, String idPromocion){

        if(fechaActual == null) return new RespuestaControlador<>(false, "fecha nula", null);

        if(idPromocion == null || idPromocion.isEmpty()) return new RespuestaControlador<>(false, "id nulo o vacio", null);

        boolean exito = promocionDAO.desactivarPromocion(fechaActual, idPromocion);

        return exito
                ? new RespuestaControlador<>(true, "Promocion desactivada correctamente", null)
                : new RespuestaControlador<>(false, "Promocion no desactivada", null);
    }

    /**
     * metodo para aplicarle una promocion personalizada al cliente
     * @param idClienteSeleccionado id del cliente seleccionado
     * @param promocionesDTO objeto que representa los datos para aplicar la promocion como lo son (fechaInicio, fechaFin, descuento...)
     * @return un record co un campo de exito() el cual me dice si fue correcto o no al aplicar la promocion,
     * un mensaje informativo.
     */
    public RespuestaControlador<Boolean> aplicarPromocionCliente(String idClienteSeleccionado, PromocionesDTO.PromocionessDTO promocionesDTO){

        RespuestaControlador<Boolean> respuestaControlador = validarCampos(promocionesDTO);

        if(!respuestaControlador.exito()) return respuestaControlador;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Promociones promociones;
        try{
            promociones = new Promociones(promocionesDTO.id(), promocionesDTO.nombre(),
                    Double.valueOf(promocionesDTO.descuento()), simpleDateFormat.parse(promocionesDTO.fechaInicio()),
                    simpleDateFormat.parse(promocionesDTO.fechaInicio()), Promociones.TipoPromocion.valueOf(promocionesDTO.tipo().toUpperCase())
            );
        }
        catch (Exception e){
            return new RespuestaControlador<>(false, "Error al tratar de realizar algun tipo de conversion", null);
        }

        if(!promocionDAO.agregarPromocionPersonalizada(promociones)) return new RespuestaControlador<>(false, "Error al tratar de insertar en promociones", null);

        if(!promocionDAO.agregarPromocionCliente(new PromocionesDTO.PromocionClienteDTO(UUID.randomUUID().toString(),
                promociones.getId(), idClienteSeleccionado)
        )) return new RespuestaControlador<>(false, "No se pudo aplicar la promocion al cliente especifico", null);

        return new RespuestaControlador<>(true, "promocion agregada al cliente con exito", null);
    }

    /**
     * metodo que un resumen de las compras realizadas por el cliente
     * @return un record con un campo exito() que me dice si fue correcta o no la consulta del resume de las compras,
     * un mensaje informativo y por ultimo una lista con todos los clientes con su resumen de compras
     */
    public RespuestaControlador<ArrayList<PromocionesDTO.PromocioPersonalizadaCliente>> obtenerResumeComprasClientes(){

        ArrayList<PromocionesDTO.PromocioPersonalizadaCliente> listaPromociones = promocionDAO.obtenerResumeComprasCliente();

        return !listaPromociones.isEmpty()
                ? new RespuestaControlador<>(true, "Lista generada correctamente", listaPromociones)
                : new RespuestaControlador<>(false, "Lista vacia o nula", null);
    }


    /**
     * metodo que analiza un cliente por medio de un modelo no supervisado, para la viabilidad de un descuento y una clasificacion del tipo de cliente
     * @param idCliente es el id del cliente que se quiere analizar
     * @return un record con un campo exito() que dice si fue correcto o no el analisis al cliente, un mensaje informativo y
     * un objeto con la informacion necesaria a mostrar
     * @example
     * <pre>{@code
     * AnalisisCliente cliente = new AnalisisCliente();
     * cliente.getEtiquetaNegocio() = si es VIP, Regular etc...
     * cliente.getDescuentoRecomendado() = 5, 10, 15...
     *}
     * </pre>
     */
    public RespuestaControlador<AnalisisCliente> analizarCliente(String idCliente){

        if(idCliente.isEmpty()) return new RespuestaControlador<>(false, "el id no puede estar vacio",null);

        HelperIASegmentadorClientes.agruparClientes(promocionDAO.getDatasetClientes());

        ArrayList<AnalisisCliente> listaPerfiles = HelperIASegmentadorClientes.analizarYEtiquetarClusters();

        for(AnalisisCliente cliente : listaPerfiles){
           if(cliente.getId().equals(idCliente)) return new RespuestaControlador<>(true, "Cliente encontrado y este es su analisis", cliente);
        }
       return new RespuestaControlador<>(false, "Cliente no encontrado",null);
    }

    private RespuestaControlador<Boolean> validarCampos(PromocionesDTO.PromocionessDTO promocionesDTO){

        if(promocionesDTO.descuento() == null || promocionesDTO.descuento().isEmpty()) return new RespuestaControlador<>(false, "el descuento esta vacio o en null", null);

        if(HelperValidacion.ValidarTodoNumeroDecimal(promocionesDTO.descuento()) > 0 ) return new RespuestaControlador<>(false, "Error, no tiene un formato valido", null);

        if(HelperValidacion.validarUUID(promocionesDTO.id()) > 0) return new RespuestaControlador<>(false, "el id debe tener un formato valido (UUID)", null);

        if(promocionesDTO.fechaInicio().isEmpty())return new RespuestaControlador<>(false, "La fecha esta vacia", null);

        if(promocionesDTO.fechaFin().isEmpty())return new RespuestaControlador<>(false, "La fecha esta vacia", null);

        if(promocionesDTO.tipo().isEmpty()) return new RespuestaControlador<>(false, "el tipo no debe estar vacio", null);

        if(HelperValidacion.validarTodoNombres(promocionesDTO.nombre()) > 0) return new RespuestaControlador<>(false, "El nombre no es valido", null);

        return new RespuestaControlador<>(true, "Todo esta bien con los parametros", null);
    }
}
