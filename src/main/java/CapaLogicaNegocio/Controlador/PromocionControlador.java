package CapaLogicaNegocio.Controlador;

import CapaDatos.Logica_Conexion.PromocionDAO;
import CapaLogicaNegocio.DTOS.PromocionesDTO;
import CapaLogicaNegocio.Helpers.HelperIAPromociones;
import CapaLogicaNegocio.Helpers.HelperValidacion;
import CapaLogicaNegocio.Logica_Negocio.Promocion;
import CapaLogicaNegocio.Logica_Negocio.Promociones;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Date;

public class PromocionControlador {

    private PromocionDAO promocionDAO;

    public PromocionControlador(){
        this.promocionDAO = new PromocionDAO();
    }

    /**
     * Metodo que devuelve una lista de productos que se pueden aplicarle una promocion arbitraria
     * Este es el metodo que se llama apenas el admin presiona el boton del modulo de Promociones
     * @return una lista con los productos, retorna null si la lista esta vacia o ha habido un problema
     */
    public RespuestaControlador<ArrayList<PromocionesDTO.PromocionAplicadaDTO>> obtenerProductosPromocion(){

        ArrayList<Promocion> listaPromociones = HelperIAPromociones.agruparProductos(promocionDAO.getDataset());

        ArrayList<PromocionesDTO.PromocionAplicadaDTO> lista = promocionDAO.datosPromociones();
        ArrayList<PromocionesDTO.PromocionAplicadaDTO> listaProductos = new ArrayList<>();

        for (PromocionesDTO.PromocionAplicadaDTO promocionAplicadaDTO : lista) {
            for (Promocion promocion : listaPromociones) {

                if (promocionAplicadaDTO.id().equals(promocion.getId()) && promocion.getCluster() == 0) {
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

    public RespuestaControlador<ArrayList<Promociones>> obtenerPromocionesActivas(){

        ArrayList<Promociones> listaPromociones = promocionDAO.obtenerPromocionesPersonalizadas();

        return listaPromociones.isEmpty()
                ? new RespuestaControlador<>(true, "Mostrando promociones",listaPromociones)
                : new RespuestaControlador<>(false, "Lista vacia", null);
    }

    public RespuestaControlador<Boolean> desactivarPromocion(Date fechaActual, String idPromocion){

        if(fechaActual == null) return new RespuestaControlador<>(false, "fecha nula", null);

        if(idPromocion == null || idPromocion.isEmpty()) return new RespuestaControlador<>(false, "id nulo o vacio", null);

        boolean exito = promocionDAO.desactivarPromocion(fechaActual, idPromocion);

        return exito
                ? new RespuestaControlador<>(true, "Promocion desactivada correctamente", null)
                : new RespuestaControlador<>(false, "Promocion no desactivada", null);
    }

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

    public RespuestaControlador<ArrayList<PromocionesDTO.PromocioPersonalizadaCliente>> obtenerResumeComprasClientes(){

        ArrayList<PromocionesDTO.PromocioPersonalizadaCliente> listaPromociones = promocionDAO.obtenerResumeComprasCliente();

        return !listaPromociones.isEmpty()
                ? new RespuestaControlador<>(true, "Lista generada correctamente", listaPromociones)
                : new RespuestaControlador<>(false, "Lista vacia o nula", null);
    }

    public RespuestaControlador<> analizarCliente(){

    }
    public RespuestaControlador<Boolean> validarCampos(PromocionesDTO.PromocionessDTO promocionesDTO){

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
