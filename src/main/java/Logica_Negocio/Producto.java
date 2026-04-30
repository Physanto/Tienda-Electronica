package Logica_Negocio;
/**
 *
 * @author Santiago Lopez
 */

/**
 * Clase que moldea un Producto en el sistema
 */
public class Producto {

    public String nombre, marca, serial;

    public Producto() { }

    public Producto(String nombre, String marca, String serial) {
        this.nombre= nombre;
        this.marca = marca;
        this.serial = serial;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }
}
