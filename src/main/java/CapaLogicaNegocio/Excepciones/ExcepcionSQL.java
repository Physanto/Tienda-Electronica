package CapaLogicaNegocio.Excepciones;

public class ExcepcionSQL extends Exception{

    public ExcepcionSQL(String mensaje){
        super(mensaje);
    }
}