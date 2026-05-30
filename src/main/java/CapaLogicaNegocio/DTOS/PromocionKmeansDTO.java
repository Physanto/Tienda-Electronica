package CapaLogicaNegocio.DTOS;

/**
 * record encargado de modelar un objeto promocion generico para la vista.
 * Se usa solo para transportar los datos para las validaciones
 *
 * @author Manuel Figueroa (Physanto)
 */
public record PromocionKmeansDTO(String id, Double stockActual, Double diasSinVender, Double totalVendido) {}
