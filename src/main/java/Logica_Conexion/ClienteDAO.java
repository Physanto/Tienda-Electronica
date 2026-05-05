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

//Usamos la interfaz cumpliendo con el patron DAO y se implementa mediante Genericos
/**
 * Clase que se encarga de hacer y gestionar todo el CRUD a la base de datos
 */
public class ClienteDAO implements DAOInterfaceCrud<Cliente> {

    public static Connection con = Conexion.getConnection();

    /**
     * Agrega un nuevo cliente a la base de datos
     * @param cliente el cliente que quiere agregar a la base de datos
     * @return 0 si no modifico ninguna fila, o mayor a 0 (cantidad de filas que modifico)
     * @throws SQLException si no se puede acceder a la base de datos
     */
    @Override
    public boolean agregar(Cliente cliente) throws SQLException {
        String query
                = "INSERT INTO Cliente(id_cliente,nombre,apellido,direccion,cedula,nom_img)"
                + "VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = con.prepareStatement(query);

        preparedStatement.setInt(1, cliente.getIdCliente());
        preparedStatement.setString(2, cliente.getNombre());
        preparedStatement.setString(3, cliente.getApellido());
        preparedStatement.setString(4, cliente.getDireccion());
        preparedStatement.setString(5, cliente.getCedula());
        preparedStatement.setString(6, cliente.getUrlImg());

        return preparedStatement.executeUpdate() >= 1;
    }

    /**
     * Elimina de la base de datos el cliente con el idCliente pasado por argumento
     * @param id es el idCliente del cliente que se quiere eliminar
     * @return true si elimina el registro, de lo contrario false
     * @throws SQLException si no puede acceder a la base de datos
     */
    @Override
    public boolean eliminar(int id) throws SQLException {
        String query = "DELETE FROM Cliente WHERE id_cliente = ?";
        PreparedStatement preparedStatement = con.prepareStatement(query);
        preparedStatement.setInt(1, id);

       return preparedStatement.executeUpdate() >= 1;
    }

    /**
     * Extrae de la base de datos el cliente que coincide con el idCliente pasado por argumento
     * @param id es el idCliente del cliente a buscar
     * @return un objeto de tipo Cliente con toda la informacion del cliente o null si no encuentra nada.
     * @throws SQLException si no puede acceder a la base de datos.
     */
    @Override
    public Cliente obtener(int id) throws SQLException {
        String query = "SELECT * FROM Cliente WHERE id_cliente = ?";
        PreparedStatement preparedStatement = con.prepareStatement(query);

        preparedStatement.setInt(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();

        Cliente cliente = null;

        while (resultSet.next()) {
            cliente = new Cliente(resultSet.getInt("id"),
                    resultSet.getString("nombre"),
                    resultSet.getString("apellido"),
                    resultSet.getString("cedula"),
                    resultSet.getString("direccion"),
                    resultSet.getString("nom_img"));
        }
        return cliente;
    }

    /**
     * Obtiene todos los registros de la tabla Cliente de la base de datos
     * @return una lista con los clientes registrados en la base de datos o una lista vacia sino existen clientes
     * @throws SQLException si no puede acceder a la base de datos
     */
    @Override
    public ArrayList<Cliente> obteners() throws SQLException {
        String query = "SELECT * FROM Cliente";
        PreparedStatement preparedStatement = con.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Cliente> listaClientes = new ArrayList<>();
 
        while (resultSet.next()) {
            Cliente cliente = new Cliente(
                    resultSet.getInt("id"),
                    resultSet.getString("nombre"),
                    resultSet.getString("apellido"),
                    resultSet.getString("cedula"),
                    resultSet.getString("direccion"),
                    resultSet.getString("nom_img"));
            listaClientes.add(cliente);
        }
        return listaClientes;
    }

    /**
     * Actualiza el registro del cliente que se le pase por argumento
     * @param cliente es el registro que se quiere actualizar
     * @throws SQLException sucede cuando no se puede acceder a la base de datos
     */
    @Override
    public boolean actualizar(Cliente cliente) throws SQLException {
         String query
            = "UPDATE Cliente SET nombre=?,apellido=?,direccion=?,cedula=?,nom_img=?"
              + "WHERE id_cliente=?";
            PreparedStatement preparedStatement = con.prepareStatement(query);

        preparedStatement.setInt(1, cliente.getIdCliente());
        preparedStatement.setString(2, cliente.getNombre());
        preparedStatement.setString(3, cliente.getApellido());
        preparedStatement.setString(4, cliente.getDireccion());
        preparedStatement.setString(5, cliente.getCedula());
        preparedStatement.setString(6, cliente.getUrlImg());

        return preparedStatement.executeUpdate() >= 1;
    }

    public ArrayList<Cliente> getNoSincronizados() throws SQLException{
        String query = "SELECT * FROM Cliente WHERE estado = 0";
        PreparedStatement preparedStatement = con.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Cliente> listaClientes = new ArrayList<>();

        while(resultSet.next()){
            Cliente cliente = new Cliente(resultSet.getInt("id"),
                    resultSet.getString("nombre"),
                    resultSet.getString("apellido"),
                    resultSet.getString("cedula"),
                    resultSet.getString("direccion"),
                    resultSet.getString("nom_img"));
            listaClientes.add(cliente);
        }
        return listaClientes;
    }

    public void marcarResultSetincronizado(String Uid) throws SQLException {
        String query = "UPDATE Cliente SET estado = 1 WHERE idCliente = ?";
        PreparedStatement preparedStatement = con.prepareStatement(query);
        preparedStatement.setString(1,Uid);
        preparedStatement.executeUpdate();
    }

    /**
     * se encarga de cerrar la conexion con la base de datos
     * @throws SQLException lanza la exepcion si no puede cerrar esta conexion
     */
    @Override
    public void cerrarConexion() throws SQLException { con.close(); }
}
