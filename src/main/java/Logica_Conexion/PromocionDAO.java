package Logica_Conexion;

import Logica_Negocio.Promocion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class PromocionDAO {

    private static Connection connection = Conexion.getConnection();

    public ArrayList<Promocion> getDataset(){
        String query = "SELECT p.id, p.stock, " +
                "COALESCE(DATEDIFF(CURDATE(), MAX(v.fechaVenta)), 999) AS diasSinVender, " +
                "COALESCE(SUM(dv.cantidad), 0) AS totalVendido " +
                "FROM Producto p " +
                "LEFT JOIN DetalleVenta dv ON p.id = dv.id " +
                "LEFT JOIN Venta v ON dv.id = v.id " +
                "GROUP BY p.id, p.stock";

        ArrayList<Promocion> listaPromocion = new ArrayList<>();

        try{


            PreparedStatement preparedStatement = connection.prepareStatement(query);
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
}
