package CapaDatos.Logica_Conexion;

import CapaLogicaNegocio.Logica_Negocio.Producto;

import java.sql.*;
import java.util.ArrayList;

/**
 * Clase que se encarga de hacer el CRUD en la tabla producto de la base de datos local,
 * esta implementa la interfaz generica definida en el mismo paquete, ademas hace uso de la clase Connection
 * para la comunicacion con la base de datos.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class ProductoDAO implements ILocalCRUD<Producto> {

    public static Connection conexion = Conexion.getConnection();

    /**
     * Agrega una nuevo registro a la base de datos
     * @param producto el producto que quiere agregar a la base de datos
     * @return 0 si no inserta ningun registro, >= 1 si inserta el registro en la base de datos.
     */
    @Override
    public boolean agregar(Producto producto){
        String query = "INSERT INTO Producto (id, codigo, nombre, marca, serie, stock," +
                "precioActual, fechaVencimiento, urlImg, idCategoria) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try{
            PreparedStatement preparedStatement = conexion.prepareStatement(query);
            preparedStatement.setString(1, producto.getId());
            preparedStatement.setString(2, producto.getCodigo());
            preparedStatement.setString(3, producto.getNombre());
            preparedStatement.setString(4, producto.getMarca());
            preparedStatement.setString(5, producto.getSerie());
            preparedStatement.setLong(6, producto.getStock());
            preparedStatement.setDouble(7, producto.getPrecioActual());
            preparedStatement.setTimestamp(8, new Timestamp(producto.getFechaVencimiento().getTime()));
            preparedStatement.setString(9, producto.getUrlImg());
            preparedStatement.setString(10, producto.getIdCategoria());

            return preparedStatement.executeUpdate() >= 1;
        }
        catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }
        return false;
    }

    /**
     * Elimina de la base de datos el registro con el idCliente pasado por argumento
     * @param id es el idCliente del registro que se quiere eliminar.
     * @return true si elimina el registro, de lo contrario false
     */
    @Override
    public boolean eliminar(String id){
        String query = "DELETE FROM Producto WHERE id = ?";
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
     * Extrae de la base de datos el registro que coincide con el idCliente pasado por argumento
     * @param id es el idCliente del registro a buscar
     * @return un objeto de tipo Producto con toda la informacion o null si no encuentra nada.
     */
    @Override
    public Producto obtener(String id){
        String query = "SELECT * FROM Producto WHERE id = ?";
        Producto producto = null;
        try{
            PreparedStatement preparedStatement = conexion.prepareStatement(query);

            preparedStatement.setString(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                producto = new Producto(resultSet.getString("id"), resultSet.getString("codigo"),
                        resultSet.getString("nombre"), resultSet.getString("marca"),
                        resultSet.getString("serie"), resultSet.getLong("stock"),
                        resultSet.getDouble("precioActual"), resultSet.getTimestamp("fechaVencimiento"),
                        resultSet.getString("urlImg"), resultSet.getString("idCategoria")
                );
            }
        }
        catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }
        return producto;
    }

    /**
     * Obtiene todos los registros de la tabla de la base de datos
     * @return una lista con los registros encontrados en la base de datos o una lista vacia sino existen categorias
     */
    @Override
    public ArrayList<Producto> obteners(){
        String query = "SELECT * FROM Producto";
        ArrayList<Producto> listaProductos= new ArrayList<>();
        try{
            PreparedStatement preparedStatement = conexion.prepareStatement(query);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                Producto producto = new Producto(resultSet.getString("id"), resultSet.getString("codigo"),
                        resultSet.getString("nombre"), resultSet.getString("marca"),
                        resultSet.getString("serie"), resultSet.getLong("stock"),
                        resultSet.getDouble("precioActual"), resultSet.getTimestamp("fechaVencimiento"),
                        resultSet.getString("urlImg"), resultSet.getString("idCategoria")
                );
                listaProductos.add(producto);
            }
            return listaProductos;
        }
        catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }
       return listaProductos;
    }

    /**
     * Actualiza el registro por la nueva informacion del objeto pasado por argumento
     * @param producto es el registro que se quiere actualizar
     */
    @Override
    public boolean actualizar(Producto producto){
        String query = "UPDATE Producto SET codigo = ?, nombre = ?, marca = ?, " +
                "serie = ?, stock = ?, precioActual = ?, " +
                "fechaVencimiento = ?, urlImg = ?, idCategoria = ? WHERE id = ?";
        try{
            PreparedStatement preparedStatement = conexion.prepareStatement(query);

            preparedStatement.setString(1, producto.getCodigo());
            preparedStatement.setString(2, producto.getNombre());
            preparedStatement.setString(3, producto.getMarca());
            preparedStatement.setString(4, producto.getSerie());
            preparedStatement.setLong(5, producto.getStock());
            preparedStatement.setDouble(6, producto.getPrecioActual());
            preparedStatement.setTimestamp(7, new Timestamp(producto.getFechaVencimiento().getTime()));
            preparedStatement.setString(8, producto.getUrlImg());
            preparedStatement.setString(9, producto.getIdCategoria());
            preparedStatement.setString(10, producto.getId());

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
        catch(Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
