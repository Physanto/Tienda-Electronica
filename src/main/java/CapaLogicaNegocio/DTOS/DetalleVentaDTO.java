package CapaLogicaNegocio.DTOS;

/**
 * record encargado de modelar un objeto detalleVenta generico para la vista.
 * Se usa solo para transportar los datos para las validaciones
 *
 * @author Manuel Figueroa (Physanto)
 */
public record DetalleVentaDTO(String id, String cantidad, String subTotal, String precioVenta, String idProducto, String idVenta) {}
