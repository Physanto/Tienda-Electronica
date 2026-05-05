package Logica_Negocio;

import java.util.Date;

public class Sincronizadora {

    public enum Accion { INSERT, UPDATE, DELETE};

    private String id, idRegistroAfectado;
    private Accion accion;
    private String tablaAfectada;
    private Date tiempo;

    public Sincronizadora(){ }

    public Sincronizadora(String id, Accion accion, String tablaAfectada, String idRegistroAfectado, Date tiempo) {
        this.id = id;
        this.accion = accion;
        this.tablaAfectada = tablaAfectada;
        this.idRegistroAfectado = idRegistroAfectado;
        this.tiempo = tiempo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Accion getAccion() {
        return accion;
    }

    public void setAccion(Accion accion) {
        this.accion = accion;
    }

    public String getTablaAfectada() {
        return tablaAfectada;
    }

    public void setTablaAfectada(String tablaAfectada) {
        this.tablaAfectada = tablaAfectada;
    }

    public String getIdRegistroAfectado() {
        return idRegistroAfectado;
    }

    public void setIdRegistroAfectado(String idRegistroAfectado) {
        this.idRegistroAfectado = idRegistroAfectado;
    }

    public Date getTiempo() {
        return tiempo;
    }

    public void setTiempo(Date tiempo) {
        this.tiempo = tiempo;
    }
}
