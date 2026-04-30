package Helpers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class HelperRed {

    /*
    Verifica si hay conexion a internet, haciendo ping y verificando que la velocidad
    sea suficiente para la conexion a la base de datos en la nube
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
