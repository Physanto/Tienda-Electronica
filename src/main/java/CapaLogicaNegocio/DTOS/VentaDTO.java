package CapaLogicaNegocio.DTOS;

/**
 * record encargado de modelar un objeto venta generico para la vista.
 * Se usa solo para transportar los datos para las validaciones
 *
 * @author Manuel Figueroa (Physanto)
 */
public record VentaDTO(String id, String fechaVenta, String totalVenta, String metodoPago, String idCliente) {}
