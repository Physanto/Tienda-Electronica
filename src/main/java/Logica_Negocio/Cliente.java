package Logica_Negocio;

import java.util.ArrayList;
/**
 *
 * @author Santiago Lopez
 */

/**
 * Clase que moldea a una Cliente en el sistema
 */
public class Cliente {

    public int id;
    private String nombre, apellido, direccion, cedula, nombreImg;

    public Cliente(){}

	public Cliente(int id, String nombre, String apellido, String cedula, String direccion, String nombreImg) {
        this.id= id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.direccion = direccion;
        this.nombreImg=nombreImg;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreImg() {
        return nombreImg;
    }

    public void setNombreImg(String nombreImg) {
        this.nombreImg = nombreImg;
    }
}
