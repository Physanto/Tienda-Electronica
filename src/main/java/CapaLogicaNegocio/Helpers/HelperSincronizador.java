package CapaLogicaNegocio.Helpers;

import CapaDatos.Logica_Conexion.SincronizadoraDAO;
import CapaDatos.Logica_Conexion.GeneralOnlineProviderCRUD;
import CapaLogicaNegocio.Logica_Negocio.Sincronizadora;
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
     * este metodo te hace toda la sincronizacion independientemente de si
     * insertas, eliminas o actualizas un registro
     */
    public static void SincronizarLocalANube(){
        System.out.println("Iniciando vaciado de cola local hacia la nube...");

        Gson gson = new Gson();
        SincronizadoraDAO sincronizadoraDAO = new SincronizadoraDAO();
        ArrayList<Sincronizadora> datosPendientes = sincronizadoraDAO.obtenerNoSincronizados("0");

        if(datosPendientes.isEmpty()){
            System.out.println("No hay datos pendientes por subir");
            return;
        }

        for (Sincronizadora sincronizadora : datosPendientes) {
            try {
                String registroJson = sincronizadora.getRegistroJson();
                String tablaAfectada = sincronizadora.getTablaAfectada();
                String idRegistro = sincronizadora.getIdRegistroAfectado();
                Map<String, Object> datos = gson.fromJson(registroJson, Map.class);

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
                        System.out.println("Accion desconocida, se omite el registro con id: " + sincronizadora.getId());
                        continue;
                }

                if(!subidoExitosamente){
                    System.out.println("Fallo la subida del registro con id: " + idRegistro + ", queda pendiente para reintento");
                    continue;
                }

                System.out.println("Registro con id: " + idRegistro + " faltante fue sincronizado correctamente ");
                if(sincronizadoraDAO.actualizarSincronizados(sincronizadora.getId())){
                    System.out.println("Cambio el estado del registro a 1, sincronizado correctamente");
                }
            }
            catch (Exception e){
                System.out.println("Error sincronizando el registro con id: " + sincronizadora.getId() + " - " + e.getMessage());
            }
        }
    }
}
