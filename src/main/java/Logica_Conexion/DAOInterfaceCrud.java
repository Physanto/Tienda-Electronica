package Logica_Conexion;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Santiago Lopez
 */

/**
 * Interfaz/contrato que define el CRUD para la base de datos
 * @param <T> Entidad la cual va implementar los metodos
 */

//Se hace el uso de Genericos para hacer una implementacion reutilizable.

public interface DAOInterfaceCrud<T> {
    
    public boolean agregar(T object) throws SQLException;
    public boolean eliminar(String id) throws SQLException;
    public T obtener(String id) throws SQLException;
    public ArrayList<T> obteners() throws SQLException;
    public boolean actualizar(T object) throws SQLException;
    public void cerrarConexion() throws SQLException;
}
