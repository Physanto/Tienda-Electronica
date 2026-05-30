package CapaLogicaNegocio.DTOS;

/**
 * Clase DTO que se encarga de encapsular los record que van a transportar la informacion
 * y que sirven para la comunicacion modelo-controlador-vista
 *
 * @author Manuel Figueroa (Physanto)
 */
public class ClientesDTO {

    /**
     * record para el transporte de la comunicacion entre modelo-controlador-vista
     * es para el ingreso de un cliente a la base de datos
     * @param id id del cliente
     * @param nombre nombre del cliente
     * @param apellido apellido del cliente
     * @param cedula cedula del cliente
     * @param direccion direccion del cliente
     */
    public record ClienteDTO(String id, String nombre, String apellido, String cedula, String direccion) { }

    public record ClienteAnalisis(String id, String diaUltCompra, String cantCompras, String dineroGastado) { }
}
