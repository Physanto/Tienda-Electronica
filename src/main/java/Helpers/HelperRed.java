package Helpers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Clase encargada de manejar parte de la conexion a la red wifi
 * @author Manuel Escobar
 */
public class HelperRed {

    /**
     * Verifica si hay conexion a internet, haciendo ping y verificando que la velocidad
     * sea suficiente para la conexion a la base de datos en la nube
     * por medio de un socket hace un ping a www.google.com y toma el tiempo que tarda esa conexion
     * entre el inicio y fin del tiempo tomado para hacer el ping
     * si ese tiempo es menor a 2 seg entonces el internet es optimo para usar la base de datos.
    */
    public static boolean verificarConexion(){
        int tiempoMaxEspera = 2000;
        long limLatencia = 500;

        long inicio = System.currentTimeMillis();

        try(Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("8.8.8.8", 53), tiempoMaxEspera);
            long fin = System.currentTimeMillis();
            long latencia = fin - inicio;

            System.out.println("Conexion Exitosa. Latencia: " + latencia + " ms");
            return latencia < limLatencia;

        } catch (IOException e) {
            System.out.println("No hay conexion a internet o fallo el intento");
            return false;
        }
    }
}
