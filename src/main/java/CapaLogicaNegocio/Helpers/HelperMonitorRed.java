package CapaLogicaNegocio.Helpers;

/**
 * Clase que se comporta como un daemon para la monitorizacion de la red del sistema.
 *
 * @author Manuel Escobar
 */
public class HelperMonitorRed extends Thread{

    public static volatile boolean usandoNube = true;
    public static volatile boolean estadoAnterior = true;
    private static final int CICLOS_PARA_VACIADO_PERIODICO = 12;

    /**
     * Metodo sobreescrito de la clase Thread donde monitorea todo el tiempo el estado de la red
     * haciendo uso del metodo personalizado (HelperRed.verificarConexion)
     * si hay red entonces sincroniza los datos en la nube, de lo contrario cambia el estado de la red
     * este proceso hace el monitoreo cada 5 segundos durante toda la vida del programa
     */
    @Override
    public void run(){
        int ciclos = 0;

        boolean hayInternetInicial = HelperRed.verificarConexion();
        usandoNube = hayInternetInicial;
        estadoAnterior = hayInternetInicial;
        if(hayInternetInicial){
            HelperSincronizador.SincronizarLocalANube();
        }

        while(true){
            try{
                Thread.sleep(5000);

                boolean hayInternet = HelperRed.verificarConexion();

                if(hayInternet != estadoAnterior) {
                    if (hayInternet) {
                        System.out.println("Conexion Recuperada, cambiando a la nube");
                        usandoNube = true;

                        HelperSincronizador.SincronizarLocalANube();
                        ciclos = 0;
                    }
                    else {
                        System.out.println("Conexion perdida, cambiando a local");
                        usandoNube = false;
                    }
                    estadoAnterior = hayInternet;
                }
                //vaciado periodico si quizas hubo fallos en transacciones en red
                else if(hayInternet && ++ciclos >= CICLOS_PARA_VACIADO_PERIODICO){
                    ciclos = 0;
                    HelperSincronizador.SincronizarLocalANube();
                }
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