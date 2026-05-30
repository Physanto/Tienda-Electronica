package CapaDatos.Logica_Conexion;

import CapaLogicaNegocio.DTOS.PromocionAplicadaDTO;
import CapaLogicaNegocio.Logica_Negocio.Promocion;
import CapaLogicaNegocio.Logica_Negocio.Promociones;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Date;

public class PromocionDAO {

    public ArrayList<Promocion> getDataset(){
        String query = "SELECT p.id, p.stock, " +
                "COALESCE(DATEDIFF(CURDATE(), MAX(v.fechaVenta)), 999) AS diasSinVender, " +
                "COALESCE(SUM(dv.cantidad), 0) AS totalVendido " +
                "FROM Producto p " +
                "LEFT JOIN DetalleVenta dv ON p.id = dv.id " +
                "LEFT JOIN Venta v ON dv.id = v.id " +
                "GROUP BY p.id, p.stock";

        ArrayList<Promocion> listaPromocion = new ArrayList<>();

        Connection conexion = Conexion.getConexionLocal();

        if(conexion == null){ return null; }

        try{
            PreparedStatement preparedStatement = conexion.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

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

    public ArrayList<PromocionAplicadaDTO> datosPromociones(){
        String query = "SELECT p.id, p.nombre, p.marca, p.stock, p.precioActual " +
                "COALESCE(DATEDIFF(CURDATE(), MAX(v.fechaVenta)), 999) AS diasSinVender, " +
                "COALESCE(SUM(dv.cantidad), 0) AS totalVendido " +
                "FROM Producto p " +
                "LEFT JOIN DetalleVenta dv ON p.id = dv.id " +
                "LEFT JOIN Venta v ON dv.id = v.id " +
                "GROUP BY p.nombre, p.stock";

        ArrayList<PromocionAplicadaDTO> listaPromocion = new ArrayList<>();

        Connection conexion = Conexion.getConexionLocal();

        if(conexion == null){ return null; }

        try{
            PreparedStatement preparedStatement = conexion.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                PromocionAplicadaDTO promocion = new PromocionAplicadaDTO(resultSet.getString("id"), resultSet.getString("nombre"),
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
        String query = "INSERT INTO Promociones (id, nombre, descuento, fechaInicio, fechaFin, tipo) " +
                "VALUES (?, ?, ?, ?, ?, ?);";

        Connection conexion = Conexion.getConexionLocal();
        if(conexion == null) return false;

        try {
            PreparedStatement preparedStatement = conexion.prepareStatement(query);

            preparedStatement.setString(1, promociones.getId());
            preparedStatement.setString(2, promociones.getNombre());
            preparedStatement.setDouble(3, promociones.getDescuento());
            preparedStatement.setDate(4, (Date) promociones.getFechaInicio());
            preparedStatement.setDate(5, (Date) promociones.getFechaFin());
            preparedStatement.setString(6, promociones.getTipo().name());

            return preparedStatement.executeUpdate() >= 1;
        }
        catch(Exception e) {
            System.out.println("Error consulta PromocionDAO, agregar" + e.getMessage());
        }
        return false;
    }
}
