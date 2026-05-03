package Logica_Conexion;

import Logica_Negocio.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProductoDAO implements DAOInterfaceCrud<Producto> {

    public static Connection conexion = Conexion.getConnection();

    /**
     * Agrega una nuevo registro a la base de datos
     * @param producto el producto que quiere agregar a la base de datos
     * @return 0 si no inserta ningun registro, >= 1 si inserta el registro en la base de datos.
     * @throws SQLException si no se puede acceder a la base de datos
     */
    @Override
    public boolean agregar(Producto producto) throws SQLException {
        String query = "INSERT INTO Producto(id_producto, codigo, nombre, marca, serie, stock," +
                "precio_actual, fecha_vencimiento, nom_img, id_categoria) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = conexion.prepareStatement(query);

        preparedStatement.setInt(1, producto.getId());
        preparedStatement.setString(2, producto.getCodigo());
        preparedStatement.setString(3, producto.getNombre());
        preparedStatement.setString(4, producto.getMarca());
        preparedStatement.setString(5, producto.getSerial());
        preparedStatement.setInt(6, producto.getStock());
        preparedStatement.setBigDecimal(7, producto.getPrecioActual());
        preparedStatement.setTimestamp(8, producto.getFechaVencimiento());
        preparedStatement.setString(9, producto.getNombreImg());
        preparedStatement.setInt(10, producto.getIdCategoria());

        return preparedStatement.executeUpdate() >= 1;
    }

    /**
     * Elimina de la base de datos el registro con el id pasado por argumento
     * @param id es el id del registro que se quiere eliminar.
     * @return true si elimina el registro, de lo contrario false
     * @throws SQLException si no puede acceder a la base de datos
     */
    @Override
    public boolean eliminar(int id) throws SQLException {
        String query = "DELETE FROM Producto WHERE id_producto = ?";
        PreparedStatement preparedStatement = conexion.prepareStatement(query);
        preparedStatement.setInt(1, id);

        return preparedStatement.executeUpdate() >= 1;
    }

    /**
     * Extrae de la base de datos el registro que coincide con el id pasado por argumento
     * @param id es el id del registro a buscar
     * @return un objeto de tipo Producto con toda la informacion o null si no encuentra nada.
     * @throws SQLException si no puede acceder a la base de datos.
     */
    @Override
    public Producto obtener(int id) throws SQLException {
        String query = "SELECT * FROM Producto WHERE id_producto = ?";
        PreparedStatement preparedStatement = conexion.prepareStatement(query);

        preparedStatement.setInt(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();

        Producto producto = null;

        while(resultSet.next()){
            producto = new Producto(resultSet.getInt("id_producto"), resultSet.getString("codigo"),
                    resultSet.getString("nombre"), resultSet.getString("marca"),
                    resultSet.getString("serie"), resultSet.getInt("stock"),
                    resultSet.getBigDecimal("precio_actual"), resultSet.getTimestamp("fecha_vencimiento"),
                    resultSet.getString("nom_img"), resultSet.getInt("id_categoria"));
        }
        return producto;
    }

    /**
     * Obtiene todos los registros de la tabla de la base de datos
     * @return una lista con los registros encontrados en la base de datos o una lista vacia sino existen categorias
     * @throws SQLException si no puede acceder a la base de datos
     */
    @Override
    public ArrayList<Producto> obteners() throws SQLException {
        String query = "SELECT * FROM Producto";
        PreparedStatement preparedStatement = conexion.prepareStatement(query);

        ResultSet resultSet = preparedStatement.executeQuery();

        ArrayList<Producto> listaProductos= new ArrayList<>();

        while(resultSet.next()){
            Producto producto = new Producto(resultSet.getInt("id_producto"), resultSet.getString("codigo"),
                    resultSet.getString("nombre"), resultSet.getString("marca"),
                    resultSet.getString("serie"), resultSet.getInt("stock"),
                    resultSet.getBigDecimal("precio_actual"), resultSet.getTimestamp("fecha_vencimiento"),
                    resultSet.getString("nom_img"), resultSet.getInt("id_categoria"));
            listaProductos.add(producto);
        }
        return listaProductos;
    }

    /**
     * Actualiza el registro por la nueva informacion del objeto pasado por argumento
     * @param producto es el registro que se quiere actualizar
     * @throws SQLException sucede cuando no se puede acceder a la base de datos
     */
    @Override
    public boolean actualizar(Producto producto) throws SQLException {
        String query = "UPDATE Producto SET codigo = ?, nombre = ?, marca = ?, " +
                "serie = ?, stock = ?, precio_actual = ?, " +
                "fecha_vencimiento = ?, nom_img = ?, id_categoria = ? WHERE id_producto = ?";
        PreparedStatement preparedStatement = conexion.prepareStatement(query);

        preparedStatement.setString(1, producto.getCodigo());
        preparedStatement.setString(2, producto.getNombre());
        preparedStatement.setString(3, producto.getMarca());
        preparedStatement.setString(4, producto.getSerial());
        preparedStatement.setInt(5, producto.getStock());
        preparedStatement.setBigDecimal(6, producto.getPrecioActual());
        preparedStatement.setTimestamp(7, producto.getFechaVencimiento());
        preparedStatement.setString(8, producto.getNombreImg());
        preparedStatement.setInt(9, producto.getIdCategoria());

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
