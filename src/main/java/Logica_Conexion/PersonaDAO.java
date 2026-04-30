package Logica_Conexion;

import Logica_Negocio.Persona;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Santiago Lopez
 */
public class PersonaDAO implements DAOInterfacePersona {

    public static Connection con = Conexion.getConnection();

    @Override
    public int add(Persona per) throws SQLException {
        String query
                = "INSERT INTO persona(Uid,Nombre,Apellido, Direccion,Cedula,Producto,Nom_img, estado)"
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, per.getUid());
        ps.setString(2, per.getNombre());
        ps.setString(3, per.getApellido());
        ps.setString(4, per.getDireccion());
        ps.setString(5, per.getCedula());
        ps.setString(6, per.getProducto());
        ps.setString(7, per.getNom_img());
        ps.setString(8, Character.toString(per.getEstado()));

        return ps.executeUpdate();
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String query
            = "delete from persona where Uid =?";
        PreparedStatement ps
            = con.prepareStatement(query);
        ps.setString(1, id);
       int n= ps.executeUpdate();

       return n >= 1;
    }

    @Override
    public Persona getPersona(String id) throws SQLException {
        String query = "SELECT * FROM persona WHERE Uid = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();
        Persona persona = null;

        while (rs.next()) {
            persona = new Persona(rs.getString("Uid"),
                    rs.getString("Nombre"),
                    rs.getString("Apellido"),
                    rs.getString("Cedula"),
                    rs.getString("Direccion"),
                    rs.getString("Producto"),
                    rs.getString("Nom_img"),
                    rs.getString("estado").charAt(0));
        }
        return persona;
    }

    @Override
    public ArrayList<Persona> getPersona() throws SQLException {
        String query = "SELECT * FROM persona";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        ArrayList<Persona> listaPersonas = new ArrayList<>();
 
        while (rs.next()) {
            Persona persona = new Persona(
                    rs.getString("Uid"),
                    rs.getString("Nombre"),
                    rs.getString("Apellido"),
                    rs.getString("Cedula"),
                    rs.getString("Direccion"),
                    rs.getString("Producto"),
                    rs.getString("Nom_img"),
                    rs.getString("estado").charAt(0));
            listaPersonas.add(persona);
        }
        return listaPersonas;
    }

    @Override
    public void update(Persona per) throws SQLException {
         String query
            = "update persona set Nombre=?,Apellido=?,Direccion=?,Cedula=?,Producto=?,Nom_img=?, estado=?"
              + "where Uid=?";
            PreparedStatement ps = con.prepareStatement(query);
       
        ps.setString(1, per.getNombre());
        ps.setString(2, per.getApellido());
        ps.setString(3, per.getDireccion());
        ps.setString(4, per.getCedula());
        ps.setString(5, per.getProducto());
        ps.setString(6, per.getNom_img());
        ps.setString(7, per.getUid());
        ps.setString(8, Character.toString(per.getEstado()));
        ps.executeUpdate();
    }

    public ArrayList<Persona> getNoSincronizados() throws SQLException{
        String query = "SELECT * FROM persona WHERE estado = 0";
        PreparedStatement preparedStatement = con.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Persona> listaPersonas = new ArrayList<>();

        while(resultSet.next()){
            Persona persona = new Persona(resultSet.getString("Uid"),
                    resultSet.getString("Nombre"),
                    resultSet.getString("Apellido"),
                    resultSet.getString("Cedula"),
                    resultSet.getString("Direccion"),
                    resultSet.getString("Producto"),
                    resultSet.getString("Nom_img"),
                    resultSet.getString("estado").charAt(0));
            listaPersonas.add(persona);
        }
        return listaPersonas;
    }

    public void marcarSincronizado(String Uid) throws SQLException {
        String query = "UPDATE persona SET estado = 1 WHERE Uid = ?";
        PreparedStatement preparedStatement = con.prepareStatement(query);
        preparedStatement.setString(1,Uid);
        preparedStatement.executeUpdate();
    }

    @Override
    public void close() throws SQLException { con.close(); }
}
