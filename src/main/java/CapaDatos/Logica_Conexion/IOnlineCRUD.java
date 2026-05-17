package CapaDatos.Logica_Conexion;

import java.util.ArrayList;

/**
 * Intefaz/contrato que se define para toda la implementacion del CRUD en la base de datos de la nube,
 * esta se hace generica para la reutilizacion del codigo y minimizar la complejidad de manejar varias interfaces
 *
 * @author Manuel Figueroa (Physanto)
 */
public interface IOnlineCRUD<T> {

    public boolean registrarNube(T object);
    public T obtenerNube(Class<T> clase, String id);
    public ArrayList<T> obtenersNube(Class<T> clase);
    public boolean eliminarNube(String id);
    public boolean actualizarNube(T object);
}
