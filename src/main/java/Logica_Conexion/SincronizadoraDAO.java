package Logica_Conexion;

import Logica_Negocio.Categoria;
import Logica_Negocio.Sincronizadora;

import java.sql.*;
import java.util.ArrayList;

/**
 * Clase que se encarga de hacer el CRUD en la tabla Sincronizadora de la base de datos
 */

// Este crud se usa para poder sincronizar los registros entre las dos bases de datos

public class SincronizadoraDAO implements DAOInterfaceCrud<Sincronizadora> {

    public static Connection conexion = Conexion.getConnection();

    /**
     * Agrega una nuevo registro a la base de datos
     * @param sincronizadora el producto que quiere agregar a la base de datos
     * @return 0 si no inserta ningun registro, >= 1 si inserta el registro en la base de datos.
     * @throws SQLException si no se puede acceder a la base de datos
     */
    @Override
    public boolean agregar(Sincronizadora sincronizadora) throws SQLException {
        String query = "INSERT INTO Cola_Sincronizadora (id, accion," +
                "tablaAfectada, idRegistroAfectado, tiempo) " +
                "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = conexion.prepareStatement(query);

        preparedStatement.setString(1, sincronizadora.getId());
        preparedStatement.setString(2, sincronizadora.getAccion().name());
        preparedStatement.setString(3, sincronizadora.getTablaAfectada());
        preparedStatement.setString(3, sincronizadora.getIdRegistroAfectado());
        preparedStatement.setTimestamp(4, new Timestamp(sincronizadora.getTiempo().getTime()));

        return preparedStatement.executeUpdate() >= 1;
    }
    /**
     * Elimina de la base de datos el registro con el idCliente pasado por argumento
     * @param id es el idCliente del registro que se quiere eliminar.
     * @return true si elimina el registro, de lo contrario false
     * @throws SQLException si no puede acceder a la base de datos
     */
    @Override
    public boolean eliminar(String id) throws SQLException {
        String query = "DELETE FROM Cola_Sincronizadora WHERE id = ?";
        PreparedStatement preparedStatement = conexion.prepareStatement(query);
        preparedStatement.setString(1, id);

        return preparedStatement.executeUpdate() >= 1;
    }

    /**
     * Extrae de la base de datos el registro que coincide con el idCliente pasado por argumento
     * @param id es el idCliente del registro a buscar
     * @return un objeto de tipo Producto con toda la informacion o null si no encuentra nada.
     * @throws SQLException si no puede acceder a la base de datos.
     */
    @Override
    public Sincronizadora obtener(String id) throws SQLException {
        String query = "SELECT * FROM Cola_Sincronizadora WHERE id = ?";
        PreparedStatement preparedStatement = conexion.prepareStatement(query);

        preparedStatement.setString(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();

        Sincronizadora sincronizadora = null;

        while(resultSet.next()){
            sincronizadora = new Sincronizadora(resultSet.getString("id"), Sincronizadora.Accion.valueOf(resultSet.getString("accion")),
                    resultSet.getString("tablaAfectada"), resultSet.getString("idRegistroAfectado"), resultSet.getTimestamp("tiempo"));
        }
        return sincronizadora;
    }

    /**
     * Obtiene todos los registros de la tabla de la base de datos
     * @return una lista con los registros encontrados en la base de datos o una lista vacia sino existen categorias
     * @throws SQLException si no puede acceder a la base de datos
     */
    @Override
    public ArrayList<Sincronizadora> obteners() throws SQLException {
        String query = "SELECT * FROM Cola_Sincronizadora";
        PreparedStatement preparedStatement = conexion.prepareStatement(query);

        ResultSet resultSet = preparedStatement.executeQuery();

        ArrayList<Sincronizadora> listaSincronizadora = new ArrayList<>();

        while(resultSet.next()){
            Sincronizadora sincronizadora = new Sincronizadora(resultSet.getString("id"), Sincronizadora.Accion.valueOf(resultSet.getString("accion")),
                    resultSet.getString("tablaAfectada"), resultSet.getString("idRegistroAfectado"), resultSet.getTimestamp("tiempo")
            );
            listaSincronizadora.add(sincronizadora);
        }
        return listaSincronizadora;
    }

    /**
     * Actualiza el registro por la nueva informacion del objeto pasado por argumento
     * @param sincronizadora es el registro que se quiere actualizar
     * @throws SQLException sucede cuando no se puede acceder a la base de datos
     */
    @Override
    public boolean actualizar(Sincronizadora sincronizadora) throws SQLException {
        String query = "UPDATE Cola_Sincronizadora SET accion = ?, tablaAfectada = ?," +
                "idRegistroAfectado = ?, tiempo = ? WHERE id = ?";
        PreparedStatement preparedStatement = conexion.prepareStatement(query);

        preparedStatement.setString(1, sincronizadora.getAccion().name());
        preparedStatement.setString(2, sincronizadora.getTablaAfectada());
        preparedStatement.setString(3, sincronizadora.getIdRegistroAfectado());
        preparedStatement.setTimestamp(4, new Timestamp(sincronizadora.getTiempo().getTime()));

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
