/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Logica_Conexion;

import Logica_Negocio.Persona;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 *
 * @author Santiago Lopez
 */
public interface DAOInterfacePersona {
    
    public int add(Persona per) throws SQLException;
    public int delete(String id) throws SQLException;
    public Persona getPersona(String id) throws SQLException;
    public ArrayList<Persona> getPersona() throws SQLException;
    public void update(Persona per) throws SQLException;
    public void close() throws SQLException;
    
}
