package Helpers;

import Logica_Conexion.*;
import Logica_Negocio.Sincronizadora;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Map;

/**
 * Clase encargada de Sincronizar la informacion de las dos bases de datos existentes, esto
 * para la implementacion de un metodo de redundancia optimo
 *
 * @author Manuel Escobar (Physanto)
 */
public class HelperSincronizador {

    /**
     * Metodo que se encarga de sincronizar la base de datos local a la nube
     * - valida que haya datos sin sincronizar (con ayuda de un metodo personalizado ClienteDAO.getNoSincronizados())
     * - si hay datos que no estan sincronizados entonces los agrega a la nube
     * - de lo contrario sino hay nada pendiente entonces hace una parada anticipada
     */
    public static void SincronizarLocalANube(){
        System.out.println("Iniciando vaciado de cola local hacia la nube...");

        try{
            Gson gson = new Gson();
            ArrayList<Sincronizadora> datosPendientes = new SincronizadoraDAO().obtenerNoSincronizados("0");

            if(datosPendientes.isEmpty()){
                System.out.println("No hay datos pendientes por subir");
                return;
            }
            for (Sincronizadora sincronizadora : datosPendientes) {

                String registroJson = sincronizadora.getRegistroJson();
                String tablaAfectada = sincronizadora.getTablaAfectada();
                String idRegistro = sincronizadora.getIdRegistroAfectado();
                Map<String, Object> datos = gson.fromJson(registroJson, Map.class);

                switch (sincronizadora.getAccion().name()) {
                    case "INSERT":
                        GeneralOnlineProviderCRUD.guardar(tablaAfectada, idRegistro, datos);
                            break;
                    case "UPDATE":
                        GeneralOnlineProviderCRUD.actualizar(tablaAfectada, idRegistro, datos);
                            break;
                    case "DELETE":
                        GeneralOnlineProviderCRUD.eliminar(tablaAfectada, idRegistro);
                        break;
                }
                System.out.println("Registro con id: " + idRegistro + " faltante fue sincronizado correctamente ");
                if(new SincronizadoraDAO().actualizarSincronizados(sincronizadora.getId())){
                    System.out.println("Cambio el estado del registro a 1, sincronizado correctamente");
                }
            }
        }
        catch (Exception e){
            System.out.println("Error durante la sincronizacion: " + e.getMessage());
        }
    }
}
