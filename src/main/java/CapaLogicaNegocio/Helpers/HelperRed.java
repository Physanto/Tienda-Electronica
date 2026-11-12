package CapaLogicaNegocio.Helpers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Clase que se encarga de manejar verificar la conexion a internet mediante sockets
 *
 * @author Manuel Escobar
 */
public class HelperRed {

    /**
     * Comprueba DNS y acceso TCP a Firestore. Una conexión lenta sigue siendo válida;
     * los permisos y la disponibilidad de la base se comprueban en las operaciones.
    */
    public static boolean verificarConexion(){
        int tiempoMaxEspera = 2000;

        long inicio = System.currentTimeMillis();

        // Se conecta a firestore.googleapis.com:443 en lugar de 8.8.8.8:53 para que
        // el check incluya resolucion DNS — si DNS falla, Firestore tampoco funcionara
        // y usandoNube quedaria en true causando errores UNAVAILABLE en gRPC.
        try(Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("firestore.googleapis.com", 443), tiempoMaxEspera);
            long fin = System.currentTimeMillis();
            long latencia = fin - inicio;

            System.out.println("Conexion Exitosa. Latencia: " + latencia + " ms");
            return true;

        } catch (IOException e) {
            System.out.println("No hay conexion a internet o fallo el intento");
            return false;
        }
    }
}
