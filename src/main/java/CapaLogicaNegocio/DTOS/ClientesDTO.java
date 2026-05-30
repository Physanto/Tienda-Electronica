package CapaLogicaNegocio.DTOS;


public class ClientesDTO {
    /**
     * record encargado de modelar un objeto cliente generico para la vista.
     * Se usa solo para transportar los datos para las validaciones
     *
     * @author Manuel Figueroa (Physanto)
     */
    public record ClienteDTO(String id, String nombre, String apellido, String cedula, String direccion) { }


}
