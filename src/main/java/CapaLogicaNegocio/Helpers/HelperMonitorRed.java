package CapaLogicaNegocio.Helpers;

import CapaDatos.Logica_Conexion.Conexion;

import java.sql.Connection;

/**
 * Clase que se comporta como un daemon para la monitorizacion de la red del sistema.
 *
 * @author Manuel Escobar
 */
public class HelperMonitorRed extends Thread{

    // variables de tipo volatile porque se estan manejando hilos asi que se quiere que el cambio sea visible para todos

    public static volatile boolean usandoNube = true;
    public static volatile boolean estadoAnterior = true;

    public static volatile boolean localDisponible = true;
    public static volatile boolean estadoAnteriorLocal = true;

    /**
     * Metodo sobreescrito de la clase Thread donde monitorea todo el tiempo el estado de la red
     * haciendo uso del metodo personalizado (HelperRed.verificarConexion)
     * si hay red entonces sincroniza los datos en la nube, de lo contrario cambia el estado de la red
     * este proceso hace el monitoreo cada 5 segundos durante toda la vida del programa
     */
    @Override
    public void run(){
        while(true){
            try{
                boolean hayInternet = HelperRed.verificarConexion();

                if(hayInternet != estadoAnterior) {
                    if (hayInternet) {
                        System.out.println("Conexion Recuperada, cambiando a la nube");
                        usandoNube = true;

                        HelperSincronizador.SincronizarLocalANube();
                        if (hayLocalDisponible()) {
                            HelperSincronizador.SincronizarNubeALocal();
                        }
                    }
                    else {
                        System.out.println("Conexion perdida, cambiando a local");
                        usandoNube = false;
                    }
                    estadoAnterior = hayInternet;
                }

                boolean hayLocal = hayLocalDisponible();

                if(hayLocal != estadoAnteriorLocal) {
                    if (hayLocal) {
                        System.out.println("Base de datos local recuperada, bajando pendientes desde la nube");
                        localDisponible = true;

                        if (usandoNube) {
                            HelperSincronizador.SincronizarNubeALocal();
                        }
                    }
                    else {
                        System.out.println("Base de datos local perdida, operando contra la nube");
                        localDisponible = false;
                    }
                    estadoAnteriorLocal = hayLocal;
                }

                Thread.sleep(5000);
            }
            catch (InterruptedException e){ break; }
        }
    }

    /**
     * Metodo estatico que retorna si hay conexion o no.
     * @return true si hay conexion estable de internet, de lo contrario retorna false
     */
    public static boolean estaUsandoNube(){ return usandoNube; }

    /**
     * Verifica si la base de datos local esta disponible abriendo una conexion de sondeo que se cierra de inmediato.
     * @return true si se pudo conectar con la base local, de lo contrario false
     */
    private static boolean hayLocalDisponible(){
        try {
            Connection conexion = Conexion.getConexionLocal();
            return conexion != null && conexion.isValid(2);
        }
        catch (Exception e) {
            return false;
        }
    }
}
