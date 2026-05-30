package CapaLogicaNegocio.DTOS;


/**
 * Clase que encapsule los record para el transporte de informacion entre la vista-controlador-modelo
 *
 * @author Manuel Figueroa (Physanto)
 */
public class PromocionesDTO {

    /**
     * Record para la transferencia de datos de la vista al controlador de manera segura
     * @param id id que se genera con UUID, este se debe generar manualmente
     * @param nombre nombre de la promocion que el cliente le va poner
     * @param descuento el descuento que le aplica el cliente en porcentaje
     * @param fechaInicio fecha en la que inicia el descuento en formato (dd/MM/yyyy)
     * @param fechaFin fecha en la cual va durar la promocion de ese producto
     * @param tipo Si es un promocion GENERAL o ESPECIFICA
     */
    public record PromocionessDTO(String id, String nombre, String descuento, String fechaInicio, String fechaFin, String tipo){ }

    /**
     * Record que modela el reporte para la aplicacion de una promocion en el modulo de promociones
     * @param id id del producto
     * @param nombre nombre del producto
     * @param marca marca del producto
     * @param Stock stock del producto
     * @param PrecioActual precio del producto
     * @param diasSinVenta cantidad de dias que no se vende ese producto
     * @param totalVendido cantidad de unidades totales que se han vendido en la vida del sistema
     */
    public record PromocionAplicadaDTO(String id, String nombre, String marca, String Stock, String PrecioActual, String diasSinVenta, String totalVendido) {}

    public record PromocionClienteDTO(String id, String idPromocion, String idCliente){ }

    public record PromocionKmeansDTO(String id, Double stockActual, Double diasSinVender, Double totalVendido) {}

    public record PromocionProductoDTO(String id, String idPromocion, String idProducto){ }

    /**
     * Record que sirve para modelar un reporte en el modulo de promociones
     * @param id id del cliente
     * @param nombre nombre del cliente
     * @param apellido apellido del cliente
     * @param cedula cedula del cliente
     * @param totalCompras el dinero total de las compras que ha realizado en el sistema
     * @param ultCompra cantidad de dias donde fue su ultima compra
     */
    public record PromocioPersonalizadaCliente(String id, String nombre, String apellido, String cedula, String totalCompras, String ultCompra){ }
}

