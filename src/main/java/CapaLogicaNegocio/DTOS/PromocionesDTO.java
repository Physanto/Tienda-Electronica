package CapaLogicaNegocio.DTOS;

/**
 * Record para la transferencia de datos de la vista al controlador de manera segura
 * @param id id que se genera con UUID, este se debe generar manualmente
 * @param nombre nombre de la promocion que el cliente le va poner
 * @param descuento el descuento que le aplica el cliente en porcentaje
 * @param fechaInicio fecha en la que inicia el descuento en formato (dd/MM/yyyy)
 * @param fechaFin fecha en la cual va durar la promocion de ese producto
 * @param tipo Si es un promocion GENERAL o ESPECIFICA
 */
public record PromocionesDTO(String id, String nombre, String descuento, String fechaInicio, String fechaFin, String tipo){
}
