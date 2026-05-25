package CapaLogicaNegocio.DTOS;

/**
 * record encargado de modelar un objeto promocion generico para la vista.
 * Se usa solo para transportar los datos para las validaciones
 *
 * @author Manuel Figueroa (Physanto)
 */
public record PromocionDTO(String id, String stockActual, String diasSinVender, String totalVendido) {}
