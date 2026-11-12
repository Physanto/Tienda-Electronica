package CapaDatos.Logica_Conexion;

import CapaLogicaNegocio.DTOS.PromocionesDTO;
import CapaLogicaNegocio.DTOS.AnalisisCliente;
import CapaLogicaNegocio.Logica_Negocio.Promocion;
import CapaLogicaNegocio.DTOS.Promociones;

import java.sql.*;
import java.util.ArrayList;

public class PromocionDAO {

    public ArrayList<Promocion> getDataset() {
        String query = "SELECT p.id, p.stock, " +
                "COALESCE(GREATEST(0, TIMESTAMPDIFF(DAY, CAST(MAX(v.fechaVenta) AS DATE), CURDATE())), 999) AS diasSinVender, " +
                "COALESCE(SUM(dv.cantidad), 0) AS totalVendido " +
                "FROM Producto p " +
                "LEFT JOIN DetalleVenta dv ON p.id = dv.idProducto " +
                "LEFT JOIN Venta v ON dv.idVenta = v.id " +
                "GROUP BY p.id, p.stock";

        ArrayList<Promocion> listaPromocion = new ArrayList<>();

        Connection conexion = Conexion.getConexionLocal();

        if (conexion == null) throw new IllegalStateException("No se pudo abrir la base local");

        try (PreparedStatement preparedStatement = conexion.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Promocion promocion = new Promocion(resultSet.getString("id"), resultSet.getDouble("stock"),
                        resultSet.getDouble("diasSinVender"), resultSet.getDouble("totalVendido"), -1);

                listaPromocion.add(promocion);
            }
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudieron consultar los datos de promociones", ex);
        }
        return listaPromocion;
    }

    public ArrayList<PromocionesDTO.PromocionAplicadaDTO> datosPromociones() {
        String query = "SELECT p.id, p.nombre, p.marca, p.stock, p.precioActual, " +
                "COALESCE(GREATEST(0, TIMESTAMPDIFF(DAY, CAST(MAX(v.fechaVenta) AS DATE), CURDATE())), 999) AS diasSinVender, " +
                "COALESCE(SUM(dv.cantidad), 0) AS totalVendido " +
                "FROM Producto p " +
                "LEFT JOIN DetalleVenta dv ON p.id = dv.idProducto " +
                "LEFT JOIN Venta v ON dv.idVenta = v.id " +
                "GROUP BY p.id, p.nombre, p.marca, p.stock, p.precioActual";

        ArrayList<PromocionesDTO.PromocionAplicadaDTO> listaPromocion = new ArrayList<>();

        Connection conexion = Conexion.getConexionLocal();

        if (conexion == null) throw new IllegalStateException("No se pudo abrir la base local");

        try (PreparedStatement preparedStatement = conexion.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                PromocionesDTO.PromocionAplicadaDTO promocion = new PromocionesDTO.PromocionAplicadaDTO(
                        resultSet.getString("id"), resultSet.getString("nombre"),
                        resultSet.getString("marca"), String.valueOf(resultSet.getLong("stock")),
                        String.valueOf(resultSet.getDouble("precioActual")),
                        String.valueOf(resultSet.getLong("diasSinVender")),
                        String.valueOf(resultSet.getDouble("totalVendido")));

                listaPromocion.add(promocion);
            }
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudieron consultar los datos de promociones", ex);
        }
        return listaPromocion;
    }

    public boolean agregarPromocionPersonalizada(Promociones promociones) {
        String query = "INSERT INTO Promocion (id, nombre, descuento, fechaInicio, fechaFin, tipo) " +
                "VALUES (?, ?, ?, ?, ?, ?);";

        Connection conexion = Conexion.getConexionLocal();
        if (conexion == null)
            return false;

        try (PreparedStatement preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, promociones.getId());
            preparedStatement.setString(2, promociones.getNombre());
            preparedStatement.setDouble(3, promociones.getDescuento());
            preparedStatement.setTimestamp(4, new Timestamp(promociones.getFechaInicio().getTime()));
            preparedStatement.setTimestamp(5, new Timestamp(promociones.getFechaFin().getTime()));
            preparedStatement.setString(6, promociones.getTipo().name());

            return preparedStatement.executeUpdate() >= 1;
        } catch (Exception e) {
            System.out.println("Error consulta PromocionDAO, agregar" + e.getMessage());
        }
        return false;
    }

    public ArrayList<Promociones> obtenerPromocionesPersonalizadas() {
        String query = "SELECT p.*, pp.idProducto, pc.idCliente FROM Promocion p "
                + "LEFT JOIN PromocionProducto pp ON pp.idPromocion=p.id "
                + "LEFT JOIN PromocionCliente pc ON pc.idPromocion=p.id ORDER BY p.fechaInicio, p.id";

        ArrayList<Promociones> listaPromociones = new ArrayList<>();

        Connection conexion = Conexion.getConexionLocal();
        if (conexion == null) throw new IllegalStateException("No se pudo abrir la base local");

        try (PreparedStatement preparedStatement = conexion.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {

                Promociones promociones = new Promociones(resultSet.getString("id"), resultSet.getString("nombre"),
                        resultSet.getDouble("descuento"), resultSet.getTimestamp("fechaInicio"),
                        resultSet.getTimestamp("fechaFin"),
                        Promociones.TipoPromocion.valueOf(resultSet.getString("tipo").toUpperCase()));
                promociones.setIdProducto(resultSet.getString("idProducto"));
                promociones.setIdCliente(resultSet.getString("idCliente"));
                listaPromociones.add(promociones);
            }
        } catch (Exception e) {
            throw new IllegalStateException("No se pudieron consultar los datos de promociones", e);
        }
        return listaPromociones;
    }

    public boolean desactivarPromocion(java.util.Date fechaActual, String idPromocion) {
        String query = "UPDATE Promocion SET fechaFin=? WHERE id = ?";

        Connection conexion = Conexion.getConexionLocal();
        if (conexion == null)
            return false;

        try (PreparedStatement preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setTimestamp(1, new Timestamp(fechaActual.getTime()));
            preparedStatement.setString(2, idPromocion);

            return preparedStatement.executeUpdate() >= 1;
        } catch (Exception e) {
            System.out.println("Error consulta PromocionDAO, desactivando" + e.getMessage());
        }
        return false;
    }

    public boolean agregarPromocionCliente(PromocionesDTO.PromocionClienteDTO promocionClienteDTO) {
        String query = "INSERT INTO PromocionCliente(id, idPromocion, idCliente) " +
                "VALUES (?, ?, ?);";

        Connection conexion = Conexion.getConexionLocal();
        if (conexion == null)
            return false;

        try (PreparedStatement preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, promocionClienteDTO.id());
            preparedStatement.setString(2, promocionClienteDTO.idPromocion());
            preparedStatement.setString(3, promocionClienteDTO.idCliente());

            return preparedStatement.executeUpdate() >= 1;
        } catch (Exception e) {
            System.out.println("Error consulta PromocionDAO" + e.getMessage());
        }
        return false;
    }

    public ArrayList<PromocionesDTO.PromocioPersonalizadaCliente> obtenerResumeComprasCliente() {
        String query = "SELECT c.id, c.nombre, c.apellido, c.cedula, " +
                "COALESCE(SUM(v.totalVenta), 0) AS totalComprado, " +
                "TIMESTAMPDIFF(DAY, CAST(MAX(v.fechaVenta) AS DATE), CURDATE()) AS ultimaCompra " +
                "FROM Cliente c " +
                "LEFT JOIN Venta v ON v.idCliente = c.id " +
                "GROUP BY c.id, c.nombre, c.apellido, c.cedula";

        ArrayList<PromocionesDTO.PromocioPersonalizadaCliente> listaPromociones = new ArrayList<>();

        Connection conexion = Conexion.getConexionLocal();
        if (conexion == null) throw new IllegalStateException("No se pudo abrir la base local");

        try (PreparedStatement preparedStatement = conexion.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                PromocionesDTO.PromocioPersonalizadaCliente promociones = new PromocionesDTO.PromocioPersonalizadaCliente(
                        resultSet.getString("id"),
                        resultSet.getString("nombre"), resultSet.getString("apellido"),
                        resultSet.getString("cedula"), resultSet.getString("totalComprado"),
                        resultSet.getString("ultimaCompra"));
                listaPromociones.add(promociones);
            }
        } catch (Exception e) {
            throw new IllegalStateException("No se pudieron consultar los datos de promociones", e);
        }
        return listaPromociones;
    }

    public boolean agregarPromocionProducto(PromocionesDTO.PromocionProductoDTO promocionProductoDTO) {
        String query = "INSERT INTO PromocionProducto (id, idPromocion, idProducto) " +
                "VALUES (?, ?, ?);";

        Connection conexion = Conexion.getConexionLocal();
        if (conexion == null)
            return false;

        try (PreparedStatement preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, promocionProductoDTO.id());
            preparedStatement.setString(2, promocionProductoDTO.idPromocion());
            preparedStatement.setString(3, promocionProductoDTO.idProducto());

            return preparedStatement.executeUpdate() >= 1;
        } catch (Exception e) {
            System.out.println("Error consulta PromocionDAO" + e.getMessage());
        }
        return false;
    }

    public ArrayList<AnalisisCliente> getDatasetClientes() {
        String query = "SELECT c.id, " +
                " COALESCE(TIMESTAMPDIFF(DAY, CAST(MAX(v.fechaVenta) AS DATE), CURDATE()), 999) AS recencia, " +
                " COUNT(v.id) AS frecuencia, " +
                " COALESCE(SUM(v.totalVenta), 0) AS monetario FROM Cliente c " +
                " LEFT JOIN Venta v ON c.id = v.idCliente" +
                " GROUP BY c.id ORDER BY c.id;";
        ArrayList<AnalisisCliente> listaPromociones = new ArrayList<>();

        Connection conexion = Conexion.getConexionLocal();
        if (conexion == null) throw new IllegalStateException("No se pudo abrir la base local");

        try (PreparedStatement preparedStatement = conexion.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {

                AnalisisCliente analisisCliente = new AnalisisCliente(resultSet.getString("id"),
                        resultSet.getDouble("recencia"), resultSet.getDouble("frecuencia"),
                        resultSet.getDouble("monetario"));
                listaPromociones.add(analisisCliente);
            }
        } catch (Exception e) {
            throw new IllegalStateException("No se pudieron consultar los datos de promociones", e);
        }
        return listaPromociones;
    }
    /** Bloquea el destino para serializar promociones concurrentes en la misma base. */
    public boolean destinoDisponible(String id, boolean cliente, java.util.Date inicio, java.util.Date fin) {
        Connection conexion = Conexion.getConexionLocal();
        if (conexion == null) return false;
        String tabla = cliente ? "Cliente" : "Producto";
        String relacion = cliente ? "PromocionCliente" : "PromocionProducto";
        String campo = cliente ? "idCliente" : "idProducto";
        try (PreparedStatement bloqueo = conexion.prepareStatement("SELECT id FROM " + tabla + " WHERE id=? FOR UPDATE")) {
            bloqueo.setString(1, id);
            try (ResultSet datos = bloqueo.executeQuery()) { if (!datos.next()) return false; }
            try (PreparedStatement consulta = conexion.prepareStatement("SELECT p.id FROM Promocion p JOIN "
                    + relacion + " r ON r.idPromocion=p.id WHERE r." + campo
                    + "=? AND p.fechaInicio < ? AND p.fechaFin > ? AND p.fechaFin > CURRENT_TIMESTAMP")) {
                consulta.setString(1, id);
                consulta.setTimestamp(2, new Timestamp(fin.getTime()));
                consulta.setTimestamp(3, new Timestamp(inicio.getTime()));
                try (ResultSet datos = consulta.executeQuery()) { return !datos.next(); }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo verificar el destino de la promoción", e);
        }
    }
}
