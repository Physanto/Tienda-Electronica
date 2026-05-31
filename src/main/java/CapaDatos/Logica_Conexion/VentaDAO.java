package CapaDatos.Logica_Conexion;

import CapaLogicaNegocio.DTOS.VentasDTO;
import CapaLogicaNegocio.Logica_Negocio.Venta;
import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * DAO encargado de realizar el CRUD de la entidad Venta
 * en la base de datos local.
 * 
 * @author hiTiimmy
 */
public class VentaDAO implements ILocalDAO<Venta> {

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

        try(PreparedStatement preparedStatement = con.prepareStatement(query)){

            preparedStatement.setString(1, venta.getId());
            preparedStatement.setTimestamp(2, new Timestamp(venta.getFechaVenta().getTime()));
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

        try(PreparedStatement preparedStatement = con.prepareStatement(query)){
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

        try(PreparedStatement preparedStatement = con.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery()){
            preparedStatement.setString(1, id);

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

        try(PreparedStatement preparedStatement = con.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery()){

            while(resultSet.next()) {
                Venta venta = new Venta(resultSet.getString("id"), resultSet.getDate("fechaVenta"),
                        resultSet.getDouble("totalVenta"),
                        Venta.MetodoPago.valueOf(resultSet.getString("metodoPago").toUpperCase()), resultSet.getString("idCliente")
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
        String query = "UPDATE Venta SET fechaVenta=?, totalVenta=?, metodoPago=?, idCliente=? "
                + "WHERE id=?";

        Connection con = Conexion.getConexionLocal();
        if(con == null) { return false; }

        try(PreparedStatement preparedStatement = con.prepareStatement(query)){

            preparedStatement.setTimestamp(1, new Timestamp(venta.getFechaVenta().getTime()));
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

        try(PreparedStatement preparedStatement = conexion.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery()){

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

        String query = "SELECT COUNT(id) AS cantidad FROM Venta";

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

    public ArrayList<VentasDTO.VentaPorCategoriaDTO> ventasPorCategoria(){

        String query = "SELECT c.nombre AS categoria," +
                "    SUM(dt.cantidad) AS productosVendidos," +
                "    SUM(dt.cantidad * p.precioActual) AS totalVentas," +
                "    MIN(v.fechaVenta) AS primeraVenta," +
                "    MAX(v.fechaVenta) AS ultimaVenta " +
                "FROM Categoria c" +
                "JOIN Producto p ON p.idCategoria = c.id" +
                "JOIN DetalleVenta dt ON dt.idProducto = p.id" +
                "JOIN Venta v ON v.id = dt.idVenta" +
                "GROUP BY c.id, c.nombre;";

        ArrayList<VentasDTO.VentaPorCategoriaDTO> listaVentas = new ArrayList<>();
        Connection conexion = Conexion.getConexionLocal();

        if(conexion == null) return listaVentas;

        try(PreparedStatement preparedStatement = conexion.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery()){

            VentasDTO.VentaPorCategoriaDTO ventaPorCategoriaDTO;

            while(resultSet.next()){
                ventaPorCategoriaDTO = new VentasDTO.VentaPorCategoriaDTO(resultSet.getString("categoria"),
                        resultSet.getLong("productosVendidos"), resultSet.getDouble("totalVentas"),
                        resultSet.getTimestamp("primeraVenta"), resultSet.getTimestamp("ultimaVenta"));
                listaVentas.add(ventaPorCategoriaDTO);
            }
        }
        catch (Exception e){
            System.out.println("no se pudo ejecutar ventas por categoria");
        }
		return listaVentas;
    }
}
