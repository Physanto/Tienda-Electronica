package Logica_Negocio;

import java.sql.Timestamp;

public class Sincronizadora {

    public enum Accion { INSERT, UPDATE, DELETE};

    private int id, idRegistroAfectado;
    private Accion accion;
    private String tablaAfectada;
    private Timestamp tiempo;

    public Sincronizadora(){ }

    public Sincronizadora(int id, Accion accion, String tablaAfectada, int idRegistroAfectado, Timestamp tiempo) {
        this.id = id;
        this.idRegistroAfectado = idRegistroAfectado;
        this.accion = accion;
        this.tablaAfectada = tablaAfectada;
        this.tiempo = tiempo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdRegistroAfectado() {
        return idRegistroAfectado;
    }

    public void setIdRegistroAfectado(int idRegistroAfectado) {
        this.idRegistroAfectado = idRegistroAfectado;
    }

    public Accion getAccion() {
        return accion;
    }

    public String getTablaAfectada() {
        return tablaAfectada;
    }

    public void setTablaAfectada(String tablaAfectada) {
        this.tablaAfectada = tablaAfectada;
    }

    public Timestamp getTiempo() {
        return tiempo;
    }

    public void setTiempo(Timestamp tiempo) {
        this.tiempo = tiempo;
    }
}
