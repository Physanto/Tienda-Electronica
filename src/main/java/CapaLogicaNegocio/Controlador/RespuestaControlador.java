package CapaLogicaNegocio.Controlador;

/**
 * Este record es el objeto que devuelve el controlador para que la vista se entere de que fue lo que paso
 * con lo que ella mando a consultar al modelo.
 *
 * @param exito si la respuesta es true o false
 * @param mensaje el mensaje que se quiere almacenar
 * @param objeto si se quiere enviar un objeto con informacion mas detallada
 * @param <T> generico de cualquier tipo de objeto
 */
public record RespuestaControlador <T>(boolean exito, String mensaje, T objeto){ }
