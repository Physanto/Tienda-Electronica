package Logica_Conexion;

import Logica_Negocio.Categoria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Clase que se encarga de hacer el CRUD en la tabla categoria de la base de datos local,
 * esta implementa la interfaz generica definida en el mismo paquete, ademas hace uso de la clase Connection
 * para la comunicacion con la base de datos.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class CategoriaDAO implements ILocalCRUD<Categoria> {

    public static Connection conexion = Conexion.getConnection();

    /**
     * Agrega una nueva categoria a la base de datos
     * @param categoria la categoria que quiere agregar a la base de datos
     * @return false si no inserta ningun registro, de lo contrario true.
     */
    @Override
    public boolean agregar(Categoria categoria){
        String query = "INSERT INTO Categoria (id, nombre) VALUES (?,?)";
        try{
            PreparedStatement preparedStatement = conexion.prepareStatement(query);

            preparedStatement.setString(1, categoria.getId());
            preparedStatement.setString(2, categoria.getNombre());

            return preparedStatement.executeUpdate() >= 1;
        }
        catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }
       return false;
    }
    /**
     * Elimina de la base de datos la categoria con el idCliente pasado por argumento
     * @param id es el idCliente de la categoria que se quiere eliminar
     * @return true si elimina el registro, de lo contrario false
     */
    @Override
    public boolean eliminar(String id){
        String query = "DELETE FROM Categoria WHERE id = ?";
        try{
            PreparedStatement preparedStatement = conexion.prepareStatement(query);
            preparedStatement.setString(1, id);

            return preparedStatement.executeUpdate() >= 1;
        }
        catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }
        return false;
    }
    /**
     * Extrae de la base de datos la categoria que coincide con el idCliente pasado por argumento
     * @param id es el idCliente de la categoria a buscar
     * @return un objeto de tipo Categoria con toda la informacion de la categoria o null si no encuentra nada.
     */
    @Override
    public Categoria obtener(String id){
        String query = "SELECT * FROM Categoria WHERE id = ?";
        Categoria categoria = null;
        try{
            PreparedStatement preparedStatement = conexion.prepareStatement(query);

            preparedStatement.setString(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                categoria = new Categoria(resultSet.getString("id"), resultSet.getString("nombre"));
            }
            return categoria;
        }
        catch(Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return categoria;
    }
    /**
     * Obtiene todos los registros de la tabla categoria de la base de datos
     * @return una lista con las categorias registradas en la base de datos o una lista vacia sino existen categorias
     */
    @Override
    public ArrayList<Categoria> obteners(){
        String query = "SELECT * FROM Categoria";
        ArrayList<Categoria> listaCategorias = new ArrayList<>();
        try{
            PreparedStatement preparedStatement = conexion.prepareStatement(query);

            ResultSet resultSet = preparedStatement.executeQuery();


            while(resultSet.next()){
                Categoria categoria = new Categoria(resultSet.getString("id"), resultSet.getString("nombre"));
                listaCategorias.add(categoria);
            }
        }
        catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }
        return listaCategorias;
    }
    /**
     * Actualiza el registro de la categoria que se le pase por argumento
     * @param categoria es el registro que se quiere actualizar
     */
    @Override
    public boolean actualizar(Categoria categoria){
        String query = "UPDATE Categoria SET nombre = ? WHERE id = ?";
        try{
            PreparedStatement preparedStatement = conexion.prepareStatement(query);
            preparedStatement.setString(1, categoria.getNombre());
            preparedStatement.setString(2, categoria.getId());

            return preparedStatement.executeUpdate() >= 1;
        }
        catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }
        return false;
    }
    /**
     * se encarga de cerrar la conexion con la base de datos
     */
    @Override
    public void cerrarConexion(){
        try{
            conexion.close();
        }
        catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
