package CapaLogicaNegocio.Controlador;

import CapaDatos.Logica_Conexion.PromocionOnlineCRUD;
import CapaLogicaNegocio.DTOS.Promociones;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Clase Controladora del modulo Cliente encargada de exponer las promociones.
 * A diferencia de {@link PromocionControlador} (que opera contra la base local y la IA del Admin),
 * este controlador consulta UNICAMENTE la base de datos de la nube, ya que el cliente solo opera en
 * linea y nunca toca la base local de la tienda. CADA COSA del negocio debe pasar por el controlador,
 * la vista solo conoce el controlador. nada mas.
 *
 * <p>En la nube cada promocion trae denormalizado a quien aplica: {@code idProducto} (oferta sobre un
 * producto) o {@code idCliente} (promocion personalizada de un cliente). Con eso el cliente puede
 * ver y aplicar las promociones sin tocar las tablas relacionales locales.</p>
 *
 * @author Manuel Figueroa (Physanto)
 */
public class PromocionClienteControlador {

    private PromocionOnlineCRUD promocionOnlineCRUD;

    public PromocionClienteControlador() {
        this.promocionOnlineCRUD = new PromocionOnlineCRUD();
    }

    /**
     * Lista las promociones que estan vigentes (segun su rango de fechas) directamente desde la nube.
     * Una promocion se considera vigente si la fecha actual esta dentro de [fechaInicio, fechaFin];
     * las fechas nulas se interpretan como "sin limite" por ese extremo.
     *
     * @return un record RespuestaControlador que, de ser exitoso, contiene en su propiedad .dato()
     *         un ArrayList con las promociones vigentes.
     */
    public RespuestaControlador<ArrayList<Promociones>> listarPromocionesActivas() {

        ArrayList<Promociones> activas = obtenerVigentes();

        if (activas == null) return new RespuestaControlador<>(false, "No se pudo conectar con la nube", null);

        return !activas.isEmpty()
                ? new RespuestaControlador<>(true, "Promociones activas cargadas correctamente", activas)
                : new RespuestaControlador<>(false, "No hay promociones activas en este momento", activas);
    }

    /**
     * Lista solo las promociones vigentes que aplican a un producto del catalogo (ofertas por producto).
     * @return un record RespuestaControlador con las ofertas por producto en su propiedad .dato()
     */
    public RespuestaControlador<ArrayList<Promociones>> listarPromocionesProducto() {

        ArrayList<Promociones> activas = obtenerVigentes();

        if (activas == null) return new RespuestaControlador<>(false, "No se pudo conectar con la nube", null);

        ArrayList<Promociones> ofertas = new ArrayList<>();
        for (Promociones promocion : activas) {
            if (promocion.getIdProducto() != null && !promocion.getIdProducto().isEmpty()) ofertas.add(promocion);
        }

        return !ofertas.isEmpty()
                ? new RespuestaControlador<>(true, "Ofertas por producto cargadas correctamente", ofertas)
                : new RespuestaControlador<>(false, "No hay ofertas por producto en este momento", ofertas);
    }

    /**
     * Lista las promociones vigentes personalizadas para el cliente indicado (promocion por cliente).
     * @param idCliente id del cliente con la sesion activa
     * @return un record RespuestaControlador con las promociones del cliente en su propiedad .dato()
     */
    public RespuestaControlador<ArrayList<Promociones>> listarPromocionesCliente(String idCliente) {

        if (idCliente == null || idCliente.isEmpty())
            return new RespuestaControlador<>(false, "El id del cliente es obligatorio", null);

        ArrayList<Promociones> activas = obtenerVigentes();

        if (activas == null) return new RespuestaControlador<>(false, "No se pudo conectar con la nube", null);

        ArrayList<Promociones> personales = new ArrayList<>();
        for (Promociones promocion : activas) {
            if (idCliente.equals(promocion.getIdCliente())) personales.add(promocion);
        }

        return !personales.isEmpty()
                ? new RespuestaControlador<>(true, "Promociones del cliente cargadas correctamente", personales)
                : new RespuestaControlador<>(false, "No tienes promociones personalizadas activas", personales);
    }

    /**
     * Devuelve, por cada producto que tenga una oferta vigente, el mejor porcentaje de descuento.
     * Lo usa el catalogo del cliente para mostrar y cobrar el precio con descuento.
     *
     * @return un record RespuestaControlador cuyo .dato() es un Map idProducto -> descuento (%).
     *         Si no hay nube o no hay ofertas, el mapa va vacio (no es un error de cara a la vista).
     */
    public RespuestaControlador<HashMap<String, Double>> obtenerDescuentosPorProducto() {

        HashMap<String, Double> descuentos = new HashMap<>();
        ArrayList<Promociones> activas = obtenerVigentes();

        if (activas == null) return new RespuestaControlador<>(false, "No se pudo conectar con la nube", descuentos);

        for (Promociones promocion : activas) {
            String idProducto = promocion.getIdProducto();
            Double descuento = promocion.getDescuento();
            if (idProducto == null || idProducto.isEmpty() || descuento == null || descuento <= 0) continue;

            Double actual = descuentos.get(idProducto);
            if (actual == null || descuento > actual) descuentos.put(idProducto, descuento);
        }

        return new RespuestaControlador<>(true, "Descuentos por producto vigentes", descuentos);
    }

    /**
     * Trae las promociones de la nube y deja solo las vigentes para la fecha actual.
     * @return la lista de promociones vigentes, o null si no se pudo leer la nube.
     */
    private ArrayList<Promociones> obtenerVigentes() {
        ArrayList<Promociones> promociones = promocionOnlineCRUD.obtenersNube(Promociones.class);

        if (promociones == null) return null;

        Date ahora = new Date();
        ArrayList<Promociones> activas = new ArrayList<>();
        for (Promociones promocion : promociones) {
            if (estaVigente(promocion, ahora)) activas.add(promocion);
        }
        return activas;
    }

    /**
     * Determina si una promocion esta vigente para la fecha indicada.
     */
    private boolean estaVigente(Promociones promocion, Date ahora) {
        if (promocion == null) return false;
        if (promocion.getFechaInicio() != null && ahora.before(promocion.getFechaInicio())) return false;
        if (promocion.getFechaFin() != null && ahora.after(promocion.getFechaFin())) return false;
        return true;
    }
}
