package CapaLogicaNegocio.DTOS;

/**
 * record encargado de modelar un objeto producto generico para la vista.
 * Se usa solo para transportar los datos para las validaciones
 *
 * @author Manuel Figueroa (Physanto)
 */
public record ProductoDTO(String id, String codigo, String nombre, String marca, String serie, String stock,
        String precioActual, String fechaVencimiento, String idCategoria) {
}
