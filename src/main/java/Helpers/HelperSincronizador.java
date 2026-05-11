package Helpers;

import Logica_Conexion.*;
import Logica_Negocio.Categoria;
import Logica_Negocio.Cliente;
import Logica_Negocio.Producto;
import Logica_Negocio.Sincronizadora;

import java.awt.image.PackedColorModel;
import java.util.ArrayList;

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
    public static <T> void SincronizarLocalANube(){
        System.out.println("Iniciando vaciado de cola local hacia la nube...");

        try{
            SincronizadoraDAO sincronizadoraDAO = new SincronizadoraDAO();
            ArrayList<Sincronizadora> datosPendientes = sincronizadoraDAO.obteners();

            if(datosPendientes.isEmpty()){
                System.out.println("No hay datos pendientes por subir");
                return;
            }
            for (Sincronizadora sincronizadora : datosPendientes) {
                if(extraerObjeto(sincronizadora)){
                    System.out.println("Registro con id: " + sincronizadora.getIdRegistroAfectado() + " faltante fue sincronizado correctamente ");
                }
            }
        }
        catch (Exception e){
            System.out.println("Error durante la sincronizacion: " + e.getMessage());
        }
    }

    public static <T> boolean extraerObjeto(Sincronizadora sincronizadora){

        String tablaAfectada = sincronizadora.getTablaAfectada();
        String id = sincronizadora.getIdRegistroAfectado();

       if(tablaAfectada.equals("Cliente")){
          ClienteDAO clienteDAO = new ClienteDAO();
          return insertarDatos(tablaAfectada, clienteDAO.obtener(id));
       }
       return false;
    }

    public static <T> boolean insertarDatos(String tabla, T object){

        if(object == null) return false;

        if(tabla.equals("Cliente") && object instanceof Cliente){
            ClienteOnlineDAO clienteOnlineDAO = new ClienteOnlineDAO();
            clienteOnlineDAO.registrarNube((Cliente) object);
        }
        else if(tabla.equals("Categoria") && object instanceof Categoria){
            CategoriaOnlineDAO categoriaOnlineDAO = new CategoriaOnlineDAO();
            categoriaOnlineDAO.registrarNube((Categoria) object);
        }
        else if(tabla.equals("Producto") && object instanceof Producto){
            ProductoOnlineDAO productoOnlineDAO = new ProductoOnlineDAO();
            productoOnlineDAO.registrarNube((Producto) object);
        }
        return true;
    }
}
