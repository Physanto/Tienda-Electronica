package CapaLogicaNegocio.DTOS;

import CapaLogicaNegocio.Logica_Negocio.Venta;

/**
 * record encargado de modelar un objeto venta generico para la vista.
 * Se usa solo para transportar los datos para las validaciones
 *
 * @author Manuel Figueroa (Physanto)
 */
public record VentaDTO(String id, String fechaVenta, String totalVenta, Venta.MetodoPago metodoPago, String idCliente) {}
