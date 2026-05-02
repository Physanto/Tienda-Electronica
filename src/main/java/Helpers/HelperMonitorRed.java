package Helpers;

/**
 * Clase encargada de Monitorear la red
 * @author Manuel Escobar
 */
public class HelperMonitorRed extends Thread{

    public static boolean usandoNube = true;
    public static boolean estadoAnterior = true;

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
                    }
                    else {
                        System.out.println("Conexion perdida, cambiando a local");
                        usandoNube = false;
                    }
                    estadoAnterior = hayInternet;
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
}
