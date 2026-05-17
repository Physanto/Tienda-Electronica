package CapaLogicaNegocio.Logica_Negocio;

/**
 * Representa una categoría dentro del sistema.
 * Esta entidad almacena y envia la informacion necesaria donde sea solicitada
 *
 * @author Manuel Figueroa (Physanto)
 */
public class Categoria {

    private String id;
    private String nombre;

    public Categoria() { }

    public Categoria(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
