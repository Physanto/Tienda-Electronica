package CapaDatos.Logica_Conexion;

import CapaLogicaNegocio.Logica_Negocio.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Santiago Lopez
 */
public class ClienteDAO implements ILocalDAO<Cliente> {

    /**
     * Agrega un nuevo cliente a la base de datos
     * @param cliente el cliente que quiere agregar a la base de datos
     * @return 0 si no modifico ninguna fila, o mayor a 0 (cantidad de filas que modifico)
     */
    @Override
    public boolean agregar(Cliente cliente){
        String query
                = "INSERT INTO Cliente (id,nombre,apellido,direccion,cedula)"
                + " VALUES (?, ?, ?, ?, ?)";

        Connection con = Conexion.getConexionLocal();
        if(con == null) { return false; }

        try(PreparedStatement preparedStatement = con.prepareStatement(query)){
            preparedStatement.setString(1, cliente.getId());
            preparedStatement.setString(2, cliente.getNombre());
            preparedStatement.setString(3, cliente.getApellido());
            preparedStatement.setString(4, cliente.getDireccion());
            preparedStatement.setString(5, cliente.getCedula());

            return preparedStatement.executeUpdate() >= 1;
        }
        catch (SQLException e){
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Elimina de la base de datos el cliente con el id pasado por argumento
     * @param id es el id del cliente que se quiere eliminar
     * @return true si elimina el registro, de lo contrario false
     */
    @Override
    public boolean eliminar(String id) {
        String query = "DELETE FROM Cliente WHERE id = ?";

        Connection con = Conexion.getConexionLocal();
        if(con == null) { return false; }

        try(PreparedStatement preparedStatement = con.prepareStatement(query)){
            preparedStatement.setString(1, id);
            return preparedStatement.executeUpdate() >= 1;
        }
       catch (SQLException e){
           System.out.println("Error: " + e.getMessage());
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

        Connection con = Conexion.getConexionLocal();
        if(con == null) { return null; }

        try(PreparedStatement preparedStatement = con.prepareStatement(query)){
            preparedStatement.setString(1, id);

            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    cliente = new Cliente(resultSet.getString("id"),
                            resultSet.getString("nombre"),
                            resultSet.getString("apellido"),
                            resultSet.getString("cedula"),
                            resultSet.getString("direccion")
                    );
                }
            }
        }
        catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
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

        Connection con = Conexion.getConexionLocal();
        if(con == null) { return listaClientes; }

        try(PreparedStatement preparedStatement = con.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery()){

            while (resultSet.next()) {
                Cliente cliente = new Cliente(
                        resultSet.getString("id"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellido"),
                        resultSet.getString("cedula"),
                        resultSet.getString("direccion"));
                listaClientes.add(cliente);
            }
        }
        catch (SQLException e){
            System.out.println("Error: " + e.getMessage());
        }
        return listaClientes;
    }

    /**
     * Actualiza el registro del cliente que se le pase por argumento
     * @param cliente es el registro que se quiere actualizar
     * @return true si actualiza correctamente, de lo contrario retorna false
     */
    @Override
    public boolean actualizar(Cliente cliente){

        String query = "UPDATE Cliente SET nombre=?,apellido=?,direccion=?,cedula=?"
                    + " WHERE id = ?";
        Connection con = Conexion.getConexionLocal();
        if(con == null) { return false; }

        try(PreparedStatement preparedStatement = con.prepareStatement(query)){

            preparedStatement.setString(1, cliente.getNombre());
            preparedStatement.setString(2, cliente.getApellido());
            preparedStatement.setString(3, cliente.getDireccion());
            preparedStatement.setString(4, cliente.getCedula());
            preparedStatement.setString(5, cliente.getId());

            return preparedStatement.executeUpdate() >= 1;
        }
        catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }

    public Long cantidadClientes(){
        String query = "SELECT COUNT(id) AS cantidad FROM Cliente";
        Long total = 0l;

        Connection conexion = Conexion.getConexionLocal();
        if(conexion == null) return 0l;

        try(PreparedStatement preparedStatement = conexion.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery()){

            if(resultSet.next()){
                total = resultSet.getLong("cantidad");

                if(resultSet.wasNull()) return 0l;
            }
            return total;
        }
        catch(Exception e){
            System.out.println("Error en obtener la cantidad de registros de ventas");
        }
        return 0l;
    }
}
