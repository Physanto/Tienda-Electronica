package CapaLogicaNegocio.Controlador;

import CapaDatos.Logica_Conexion.PromocionClienteOnlineCRUD;
import CapaDatos.Logica_Conexion.PromocionDAO;
import CapaDatos.Logica_Conexion.PromocionOnlineCRUD;
import CapaDatos.Logica_Conexion.PromocionProductoOnlineCRUD;
import CapaDatos.Logica_Conexion.VentaOnlineCRUD;
import CapaLogicaNegocio.DTOS.AnalisisCliente;
import CapaLogicaNegocio.DTOS.PromocionesDTO;
import CapaLogicaNegocio.Helpers.HelperGestorBD;
import CapaLogicaNegocio.Helpers.HelperIAPromociones;
import CapaLogicaNegocio.Helpers.HelperIASegmentadorClientes;
import CapaLogicaNegocio.Helpers.HelperValidacion;
import CapaLogicaNegocio.Logica_Negocio.Promocion;
import CapaLogicaNegocio.Logica_Negocio.PromocionCliente;
import CapaLogicaNegocio.Logica_Negocio.PromocionProducto;
import CapaLogicaNegocio.Logica_Negocio.Venta;
import CapaLogicaNegocio.DTOS.Promociones;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
    private PromocionOnlineCRUD promocionOnlineCRUD;
    private PromocionProductoOnlineCRUD promocionProductoOnlineCRUD;
    private PromocionClienteOnlineCRUD promocionClienteOnlineCRUD;

    public PromocionControlador() {
        this.promocionDAO = new PromocionDAO();
        this.promocionOnlineCRUD = new PromocionOnlineCRUD();
        this.promocionProductoOnlineCRUD = new PromocionProductoOnlineCRUD();
        this.promocionClienteOnlineCRUD = new PromocionClienteOnlineCRUD();
    }

    /**
     * Metodo que devuelve una lista de productos que se pueden aplicarle una
     * promocion arbitraria
     * Este es el metodo que se llama apenas el admin presiona el boton del modulo
     * de Promociones
     * 
     * @return una lista con los productos tambien un campo exit() para verificar si
     *         fue correcta la consulta o no
     *         y el mensaje descriptivo, de lo contrario retorna null si la lista
     *         esta vacia o ha habido un problema
     */
    public RespuestaControlador<ArrayList<PromocionesDTO.PromocionAplicadaDTO>> obtenerProductosPromocion() {

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
            return new RespuestaControlador<>(false, "Error en IA: No se detectó un grupo de productos estancados.",
                    null);
        }

        ArrayList<PromocionesDTO.PromocionAplicadaDTO> lista = promocionDAO.datosPromociones(); // listado de todos los
                                                                                                // productos de la bd
        // lista preparada para setear los productos que si tienen promocion
        ArrayList<PromocionesDTO.PromocionAplicadaDTO> listaProductos = new ArrayList<>();

        for (PromocionesDTO.PromocionAplicadaDTO promocionAplicadaDTO : lista) {
            for (Promocion promocion : listaPromociones) {

                if (promocionAplicadaDTO.id().equals(promocion.getId())
                        && promocion.getCluster() == idClusterEstancado) {
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
     * 
     * @param idProductoSeleccionado es el id del producto que el cliente selecciono
     *                               de la tabla para aplicarle la promocion
     * @param promocionesDTO         objeto que contiene los datos (fechaInicio,
     *                               fechaFin...) para agregar la promocion
     *                               el campo de tipo() PONER "ESPECIFICA" ya que es
     *                               para un producto en especial.
     * @return un record en el cual contiene un campo .exito() de true si aplico
     *         correctamente la promocion, un campo de mensaje
     */
    public RespuestaControlador<Boolean> aplicarPromocionProducto(String idProductoSeleccionado,
            PromocionesDTO.PromocionessDTO promocionesDTO) {

        RespuestaControlador<Boolean> respuestaControlador = validarCampos(promocionesDTO);

        if (!respuestaControlador.exito())
            return respuestaControlador;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        Promociones promociones;
        try {
            promociones = new Promociones(promocionesDTO.id(), promocionesDTO.nombre(),
                    Double.valueOf(promocionesDTO.descuento()), simpleDateFormat.parse(promocionesDTO.fechaInicio()),
                    simpleDateFormat.parse(promocionesDTO.fechaFin()),
                    Promociones.TipoPromocion.valueOf(promocionesDTO.tipo().toUpperCase()));
        } catch (Exception e) {
            return new RespuestaControlador<>(false, "Error al tratar de realizar algun tipo de conversion", null);
        }

        // Se denormaliza el idProducto al que aplica ANTES de persistir, para que viaje
        // tanto en la escritura directa a la nube como en el JSON que se encola si no
        // hay
        // internet (en local ese vinculo vive aparte, en la tabla PromocionProducto).
        promociones.setIdProducto(idProductoSeleccionado);

        // Escritura dual gestionada por HelperGestorBD: si hay internet guarda en local
        // Y
        // nube; si no hay, guarda en local y deja el INSERT en la cola de
        // sincronizacion
        // (ColaSincronizadora) para subirlo automaticamente cuando vuelva la conexion.
        boolean exito = HelperGestorBD.guardarRegistro(promociones, "Promocion", promociones.getId(),
                () -> promocionDAO.agregarPromocionPersonalizada(promociones),
                () -> promocionOnlineCRUD.registrarNube(promociones));

        if (!exito)
            return new RespuestaControlador<>(false, "Error al tratar de insertar en promociones", null);

        // El vinculo Promocion->Producto tambien se escribe en local Y nube (y se encola si
        // no hay internet) para que la coleccion PromocionProducto tenga paridad entre ambas
        // bases. Se usa el MISMO id en el DTO local y la entidad de nube.
        String idRelacion = UUID.randomUUID().toString();
        PromocionesDTO.PromocionProductoDTO relacionDTO = new PromocionesDTO.PromocionProductoDTO(idRelacion,
                promociones.getId(), idProductoSeleccionado);
        PromocionProducto relacion = new PromocionProducto(idRelacion, promociones.getId(), idProductoSeleccionado);

        boolean exitoRelacion = HelperGestorBD.guardarRegistro(relacion, "PromocionProducto", idRelacion,
                () -> promocionDAO.agregarPromocionProducto(relacionDTO),
                () -> promocionProductoOnlineCRUD.registrarNube(relacion));

        if (!exitoRelacion)
            return new RespuestaControlador<>(false, "No se pudo aplicar la promocion al producto especifico", null);

        return new RespuestaControlador<>(true, "promocion agregada al producto con exito", null);
    }

    /**
     * metodo para obtener todas las promociones que estan activas en el sistema
     * 
     * @return un record con un campo de exito() para verificar si fue correcta la
     *         operacion o no,
     *         un mensaje informativo y por ultimo la lista de las promociones
     *         activas.
     */
    public RespuestaControlador<ArrayList<Promociones>> obtenerPromocionesActivas() {

        ArrayList<Promociones> listaPromociones = promocionDAO.obtenerPromocionesPersonalizadas();

        return !listaPromociones.isEmpty()
                ? new RespuestaControlador<>(true, "Mostrando promociones", listaPromociones)
                : new RespuestaControlador<>(false, "Lista vacia", null);
    }

    /**
     * Desactiva las promociones cuya fechaFin ya paso. Para que la desactivacion sea GLOBAL
     * (no dependa de la maquina) se recorren primero las promociones de la NUBE, que es la
     * fuente compartida entre todos los equipos: asi cualquier PC que entre al modulo desactiva
     * las vencidas para todos, no solo las de su BD local. Tambien se recorren las locales como
     * respaldo (por si no hay nube o existen solo en local). Cada promo se procesa una sola vez.
     * Pensado para ejecutarse al entrar al modulo de Promociones.
     *
     * @return numero de promociones desactivadas automaticamente
     */
    public int desactivarVencidas() {
        Date ahora = new Date();
        int desactivadas = 0;
        java.util.Set<String> procesados = new java.util.HashSet<>();

        // 1) Fuente compartida: promociones de la nube -> desactivacion global.
        ArrayList<Promociones> promocionesNube = promocionOnlineCRUD.obtenersNube(Promociones.class);
        if (promocionesNube != null) {
            for (Promociones p : promocionesNube) {
                if (estaVencida(p, ahora) && procesados.add(p.getId())) {
                    boolean cloudOk = promocionOnlineCRUD.desactivarNube(p.getId(), ahora);
                    boolean localOk = promocionDAO.desactivarPromocion(ahora, p.getId()); // no-op si no esta en local
                    if (cloudOk || localOk)
                        desactivadas++;
                }
            }
        }

        // 2) Respaldo: promociones locales (sin conexion o que solo existen en local).
        for (Promociones p : promocionDAO.obtenerPromocionesPersonalizadas()) {
            if (estaVencida(p, ahora) && procesados.add(p.getId())) {
                boolean localOk = promocionDAO.desactivarPromocion(ahora, p.getId());
                boolean cloudOk = promocionOnlineCRUD.desactivarNube(p.getId(), ahora);
                if (localOk || cloudOk)
                    desactivadas++;
            }
        }
        return desactivadas;
    }

    /** Indica si la promo tiene id valido y su fechaFin ya quedo en el pasado. */
    private boolean estaVencida(Promociones p, Date ahora) {
        return p != null && p.getId() != null && p.getFechaFin() != null && ahora.after(p.getFechaFin());
    }

    /**
     * metodo que desactiva la promocion que ha sido seleccionada por el usuario
     *
     * @param fechaActual es la fecha actual para que pueda desactivarse la
     *                    promocion, campo que se va quitar en futuras mejoras
     * @param idPromocion id de la promocion seleccionada por el cliente
     * @return un record co un campo de exito() el cual me dice si fue correcta o no
     *         la desactivacion,
     *         un mensaje informativo.
     */
    public RespuestaControlador<Boolean> desactivarPromocion(Date fechaActual, String idPromocion) {

        if (fechaActual == null)
            return new RespuestaControlador<>(false, "fecha nula", null);

        if (idPromocion == null || idPromocion.isEmpty())
            return new RespuestaControlador<>(false, "id nulo o vacio", null);

        // Intentar desactivar en local y en nube de forma independiente.
        // Si la promo solo existe en nube (por ejemplo, creada desde otra sesión), la
        // desactivación
        // en local retorna 0 filas pero la de nube puede tener éxito — y es lo que
        // importa al cliente.
        boolean localOk = promocionDAO.desactivarPromocion(fechaActual, idPromocion);
        boolean cloudOk = promocionOnlineCRUD.desactivarNube(idPromocion, fechaActual);

        boolean exito = localOk || cloudOk;
        return exito
                ? new RespuestaControlador<>(true, "Promocion desactivada correctamente", null)
                : new RespuestaControlador<>(false, "Promocion no desactivada", null);
    }

    /**
     * metodo para aplicarle una promocion personalizada al cliente
     * 
     * @param idClienteSeleccionado id del cliente seleccionado
     * @param promocionesDTO        objeto que representa los datos para aplicar la
     *                              promocion como lo son (fechaInicio, fechaFin,
     *                              descuento...)
     * @return un record co un campo de exito() el cual me dice si fue correcto o no
     *         al aplicar la promocion,
     *         un mensaje informativo.
     */
    public RespuestaControlador<Boolean> aplicarPromocionCliente(String idClienteSeleccionado,
            PromocionesDTO.PromocionessDTO promocionesDTO) {

        RespuestaControlador<Boolean> respuestaControlador = validarCampos(promocionesDTO);

        if (!respuestaControlador.exito())
            return respuestaControlador;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        Promociones promociones;
        try {
            promociones = new Promociones(promocionesDTO.id(), promocionesDTO.nombre(),
                    Double.valueOf(promocionesDTO.descuento()), simpleDateFormat.parse(promocionesDTO.fechaInicio()),
                    simpleDateFormat.parse(promocionesDTO.fechaFin()),
                    Promociones.TipoPromocion.valueOf(promocionesDTO.tipo().toUpperCase()));
        } catch (Exception e) {
            return new RespuestaControlador<>(false, "Error al tratar de realizar algun tipo de conversion", null);
        }

        // Se denormaliza el idCliente al que aplica ANTES de persistir, para que viaje
        // tanto en la escritura directa a la nube como en el JSON que se encola si no
        // hay
        // internet (en local ese vinculo vive aparte, en la tabla PromocionCliente).
        promociones.setIdCliente(idClienteSeleccionado);

        // Escritura dual gestionada por HelperGestorBD: si hay internet guarda en local
        // Y
        // nube; si no hay, guarda en local y deja el INSERT en la cola de
        // sincronizacion
        // (ColaSincronizadora) para subirlo automaticamente cuando vuelva la conexion.
        boolean exito = HelperGestorBD.guardarRegistro(promociones, "Promocion", promociones.getId(),
                () -> promocionDAO.agregarPromocionPersonalizada(promociones),
                () -> promocionOnlineCRUD.registrarNube(promociones));

        if (!exito)
            return new RespuestaControlador<>(false, "Error al tratar de insertar en promociones", null);

        // El vinculo Promocion->Cliente tambien se escribe en local Y nube (y se encola si
        // no hay internet) para que la coleccion PromocionCliente tenga paridad entre ambas
        // bases. Se usa el MISMO id en el DTO local y la entidad de nube.
        String idRelacion = UUID.randomUUID().toString();
        PromocionesDTO.PromocionClienteDTO relacionDTO = new PromocionesDTO.PromocionClienteDTO(idRelacion,
                promociones.getId(), idClienteSeleccionado);
        PromocionCliente relacion = new PromocionCliente(idRelacion, promociones.getId(), idClienteSeleccionado);

        boolean exitoRelacion = HelperGestorBD.guardarRegistro(relacion, "PromocionCliente", idRelacion,
                () -> promocionDAO.agregarPromocionCliente(relacionDTO),
                () -> promocionClienteOnlineCRUD.registrarNube(relacion));

        if (!exitoRelacion)
            return new RespuestaControlador<>(false, "No se pudo aplicar la promocion al cliente especifico", null);

        return new RespuestaControlador<>(true, "promocion agregada al cliente con exito", null);
    }

    /**
     * metodo que un resumen de las compras realizadas por el cliente
     * 
     * @return un record con un campo exito() que me dice si fue correcta o no la
     *         consulta del resume de las compras,
     *         un mensaje informativo y por ultimo una lista con todos los clientes
     *         con su resumen de compras
     */
    public RespuestaControlador<ArrayList<PromocionesDTO.PromocioPersonalizadaCliente>> obtenerResumeComprasClientes() {

        ArrayList<PromocionesDTO.PromocioPersonalizadaCliente> listaLocal = promocionDAO.obtenerResumeComprasCliente();

        // Las compras del cliente van solo a la nube; enriquecer con ventas cloud para
        // mostrar
        // totalCompras y diasUltCompra reales aunque la BD local no tenga esas ventas.
        ArrayList<PromocionesDTO.PromocioPersonalizadaCliente> enriquecida = enriquecerConVentasNube(listaLocal);

        return !enriquecida.isEmpty()
                ? new RespuestaControlador<>(true, "Lista generada correctamente", enriquecida)
                : new RespuestaControlador<>(false, "Lista vacia o nula", null);
    }

    private ArrayList<PromocionesDTO.PromocioPersonalizadaCliente> enriquecerConVentasNube(
            ArrayList<PromocionesDTO.PromocioPersonalizadaCliente> listaLocal) {
        try {
            ArrayList<Venta> ventasNube = new VentaOnlineCRUD().obtenersNube(Venta.class);
            if (ventasNube == null || ventasNube.isEmpty())
                return listaLocal;

            Map<String, Double> totalPorCliente = new HashMap<>();
            Map<String, Date> ultimaVentaPorCliente = new HashMap<>();

            for (Venta v : ventasNube) {
                if (v.getIdCliente() == null)
                    continue;
                String idC = v.getIdCliente();
                double total = v.getTotalVenta() != null ? v.getTotalVenta() : 0.0;
                totalPorCliente.merge(idC, total, Double::sum);
                Date fechaV = v.getFechaVenta();
                if (fechaV != null) {
                    ultimaVentaPorCliente.merge(idC, fechaV, (a, b) -> a.after(b) ? a : b);
                }
            }

            Date ahora = new Date();
            ArrayList<PromocionesDTO.PromocioPersonalizadaCliente> resultado = new ArrayList<>();
            for (PromocionesDTO.PromocioPersonalizadaCliente c : listaLocal) {
                Double totalCloud = totalPorCliente.get(c.id());
                Date ultimaCompra = ultimaVentaPorCliente.get(c.id());

                String totalStr = (totalCloud != null && totalCloud > 0)
                        ? String.format("%.2f", totalCloud)
                        : c.totalCompras();
                String diasStr = c.ultCompra();
                if (ultimaCompra != null) {
                    long dias = (ahora.getTime() - ultimaCompra.getTime()) / (1000L * 60 * 60 * 24);
                    diasStr = String.valueOf(dias);
                }

                resultado.add(new PromocionesDTO.PromocioPersonalizadaCliente(
                        c.id(), c.nombre(), c.apellido(), c.cedula(), totalStr, diasStr));
            }
            return resultado;

        } catch (Exception e) {
            System.out.println("Error enriqueciendo clientes con nube: " + e.getMessage());
            return listaLocal;
        }
    }

    /**
     * metodo que analiza un cliente por medio de un modelo no supervisado, para la
     * viabilidad de un descuento y una clasificacion del tipo de cliente
     * 
     * @param idCliente es el id del cliente que se quiere analizar
     * @return un record con un campo exito() que dice si fue correcto o no el
     *         analisis al cliente, un mensaje informativo y
     *         un objeto con la informacion necesaria a mostrar
     * @example
     * 
     *          <pre>{@code
     * AnalisisCliente cliente = new AnalisisCliente();
     * cliente.getEtiquetaNegocio() = si es VIP, Regular etc...
     * cliente.getDescuentoRecomendado() = 5, 10, 15...
     *}
     * </pre>
     */
    public RespuestaControlador<AnalisisCliente> analizarCliente(String idCliente) {

        if (idCliente.isEmpty())
            return new RespuestaControlador<>(false, "el id no puede estar vacio", null);

        ArrayList<AnalisisCliente> dataset = promocionDAO.getDatasetClientes();

        if (dataset.isEmpty())
            return new RespuestaControlador<>(false, "No hay datos de compras suficientes para analizar", null);

        // agruparClientes asigna el numero de cluster a cada elemento de dataset (por
        // referencia)
        HelperIASegmentadorClientes.agruparClientes(dataset);

        // listaPerfiles son los CENTROIDES etiquetados (VIP, Regular...), con cluster =
        // 0/1/2 como id
        ArrayList<AnalisisCliente> listaPerfiles = HelperIASegmentadorClientes.analizarYEtiquetarClusters();

        // 1. Buscar el cliente en el dataset (que ya tiene su cluster asignado)
        AnalisisCliente clienteConCluster = null;
        for (AnalisisCliente c : dataset) {
            if (c.getId() != null && c.getId().equals(idCliente)) {
                clienteConCluster = c;
                break;
            }
        }
        if (clienteConCluster == null)
            return new RespuestaControlador<>(false, "El cliente no tiene historial de compras para analizar", null);

        // 2. Buscar el perfil del cluster al que pertenece y copiarle la
        // etiqueta/descuento
        for (AnalisisCliente perfil : listaPerfiles) {
            if (perfil.getCluster() == clienteConCluster.getCluster()) {
                clienteConCluster.setEtiquetaNegocio(perfil.getEtiquetaNegocio());
                clienteConCluster.setDescuentoRecomendado(perfil.getDescuentoRecomendado());
                return new RespuestaControlador<>(true, "Cliente analizado correctamente", clienteConCluster);
            }
        }
        return new RespuestaControlador<>(false, "No se pudo determinar el perfil del cluster", null);
    }

    private RespuestaControlador<Boolean> validarCampos(PromocionesDTO.PromocionessDTO promocionesDTO) {

        if (promocionesDTO.descuento() == null || promocionesDTO.descuento().isEmpty())
            return new RespuestaControlador<>(false, "el descuento esta vacio o en null", null);

        if (HelperValidacion.ValidarTodoNumeroDecimal(promocionesDTO.descuento()) > 0)
            return new RespuestaControlador<>(false, "Error, no tiene un formato valido", null);

        if (HelperValidacion.validarUUID(promocionesDTO.id()) > 0)
            return new RespuestaControlador<>(false, "el id debe tener un formato valido (UUID)", null);

        if (promocionesDTO.fechaInicio().isEmpty())
            return new RespuestaControlador<>(false, "La fecha esta vacia", null);

        if (promocionesDTO.fechaFin().isEmpty())
            return new RespuestaControlador<>(false, "La fecha esta vacia", null);

        if (promocionesDTO.tipo().isEmpty())
            return new RespuestaControlador<>(false, "el tipo no debe estar vacio", null);

        if (HelperValidacion.validarTodoNombres(promocionesDTO.nombre()) > 0)
            return new RespuestaControlador<>(false, "El nombre no es valido", null);

        return new RespuestaControlador<>(true, "Todo esta bien con los parametros", null);
    }
}
