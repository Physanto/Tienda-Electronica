/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logica_Conexion;

import Logica_Negocio.DetalleVenta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * DAO encargado del CRUD de DetalleVenta
 * en la base de datos local.
 * 
 * @author hiTiimmy
 */
public class DetalleVentaDAO implements ILocalCRUD<DetalleVenta> {

    public static Connection con = Conexion.getConnection();

    @Override
    public boolean agregar(DetalleVenta detalleVenta) {

        String query =
                "INSERT INTO DetalleVenta "
                + "(id,cantidad,subtotal,precioVenta,idProducto,idVenta) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try {

            PreparedStatement preparedStatement = con.prepareStatement(query);

            preparedStatement.setString(1, detalleVenta.getId());
            preparedStatement.setLong(2, detalleVenta.getCantidad());
            preparedStatement.setDouble(3, detalleVenta.getSubtotal());
            preparedStatement.setDouble(4, detalleVenta.getPrecioVenta());
            preparedStatement.setString(5, detalleVenta.getIdProducto());
            preparedStatement.setString(6, detalleVenta.getIdVenta());

            return preparedStatement.executeUpdate() >= 1;

        } catch (Exception ex) {

            System.out.println("Error: " + ex.getMessage());
        }

        return false;
    }

    @Override
    public boolean eliminar(String id) {

        String query = "DELETE FROM DetalleVenta WHERE id = ?";

        try {

            PreparedStatement preparedStatement = con.prepareStatement(query);

            preparedStatement.setString(1, id);

            return preparedStatement.executeUpdate() >= 1;

        } catch (Exception ex) {

            System.out.println("Error: " + ex.getMessage());
        }

        return false;
    }

    @Override
    public DetalleVenta obtener(String id) {

        String query = "SELECT * FROM DetalleVenta WHERE id = ?";

        DetalleVenta detalleVenta = null;

        try {

            PreparedStatement preparedStatement = con.prepareStatement(query);

            preparedStatement.setString(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {

                detalleVenta = new DetalleVenta(
                        resultSet.getString("id"),
                        resultSet.getLong("cantidad"),
                        resultSet.getDouble("subtotal"),
                        resultSet.getDouble("precioVenta"),
                        resultSet.getString("idProducto"),
                        resultSet.getString("idVenta")
                );
            }

        } catch (Exception ex) {

            System.out.println("Error: " + ex.getMessage());
        }

        return detalleVenta;
    }

    @Override
    public ArrayList<DetalleVenta> obteners() {

        String query = "SELECT * FROM DetalleVenta";

        ArrayList<DetalleVenta> lista = new ArrayList<>();

        try {

            PreparedStatement preparedStatement = con.prepareStatement(query);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {

                DetalleVenta detalleVenta = new DetalleVenta(
                        resultSet.getString("id"),
                        resultSet.getLong("cantidad"),
                        resultSet.getDouble("subtotal"),
                        resultSet.getDouble("precioVenta"),
                        resultSet.getString("idProducto"),
                        resultSet.getString("idVenta")
                );

                lista.add(detalleVenta);
            }

        } catch (Exception ex) {

            System.out.println("Error: " + ex.getMessage());
        }

        return lista;
    }

    @Override
    public boolean actualizar(DetalleVenta detalleVenta) {

        String query =
                "UPDATE DetalleVenta "
                + "SET cantidad=?, subtotal=?, precioVenta=?, idProducto=?, idVenta=? "
                + "WHERE id=?";

        try {

            PreparedStatement preparedStatement = con.prepareStatement(query);

            preparedStatement.setLong(1, detalleVenta.getCantidad());
            preparedStatement.setDouble(2, detalleVenta.getSubtotal());
            preparedStatement.setDouble(3, detalleVenta.getPrecioVenta());
            preparedStatement.setString(4, detalleVenta.getIdProducto());
            preparedStatement.setString(5, detalleVenta.getIdVenta());
            preparedStatement.setString(6, detalleVenta.getId());

            return preparedStatement.executeUpdate() >= 1;

        } catch (Exception ex) {

            System.out.println("Error: " + ex.getMessage());
        }

        return false;
    }

    @Override
    public void cerrarConexion() {

        try {

            con.close();

        } catch (Exception ex) {

            System.out.println("Error: " + ex.getMessage());
        }
    }
}
