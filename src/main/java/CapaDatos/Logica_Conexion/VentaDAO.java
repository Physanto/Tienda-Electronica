/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CapaDatos.Logica_Conexion;

import CapaLogicaNegocio.Logica_Negocio.Venta;

import java.sql.Connection;
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

    public static Connection con = Conexion.getConnection();

    /**
     * Agrega una nueva venta a la base de datos.
     */
    @Override
    public boolean agregar(Venta venta) {

        String query =
                "INSERT INTO Venta (id,fechaVenta,totalVenta,metodoPago,idCliente) "
                + "VALUES (?, ?, ?, ?, ?)";

        try {

            PreparedStatement preparedStatement = con.prepareStatement(query);

            preparedStatement.setString(1, venta.getId());

            preparedStatement.setDate(
                    2,
                    new java.sql.Date(venta.getFechaVenta().getTime())
            );

            preparedStatement.setDouble(3, venta.getTotalVenta());

            preparedStatement.setString(
                    4,
                    venta.getMetodoPago().toString()
            );

            preparedStatement.setString(5, venta.getIdCliente());

            return preparedStatement.executeUpdate() >= 1;

        } catch (Exception ex) {

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

        try {

            PreparedStatement preparedStatement = con.prepareStatement(query);

            preparedStatement.setString(1, id);

            return preparedStatement.executeUpdate() >= 1;

        } catch (Exception ex) {

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

        } catch (Exception ex) {

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

        } catch (Exception ex) {

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

        try {

            PreparedStatement preparedStatement = con.prepareStatement(query);

            preparedStatement.setDate(
                    1,
                    new java.sql.Date(venta.getFechaVenta().getTime())
            );

            preparedStatement.setDouble(2, venta.getTotalVenta());

            preparedStatement.setString(
                    3,
                    venta.getMetodoPago().toString()
            );

            preparedStatement.setString(4, venta.getIdCliente());

            preparedStatement.setString(5, venta.getId());

            return preparedStatement.executeUpdate() >= 1;

        } catch (Exception ex) {

            System.out.println("Error: " + ex.getMessage());
        }

        return false;
    }

    /**
     * Cierra la conexion con la base de datos.
     */
    @Override
    public void cerrarConexion() {

        try {

            con.close();

        } catch (Exception ex) {

            System.out.println("Error: " + ex.getMessage());
        }
    }
}
