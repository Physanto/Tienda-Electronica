package Logica_Conexion;

import Logica_Negocio.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Santiago Lopez
 */
public class ClienteDAO implements ILocalCRUD<Cliente> {

    public static Connection con = Conexion.getConnection();

    /**
     * Agrega un nuevo cliente a la base de datos
     * @param cliente el cliente que quiere agregar a la base de datos
     * @return 0 si no modifico ninguna fila, o mayor a 0 (cantidad de filas que modifico)
     */
    @Override
    public boolean agregar(Cliente cliente){
        String query
                = "INSERT INTO Cliente (id,nombre,apellido,direccion,cedula,urlImg)"
                + " VALUES (?, ?, ?, ?, ?, ?)";
        try{
            PreparedStatement preparedStatement = con.prepareStatement(query);

            preparedStatement.setString(1, cliente.getId());
            preparedStatement.setString(2, cliente.getNombre());
            preparedStatement.setString(3, cliente.getApellido());
            preparedStatement.setString(4, cliente.getDireccion());
            preparedStatement.setString(5, cliente.getCedula());
            preparedStatement.setString(6, cliente.getUrlImg());

            return preparedStatement.executeUpdate() >= 1;
        }
        catch (Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }
        return false;
    }

    /**
     * Elimina de la base de datos el cliente con el id pasado por argumento
     * @param id es el id del cliente que se quiere eliminar
     * @return true si elimina el registro, de lo contrario false
     */
    @Override
    public boolean eliminar(String id){
        String query = "DELETE FROM Cliente WHERE id = ?";
        try{
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, id);

            return preparedStatement.executeUpdate() >= 1;
        }
        catch (Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }
       return false;
    }

    /**
     * Extrae de la base de datos el cliente que coincide con el id pasado por argumento
     * @param id es el id del cliente a buscar
     * @return un objeto de tipo Cliente con toda la informacion del cliente o null si no encuentra nada.
     */
    @Override
    public Cliente obtener(String id) {
        String query = "SELECT * FROM Cliente WHERE id = ?";
        Cliente cliente = null;
        try{
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                cliente = new Cliente(resultSet.getString("id"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellido"),
                        resultSet.getString("cedula"),
                        resultSet.getString("direccion"),
                        resultSet.getString("urlImg"));
            }
        }
        catch (Exception ex){
            System.out.println("Fallo algo en la base de datos: "+ ex.getMessage());
        }
        return cliente;
    }

    /**
     * Obtiene todos los registros de la tabla Cliente de la base de datos
     * @return una lista con los clientes registrados en la base de datos o una lista vacia sino existen clientes
     */
    @Override
    public ArrayList<Cliente> obteners(){
        String query = "SELECT * FROM Cliente";
        ArrayList<Cliente> listaClientes = new ArrayList<>();
        try{
            PreparedStatement preparedStatement = con.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Cliente cliente = new Cliente(
                        resultSet.getString("id"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellido"),
                        resultSet.getString("cedula"),
                        resultSet.getString("direccion"),
                        resultSet.getString("urlImg"));
                listaClientes.add(cliente);
            }
        }
        catch (Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }
        return listaClientes;
    }

    /**
     * Actualiza el registro del cliente que se le pase por argumento
     * @param cliente es el registro que se quiere actualizar
     */
    @Override
    public boolean actualizar(Cliente cliente){
         String query
            = "UPDATE Cliente SET nombre=?,apellido=?,direccion=?,cedula=?,urlImg=?"
              + " WHERE id = ?";
        try{
            PreparedStatement preparedStatement = con.prepareStatement(query);

            preparedStatement.setString(1, cliente.getNombre());
            preparedStatement.setString(2, cliente.getApellido());
            preparedStatement.setString(3, cliente.getDireccion());
            preparedStatement.setString(4, cliente.getCedula());
            preparedStatement.setString(5, cliente.getUrlImg());
            preparedStatement.setString(6, cliente.getId());

            return preparedStatement.executeUpdate() >= 1;
        }
        catch (Exception ex){
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
            con.close();
        }
        catch (Exception ex){
            System.out.println("Error:" + ex.getMessage());
        }
    }
}
