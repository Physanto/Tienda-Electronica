package Logica_Conexion;

import Logica_Negocio.Categoria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CategoriaDAO implements DAOInterfaceCrud<Categoria> {

    public static Connection conexion = Conexion.getConnection();

    /**
     * Agrega una nueva categoria a la base de datos
     * @param categoria la categoria que quiere agregar a la base de datos
     * @return 0 si no inserta ningun registro, >= 1 si inserta el registro en la base de datos.
     * @throws SQLException si no se puede acceder a la base de datos
     */
    @Override
    public boolean agregar(Categoria categoria) throws SQLException {
        String query = "INSERT INTO Categoria (id, nombre) VALUES (?,?)";
        PreparedStatement preparedStatement = conexion.prepareStatement(query);

        preparedStatement.setString(1, categoria.getId());
        preparedStatement.setString(2, categoria.getNombre());

        return preparedStatement.executeUpdate() >= 1;
    }
    /**
     * Elimina de la base de datos la categoria con el idCliente pasado por argumento
     * @param id es el idCliente de la categoria que se quiere eliminar
     * @return true si elimina el registro, de lo contrario false
     * @throws SQLException si no puede acceder a la base de datos
     */
    @Override
    public boolean eliminar(String id) throws SQLException {
        String query = "DELETE FROM Categoria WHERE id = ?";
        PreparedStatement preparedStatement = conexion.prepareStatement(query);
        preparedStatement.setString(1, id);

        return preparedStatement.executeUpdate() >= 1;
    }
    /**
     * Extrae de la base de datos la categoria que coincide con el idCliente pasado por argumento
     * @param id es el idCliente de la categoria a buscar
     * @return un objeto de tipo Categoria con toda la informacion de la categoria o null si no encuentra nada.
     * @throws SQLException si no puede acceder a la base de datos.
     */
    @Override
    public Categoria obtener(String id) throws SQLException {
        String query = "SELECT * FROM Categoria WHERE id = ?";
        PreparedStatement preparedStatement = conexion.prepareStatement(query);

        preparedStatement.setString(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();

        Categoria categoria = null;

        while(resultSet.next()){
            categoria = new Categoria(resultSet.getString("id"), resultSet.getString("nombre"));
        }
        return categoria;
    }
    /**
     * Obtiene todos los registros de la tabla categoria de la base de datos
     * @return una lista con las categorias registradas en la base de datos o una lista vacia sino existen categorias
     * @throws SQLException si no puede acceder a la base de datos
     */
    @Override
    public ArrayList<Categoria> obteners() throws SQLException {
        String query = "SELECT * FROM Categoria";
        PreparedStatement preparedStatement = conexion.prepareStatement(query);

        ResultSet resultSet = preparedStatement.executeQuery();

        ArrayList<Categoria> listaCategorias = new ArrayList<>();

        while(resultSet.next()){
            Categoria categoria = new Categoria(resultSet.getString("id"), resultSet.getString("nombre"));
            listaCategorias.add(categoria);
        }
        return listaCategorias;
    }
    /**
     * Actualiza el registro de la categoria que se le pase por argumento
     * @param categoria es el registro que se quiere actualizar
     * @throws SQLException sucede cuando no se puede acceder a la base de datos
     */
    @Override
    public boolean actualizar(Categoria categoria) throws SQLException {
        String query = "UPDATE Categoria SET nombre = ? WHERE id = ?";
        PreparedStatement preparedStatement = conexion.prepareStatement(query);

        preparedStatement.setString(1, categoria.getNombre());

        return preparedStatement.executeUpdate() >= 1;
    }
    /**
     * se encarga de cerrar la conexion con la base de datos
     * @throws SQLException lanza la exepcion si no puede cerrar esta conexion
     */
    @Override
    public void cerrarConexion() throws SQLException {
        conexion.close();
    }
}
