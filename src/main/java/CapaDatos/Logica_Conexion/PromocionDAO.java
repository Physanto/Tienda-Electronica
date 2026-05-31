package CapaDatos.Logica_Conexion;

import CapaLogicaNegocio.DTOS.PromocionesDTO;
import CapaLogicaNegocio.DTOS.AnalisisCliente;
import CapaLogicaNegocio.Logica_Negocio.Promocion;
import CapaLogicaNegocio.DTOS.Promociones;

import java.sql.*;
import java.util.ArrayList;

public class PromocionDAO {

    public ArrayList<Promocion> getDataset(){
        String query = "SELECT p.id, p.stock, " +
                "COALESCE(DATEDIFF(CURDATE(), MAX(v.fechaVenta)), 999) AS diasSinVender, " +
                "COALESCE(SUM(dv.cantidad), 0) AS totalVendido " +
                "FROM Producto p " +
                "LEFT JOIN DetalleVenta dv ON p.id = dv.idProducto " +
                "LEFT JOIN Venta v ON dv.idVenta = v.id " +
                "GROUP BY p.id, p.stock";

        ArrayList<Promocion> listaPromocion = new ArrayList<>();

        Connection conexion = Conexion.getConexionLocal();

        if(conexion == null){ return listaPromocion; }

        try(PreparedStatement preparedStatement = conexion.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery()){

            while(resultSet.next()){
                Promocion promocion = new Promocion(resultSet.getString("id"), resultSet.getDouble("stock"),
                        resultSet.getDouble("diasSinVender"), resultSet.getDouble("totalVendido"), -1);

                listaPromocion.add(promocion);
            }
        }
        catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }
        return listaPromocion;
    }

    public ArrayList<PromocionesDTO.PromocionAplicadaDTO> datosPromociones(){
        String query = "SELECT p.id, p.nombre, p.marca, p.stock, p.precioActual, " +
                "COALESCE(DATEDIFF(CURDATE(), MAX(v.fechaVenta)), 999) AS diasSinVender, " +
                "COALESCE(SUM(dv.cantidad), 0) AS totalVendido " +
                "FROM Producto p " +
                "LEFT JOIN DetalleVenta dv ON p.id = dv.id " +
                "LEFT JOIN Venta v ON dv.id = v.id " +
                "GROUP BY p.nombre, p.stock";

        ArrayList<PromocionesDTO.PromocionAplicadaDTO> listaPromocion = new ArrayList<>();

        Connection conexion = Conexion.getConexionLocal();

        if(conexion == null){ return listaPromocion; }

        try(PreparedStatement preparedStatement = conexion.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery()){

            while(resultSet.next()){
                PromocionesDTO.PromocionAplicadaDTO promocion = new PromocionesDTO.PromocionAplicadaDTO(resultSet.getString("id"), resultSet.getString("nombre"),
                        resultSet.getString("marca"), String.valueOf(resultSet.getLong("stock")),
                        String.valueOf(resultSet.getDouble("precioActual")), String.valueOf(resultSet.getLong("diasSinVender")),
                        String.valueOf(resultSet.getDouble("totalVendido"))
                );

                listaPromocion.add(promocion);
            }
        }
        catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }
        return listaPromocion;
    }

    public boolean agregarPromocionPersonalizada(Promociones promociones){
        String query = "INSERT INTO Promocion (id, nombre, descuento, fechaInicio, fechaFin, tipo) " +
                "VALUES (?, ?, ?, ?, ?, ?);";

        Connection conexion = Conexion.getConexionLocal();
        if(conexion == null) return false;

        try(PreparedStatement preparedStatement = conexion.prepareStatement(query)){

            preparedStatement.setString(1, promociones.getId());
            preparedStatement.setString(2, promociones.getNombre());
            preparedStatement.setDouble(3, promociones.getDescuento());
            preparedStatement.setTimestamp(4, new Timestamp(promociones.getFechaInicio().getTime()));
            preparedStatement.setTimestamp(5, new Timestamp(promociones.getFechaFin().getTime()));
            preparedStatement.setString(6, promociones.getTipo().name());

            return preparedStatement.executeUpdate() >= 1;
        }
        catch(Exception e) {
            System.out.println("Error consulta PromocionDAO, agregar" + e.getMessage());
        }
        return false;
    }

    public ArrayList<Promociones> obtenerPromocionesPersonalizadas(){
        String query = "SELECT * FROM Promocion";

        ArrayList<Promociones> listaPromociones = new ArrayList<>();

        Connection conexion = Conexion.getConexionLocal();
        if(conexion == null) return listaPromociones;


        try(PreparedStatement preparedStatement = conexion.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery()){

            while(resultSet.next()){

                Promociones promociones = new Promociones(resultSet.getString("id"), resultSet.getString("nombre"),
                       resultSet.getDouble("descuento"), resultSet.getTimestamp("fechaInicio"),
                        resultSet.getTimestamp("fechaFin"), Promociones.TipoPromocion.valueOf(resultSet.getString("tipo").toUpperCase())
                );
                listaPromociones.add(promociones);
            }
        }
        catch(Exception e) {
            System.out.println("Error consulta PromocionDAO, obtener" + e.getMessage());
        }
        return listaPromociones;
    }

    public boolean desactivarPromocion(java.util.Date fechaActual, String idPromocion){
        String query = "UPDATE Promocion SET fechaInicio=? WHERE id = ?";

        Connection conexion = Conexion.getConexionLocal();
        if(conexion == null) return false;

        try(PreparedStatement preparedStatement = conexion.prepareStatement(query)){

            preparedStatement.setTimestamp(1, new Timestamp(fechaActual.getTime()));
            preparedStatement.setString(2, idPromocion);

            return preparedStatement.executeUpdate() >= 1;
        }
        catch(Exception e) {
            System.out.println("Error consulta PromocionDAO, desactivando" + e.getMessage());
        }
        return false;
    }

    public boolean agregarPromocionCliente(PromocionesDTO.PromocionClienteDTO promocionClienteDTO){
        String query = "INSERT INTO PromocionCliente(id, idPromocion, idCliente) " +
                "VALUES (?, ?, ?);";

        Connection conexion = Conexion.getConexionLocal();
        if(conexion == null) return false;

        try(PreparedStatement preparedStatement = conexion.prepareStatement(query)){

            preparedStatement.setString(1, promocionClienteDTO.id());
            preparedStatement.setString(2, promocionClienteDTO.idPromocion());
            preparedStatement.setString(3, promocionClienteDTO.idCliente());

            return preparedStatement.executeUpdate() >= 1;
        }
        catch(Exception e) {
            System.out.println("Error consulta PromocionDAO" + e.getMessage());
        }
        return false;
    }

    public ArrayList<PromocionesDTO.PromocioPersonalizadaCliente> obtenerResumeComprasCliente(){
        String query =
                "SELECT c.id, c.nombre, c.apellido, c.cedula, " +
                        "COALESCE(SUM(v.totalVenta), 0) AS totalComprado, " +
                        "MAX(v.fechaVenta) AS ultimaCompra " +
                        "FROM Cliente c " +
                        "LEFT JOIN Venta v ON v.idCliente = c.id " +
                        "GROUP BY c.id, c.nombre, c.apellido, c.cedula";

        ArrayList<PromocionesDTO.PromocioPersonalizadaCliente> listaPromociones = new ArrayList<>();

        Connection conexion = Conexion.getConexionLocal();
        if(conexion == null) return listaPromociones;


        try(PreparedStatement preparedStatement = conexion.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery()){

            while(resultSet.next()){
                PromocionesDTO.PromocioPersonalizadaCliente promociones = new PromocionesDTO.PromocioPersonalizadaCliente(resultSet.getString("id"),
                        resultSet.getString("nombre"), resultSet.getString("apellido"),
                        resultSet.getString("cedula"), resultSet.getString("totalComprado"),
                        resultSet.getString("ultimaCompra")
                );
                listaPromociones.add(promociones);
            }
        }
        catch(Exception e) {
            System.out.println("Error consulta PromocionDAO, obtener" + e.getMessage());
        }
        return listaPromociones;
    }

    public boolean agregarPromocionProducto(PromocionesDTO.PromocionProductoDTO promocionProductoDTO){
        String query = "INSERT INTO PromocionProducto (id, idPromocion, idProducto) " +
                "VALUES (?, ?, ?);";

        Connection conexion = Conexion.getConexionLocal();
        if(conexion == null) return false;

        try(PreparedStatement preparedStatement = conexion.prepareStatement(query)){

            preparedStatement.setString(1, promocionProductoDTO.id());
            preparedStatement.setString(2, promocionProductoDTO.idPromocion());
            preparedStatement.setString(3, promocionProductoDTO.idProducto());

            return preparedStatement.executeUpdate() >= 1;
        }
        catch(Exception e) {
            System.out.println("Error consulta PromocionDAO" + e.getMessage());
        }
        return false;
    }

    public ArrayList<AnalisisCliente> getDatasetClientes(){
        String query = "SELECT c.id, " +
                " DATEDIFF(NOW(), MAX(v.fechaVenta)) AS recencia, " +
                " COUNT(v.id) AS frecuencia, " +
                " SUM(v.totalVenta) AS monetario FROM Cliente c " +
                " JOIN Venta v ON c.id = v.idCliente" +
                " GROUP BY c.id;";
        ArrayList<AnalisisCliente> listaPromociones = new ArrayList<>();

        Connection conexion = Conexion.getConexionLocal();
        if(conexion == null) return listaPromociones;

        try(PreparedStatement preparedStatement = conexion.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery()){

            while(resultSet.next()){

                AnalisisCliente analisisCliente = new AnalisisCliente(resultSet.getString("id"),
                        resultSet.getDouble("recencia"), resultSet.getDouble("frecuencia"),
                        resultSet.getDouble("monetario")
                );
                listaPromociones.add(analisisCliente);
            }
        }
        catch(Exception e) {
            System.out.println("Error consulta PromocionDAO, obtener" + e.getMessage());
        }
        return listaPromociones;
    }
}