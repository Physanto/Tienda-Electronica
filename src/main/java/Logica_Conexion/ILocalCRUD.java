package Logica_Conexion;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Intefaz/contrato que se define para toda la implementacion del CRUD en la base de datos local,
 * esta se hace generica para la reutilizacion del codigo y minimizar la complejidad de manejar varias interfaces
 *
 * @author Santiago Lopez
 * @author Manuel Figueroa (Physanto)
 */
public interface ILocalCRUD<T> {
    
    public boolean agregar(T object) throws SQLException;
    public boolean eliminar(String id) throws SQLException;
    public T obtener(String id) throws SQLException;
    public ArrayList<T> obteners() throws SQLException;
    public boolean actualizar(T object) throws SQLException;
    public void cerrarConexion() throws SQLException;
}
