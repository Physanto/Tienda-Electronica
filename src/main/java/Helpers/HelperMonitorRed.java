package Helpers;

public class HelperMonitorRed extends Thread{

    public static boolean usandoNube = true;
    public static boolean estadoAnterior = true;

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
    public static boolean estaUsandoNube(){ return usandoNube; }
}
