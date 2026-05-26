package CapaDatos.Logica_Conexion;

import CapaLogicaNegocio.Controlador.RespuestaControlador;
import CapaLogicaNegocio.Logica_Negocio.Venta;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * DAO encargado de realizar el CRUD de la entidad Venta
 * en la base de datos local.
 * 
 * @author hiTiimmy
 */
public class VentaDAO implements ILocalCRUD<Venta> {

    /**
     * Agrega una nueva venta a la base de datos.
     */
    @Override
    public boolean agregar(Venta venta) {

        String query =
                "INSERT INTO Venta (id,fechaVenta,totalVenta,metodoPago,idCliente) "
                + "VALUES (?, ?, ?, ?, ?)";

        Connection con = Conexion.getConexionLocal();
        if(con == null) { return false; }

        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);

            preparedStatement.setString(1, venta.getId());
            //preparedStatement.setDate(2, new java.sql.Date(venta.getFechaVenta().getTime()));
            preparedStatement.setDate(2, (Date) venta.getFechaVenta());
            preparedStatement.setDouble(3, venta.getTotalVenta());
            preparedStatement.setString(4, venta.getMetodoPago().name());
            preparedStatement.setString(5, venta.getIdCliente());

            return preparedStatement.executeUpdate() >= 1;

        }
        catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return false;
    }

    /**
     * Elimina una venta de la base de datos.
     */
    @Override
    public boolean eliminar(String id) {
        String query = "DELETE FROM Venta WHERE id = ?";

        Connection con = Conexion.getConexionLocal();
        if(con == null) { return false; }

        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, id);

            return preparedStatement.executeUpdate() >= 1;

        }
        catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return false;
    }

    /**
     * Obtiene una venta por su id.
     */
    @Override
    public Venta obtener(String id) {

        String query = "SELECT * FROM Venta WHERE id = ?";
        Venta venta = null;

        Connection con = Conexion.getConexionLocal();
        if(con == null) { return null; }

        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                venta = new Venta(
                        resultSet.getString("id"),
                        resultSet.getDate("fechaVenta"),
                        resultSet.getDouble("totalVenta"),
                        Venta.MetodoPago.valueOf(
                                resultSet.getString("metodoPago")
                        ),
                        resultSet.getString("idCliente")
                );
            }

        }
        catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return venta;
    }

    /**
     * Obtiene todas las ventas.
     */
    @Override
    public ArrayList<Venta> obteners() {

        String query = "SELECT * FROM Venta";

        ArrayList<Venta> listaVentas = new ArrayList<>();

        Connection con = Conexion.getConexionLocal();
        if(con == null) { return listaVentas; }

        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                Venta venta = new Venta(
                        resultSet.getString("id"),
                        resultSet.getDate("fechaVenta"),
                        resultSet.getDouble("totalVenta"),
                        Venta.MetodoPago.valueOf(
                                resultSet.getString("metodoPago")
                        ),
                        resultSet.getString("idCliente")
                );
                listaVentas.add(venta);
            }

        }
        catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return listaVentas;
    }

    /**
     * Actualiza una venta existente.
     */
    @Override
    public boolean actualizar(Venta venta) {
        String query =
                "UPDATE Venta SET fechaVenta=?, totalVenta=?, metodoPago=?, idCliente=? "
                + "WHERE id=?";

        Connection con = Conexion.getConexionLocal();
        if(con == null) { return false; }

        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);

            preparedStatement.setDate(1, (Date)venta.getFechaVenta());
            preparedStatement.setDouble(2, venta.getTotalVenta());
            preparedStatement.setString(3, venta.getMetodoPago().name());
            preparedStatement.setString(4, venta.getIdCliente());
            preparedStatement.setString(5, venta.getId());

            return preparedStatement.executeUpdate() >= 1;
        }
        catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return false;
    }

    public Double totalVentas(){

        String query = "SELECT SUM(totalVenta) AS total FROM Venta";

        Double total = 0.0;

       Connection conexion = Conexion.getConexionLocal();

       if(conexion == null) return 0.0;

       try{
          PreparedStatement preparedStatement = conexion.prepareStatement(query);
          ResultSet resultSet = preparedStatement.executeQuery();

          if(resultSet.next()){
              total = resultSet.getDouble("total");

              if(resultSet.wasNull()) return 0.0;
          }
          return total;
       }
       catch(Exception e){
           System.out.println("Error en la obtencion de ventas");
       }
       return 0.0;
    }

    public Long cantidadVentas(){

        String query = "SELECT SUM(id) AS cantidad FROM Venta";

        Long total = 0l;

        Connection conexion = Conexion.getConexionLocal();

        if(conexion == null) return 0l;

        try{
            PreparedStatement preparedStatement = conexion.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

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

    public ArrayList<Venta> ventasPorCategoria(){

        String query = "SELECT * FROM Venta";
    }
}
