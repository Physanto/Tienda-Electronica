package CapaDatos.Logica_Conexion;

import CapaLogicaNegocio.Logica_Negocio.Sincronizadora;

import java.sql.*;
import java.util.ArrayList;

// Este crud se usa para poder sincronizar los registros entre las dos bases de datos

/**
 * Clase que se encarga de hacer el CRUD en la tabla Sincronizadora de la base de datos local,
 * esta implementa la interfaz generica definida en el mismo paquete, ademas hace uso de la clase Connection
 * para la comunicacion con la base de datos.
 *
 * @author Manuel Figueroa (Physanto)
 */
public class SincronizadoraDAO implements ILocalCRUD<Sincronizadora> {

    public static Connection conexion = Conexion.getConnection();

    /**
     * Agrega una nuevo registro a la base de datos
     * @param sincronizadora el producto que quiere agregar a la base de datos
     * @return 0 si no inserta ningun registro, >= 1 si inserta el registro en la base de datos.
     */
    @Override
    public boolean agregar(Sincronizadora sincronizadora){
        String query = "INSERT INTO ColaSincronizadora (id, accion," +
                "tablaAfectada, idRegistroAfectado, registroJson, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try{
            PreparedStatement preparedStatement = conexion.prepareStatement(query);

            preparedStatement.setString(1, sincronizadora.getId());
            preparedStatement.setString(2, sincronizadora.getAccion().name());
            preparedStatement.setString(3, sincronizadora.getTablaAfectada());
            preparedStatement.setString(4, sincronizadora.getIdRegistroAfectado());
            preparedStatement.setString(5, sincronizadora.getRegistroJson());
            preparedStatement.setString(6, sincronizadora.getEstado());

            return preparedStatement.executeUpdate() >= 1;
        }
        catch (Exception ex){
            System.out.println("Error aqui:" + ex.getMessage());
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
        String query = "DELETE FROM ColaSincronizadora WHERE id = ?";
        try{
            PreparedStatement preparedStatement = conexion.prepareStatement(query);
            preparedStatement.setString(1, id);

            return preparedStatement.executeUpdate() >= 1;
        }
        catch (Exception ex){
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
    public Sincronizadora obtener(String id){
        String query = "SELECT * FROM ColaSincronizadora WHERE id = ?";
        Sincronizadora sincronizadora = null;
        try{
            PreparedStatement preparedStatement = conexion.prepareStatement(query);

            preparedStatement.setString(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                sincronizadora = new Sincronizadora(resultSet.getString("id"), Sincronizadora.Accion.valueOf(resultSet.getString("accion")),
                        resultSet.getString("tablaAfectada"), resultSet.getString("idRegistroAfectado"),
                        resultSet.getString("registroJson"), resultSet.getString("estado"));
            }
        }
        catch (Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }
        return sincronizadora;
    }

    /**
     * Obtiene todos los registros de la tabla de la base de datos
     * @return una lista con los registros encontrados en la base de datos o una lista vacia sino existen categorias
     */
    @Override
    public ArrayList<Sincronizadora> obteners(){
        String query = "SELECT * FROM ColaSincronizadora";
        ArrayList<Sincronizadora> listaSincronizadora = new ArrayList<>();
        try{
            PreparedStatement preparedStatement = conexion.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                Sincronizadora sincronizadora = new Sincronizadora(resultSet.getString("id"), Sincronizadora.Accion.valueOf(resultSet.getString("accion")),
                        resultSet.getString("tablaAfectada"), resultSet.getString("idRegistroAfectado"),
                        resultSet.getString("registroJson"), resultSet.getString("estado"));
                listaSincronizadora.add(sincronizadora);
            }
        }
        catch (Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }
        return listaSincronizadora;
    }

    /**
     * Actualiza el registro por la nueva informacion del objeto pasado por argumento
     * @param sincronizadora es el registro que se quiere actualizar
     */
    @Override
    public boolean actualizar(Sincronizadora sincronizadora){
        String query = "UPDATE ColaSincronizadora SET accion = ?, tablaAfectada = ?," +
                "idRegistroAfectado = ?, registroJson = ?, estado = ? WHERE id = ?";
        try{
            PreparedStatement preparedStatement = conexion.prepareStatement(query);

            preparedStatement.setString(1, sincronizadora.getAccion().name());
            preparedStatement.setString(2, sincronizadora.getTablaAfectada());
            preparedStatement.setString(3, sincronizadora.getIdRegistroAfectado());
            preparedStatement.setString(4, sincronizadora.getRegistroJson());
            preparedStatement.setString(5, sincronizadora.getEstado());
            preparedStatement.setString(6, sincronizadora.getId());

            return preparedStatement.executeUpdate() >= 1;
        }
        catch (Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }
       return false;
    }

    public boolean actualizarSincronizados(String id){
        String query = "UPDATE ColaSincronizadora SET estado = ? WHERE id = ?";
        try{
            PreparedStatement preparedStatement = conexion.prepareStatement(query);

            preparedStatement.setString(1, "1");
            preparedStatement.setString(2, id);

            return preparedStatement.executeUpdate() >= 1;
        }
        catch (Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }
        return false;
    }

    public ArrayList<Sincronizadora> obtenerNoSincronizados(String estado){
        String query = "SELECT * FROM ColaSincronizadora WHERE estado = ?";
        ArrayList<Sincronizadora> listaNoSincronizados = new ArrayList<>();
        try{
            PreparedStatement preparedStatement = conexion.prepareStatement(query);

            preparedStatement.setString(1, estado);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                Sincronizadora sincronizadora = new Sincronizadora(resultSet.getString("id"), Sincronizadora.Accion.valueOf(resultSet.getString("accion")),
                        resultSet.getString("tablaAfectada"), resultSet.getString("idRegistroAfectado"),
                        resultSet.getString("registroJson"), resultSet.getString("estado"));
                listaNoSincronizados.add(sincronizadora);
            }
        }
        catch (Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }
        return listaNoSincronizados;
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
