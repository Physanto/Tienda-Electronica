package CapaLogicaNegocio.Helpers;

import CapaDatos.Logica_Conexion.SincronizadoraDAO;
import CapaDatos.Logica_Conexion.GeneralOnlineProviderCRUD;
import CapaLogicaNegocio.Logica_Negocio.Sincronizadora;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 * Clase encargada de Sincronizar la informacion de las dos bases de datos
 * existentes, esto
 * para la implementacion de un metodo de redundancia optimo
 *
 * @author Manuel Escobar (Physanto)
 */
public class HelperSincronizador {

    // Formato ISO-8601 usado para serializar las fechas en la cola de
    // sincronizacion.
    // Es clave que el encolado (HelperGestorBD) y el vaciado (este metodo) usen
    // EXACTAMENTE
    // el mismo formato para que las fechas hagan round-trip y se puedan reconstruir
    // como Date
    // (y por ende como Timestamp en Firestore) al subirlas.
    private static final String FORMATO_FECHA_SYNC = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /**
     * Devuelve la instancia de Gson que se debe usar tanto para encolar como para
     * vaciar la cola
     * de sincronizacion. Serializa las fechas en {@link #FORMATO_FECHA_SYNC} en
     * lugar del formato
     * local por defecto, para que puedan reconstruirse al reproducir la cola.
     */
    public static Gson gsonSync() {
        return new GsonBuilder().setDateFormat(FORMATO_FECHA_SYNC).create();
    }

    /**
     * Metodo que se encarga de sincronizar la base de datos local a la nube
     * este metodo te hace toda la sincronizacion independientemente de si
     * insertas, eliminas o actualizas un registro
     */
    public static void SincronizarLocalANube() {
        System.out.println("Iniciando vaciado de cola local hacia la nube...");

        Gson gson = gsonSync();
        SincronizadoraDAO sincronizadoraDAO = new SincronizadoraDAO();
        ArrayList<Sincronizadora> datosPendientes = sincronizadoraDAO.obtenerNoSincronizados("0");

        if (datosPendientes.isEmpty()) {
            System.out.println("No hay datos pendientes por subir");
            return;
        }

        for (Sincronizadora sincronizadora : datosPendientes) {
            try {
                String registroJson = sincronizadora.getRegistroJson();
                String tablaAfectada = sincronizadora.getTablaAfectada();
                String idRegistro = sincronizadora.getIdRegistroAfectado();
                Map<String, Object> datos = gson.fromJson(registroJson, Map.class);

                // Las fechas viajan como texto ISO dentro del JSON; se reconstruyen a Date para
                // que
                // Firestore las almacene como Timestamp (y no como String, que rompia la
                // lectura del
                // Cliente con toObject). Solo aplica a payloads con datos (INSERT/UPDATE).
                if (datos != null)
                    convertirCadenasFechaADate(datos);

                boolean subidoExitosamente;
                switch (sincronizadora.getAccion().name()) {
                    case "INSERT":
                        subidoExitosamente = GeneralOnlineProviderCRUD.guardar(tablaAfectada, idRegistro, datos);
                        break;
                    case "UPDATE":
                        subidoExitosamente = GeneralOnlineProviderCRUD.actualizar(tablaAfectada, idRegistro, datos);
                        break;
                    case "DELETE":
                        subidoExitosamente = GeneralOnlineProviderCRUD.eliminar(tablaAfectada, idRegistro);
                        break;
                    default:
                        System.out
                                .println("Accion desconocida, se omite el registro con id: " + sincronizadora.getId());
                        continue;
                }

                if (!subidoExitosamente) {
                    System.out.println(
                            "Fallo la subida del registro con id: " + idRegistro + ", queda pendiente para reintento");
                    continue;
                }

                System.out.println("Registro con id: " + idRegistro + " faltante fue sincronizado correctamente ");
                if (sincronizadoraDAO.actualizarSincronizados(sincronizadora.getId())) {
                    System.out.println("Cambio el estado del registro a 1, sincronizado correctamente");
                }
            } catch (Exception e) {
                System.out.println(
                        "Error sincronizando el registro con id: " + sincronizadora.getId() + " - " + e.getMessage());
            }
        }
    }

    /**
     * Recorre el mapa de datos y convierte a {@link java.util.Date} todo valor de
     * texto que coincida
     * con el formato {@link #FORMATO_FECHA_SYNC}. El resto de valores se dejan
     * intactos. Asi, al
     * escribir el mapa en Firestore, esos campos se guardan como Timestamp en lugar
     * de String.
     *
     * @param datos mapa con los campos del registro a subir
     */
    private static void convertirCadenasFechaADate(Map<String, Object> datos) {
        SimpleDateFormat formato = new SimpleDateFormat(FORMATO_FECHA_SYNC);
        formato.setLenient(false);
        for (Map.Entry<String, Object> entrada : datos.entrySet()) {
            Object valor = entrada.getValue();
            if (!(valor instanceof String))
                continue;
            String texto = (String) valor;
            try {
                java.util.Date fecha = formato.parse(texto);
                // Solo se reemplaza si el texto completo correspondia a la fecha (parse
                // estricto).
                if (formato.format(fecha).equals(texto)) {
                    entrada.setValue(fecha);
                }
            } catch (Exception ignorado) {
                // No era una fecha: se deja el texto tal cual.
            }
        }
    }
}
