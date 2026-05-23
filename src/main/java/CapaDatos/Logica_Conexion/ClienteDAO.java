package CapaDatos.Logica_Conexion;

import CapaLogicaNegocio.Excepciones.ExcepcionSQL;
import CapaLogicaNegocio.Helpers.HelperExcepciones;
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
public class ClienteDAO implements ILocalCRUD<Cliente> {

    /**
     * Agrega un nuevo cliente a la base de datos
     * @param cliente el cliente que quiere agregar a la base de datos
     * @return 0 si no modifico ninguna fila, o mayor a 0 (cantidad de filas que modifico)
     * @throws ExcepcionSQL si se genera una
     */
    @Override
    public boolean agregar(Cliente cliente) throws ExcepcionSQL {
        String query
                = "INSERT INTO Cliente (id,nombre,apellido,direccion,cedula)"
                + " VALUES (?, ?, ?, ?, ?, ?)";
        try(Connection con= Conexion.getConnection()){

            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, cliente.getId());
            preparedStatement.setString(2, cliente.getNombre());
            preparedStatement.setString(3, cliente.getApellido());
            preparedStatement.setString(4, cliente.getDireccion());
            preparedStatement.setString(5, cliente.getCedula());

            return preparedStatement.executeUpdate() >= 1;
        }
        catch (SQLException e){
            HelperExcepciones.capturarExSQL(e);
        }
        return false;
    }

    /**
     * Elimina de la base de datos el cliente con el id pasado por argumento
     * @param id es el id del cliente que se quiere eliminar
     * @return true si elimina el registro, de lo contrario false
     */
    @Override
    public boolean eliminar(String id) throws ExcepcionSQL{
        String query = "DELETE FROM Cliente WHERE id = ?";

        try(Connection con= Conexion.getConnection()){
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, id);
            return preparedStatement.executeUpdate() >= 1;
        }
       catch (SQLException e){
            HelperExcepciones.capturarExSQL(e);
       }
        return false;

    }

    /**
     * Extrae de la base de datos el cliente que coincide con el id pasado por argumento
     * @param id es el id del cliente a buscar
     * @return un objeto de tipo Cliente con toda la informacion del cliente o null si no encuentra nada.
     */
    @Override
    public Cliente obtener(String id) throws ExcepcionSQL{
        String query = "SELECT * FROM Cliente WHERE id = ?";
        Cliente cliente = null;


        try(Connection con= Conexion.getConnection()){
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                cliente = new Cliente(resultSet.getString("id"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellido"),
                        resultSet.getString("cedula"),
                        resultSet.getString("direccion"));
            }
        }
        catch (SQLException e){
            HelperExcepciones.capturarExSQL(e);
        }
        return cliente;
    }

    /**
     * Obtiene todos los registros de la tabla Cliente de la base de datos
     * @return una lista con los clientes registrados en la base de datos o una lista vacia sino existen clientes
     */
    @Override
    public ArrayList<Cliente> obteners() throws ExcepcionSQL{
        String query = "SELECT * FROM Cliente";
        ArrayList<Cliente> listaClientes = new ArrayList<>();

        try(Connection con= Conexion.getConnection()){
            PreparedStatement preparedStatement = con.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

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
            HelperExcepciones.capturarExSQL(e);
        }
        return listaClientes;
    }

    /**
     * Actualiza el registro del cliente que se le pase por argumento
     * @param cliente es el registro que se quiere actualizar
     * @return 
     */
    @Override
    public boolean actualizar(Cliente cliente) throws ExcepcionSQL {

            String query = "UPDATE Cliente SET nombre=?,apellido=?,direccion=?,cedula=?"
                    + " WHERE id = ?";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(query);

            preparedStatement.setString(1, cliente.getNombre());
            preparedStatement.setString(2, cliente.getApellido());
            preparedStatement.setString(3, cliente.getDireccion());
            preparedStatement.setString(4, cliente.getCedula());
            preparedStatement.setString(6, cliente.getId());

            return preparedStatement.executeUpdate() >= 1;
        }
        catch (SQLException e) {
            HelperExcepciones.capturarExSQL(e);
        }
        return false;
    }

    /**
     * se encarga de cerrar la conexion con la base de datos
     */
    @Override
    public void cerrarConexion(){
    }
}
