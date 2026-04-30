package Helpers;

import Logica_Conexion.Conexion;
import Logica_Conexion.PersonaDAO;
import Logica_Conexion.PersonaProvider;
import Logica_Negocio.Persona;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HelperGestorBD {

    public static void GuardarPersonaGeneral(Persona persona, int id, String producto){

        boolean online = HelperMonitorRed.estaUsandoNube();

        try{
            PersonaDAO personaDAO = new PersonaDAO();
            personaDAO.add(persona);

            if(online){
                //aqui podemos usar el metodo de guardarPersonaNube pero se debe modificar
                // esto con el fin de no repetir codigo aqui
                Map<String, Object> datos = new HashMap<>();
                datos.put("uid", persona.getUid());
                datos.put("Nombre", persona.getNombre());
                datos.put("Apellido", persona.getApellido());
                datos.put("Direccion", persona.getDireccion());
                datos.put("Cedula", persona.getCedula());
                datos.put("Productos", producto);
                datos.put("Nom_img", persona.getNom_img());
                boolean exito = PersonaProvider.GuardarPersona("persona", String.valueOf(id), datos);

                if (exito) {
                    personaDAO.marcarSincronizado(persona.getUid());
                    System.out.println("Guardado exitoso en Local y Nube.");
                }
                else {
                    System.out.println("Fallo en Firebase. El registro se quedó en la cola local (Estado 0).");
                }
            }
            else {
                System.out.println("Modo Offline activo. Guardado solo en Local (Estado 0).");
            }
        }
        catch(Exception e){
            System.out.println("Error faltal, guardando datos " + e.getMessage());
        }
    }

    public static ArrayList<Persona> CargarPersonaGeneral(){
        ArrayList<Persona> listaPersonas = new ArrayList<>();
        boolean online = HelperMonitorRed.estaUsandoNube();

        try{
            if(online) {
                System.out.println("Listando desde la nube");
                listaPersonas = PersonaProvider.CargarInfoPersona();
            }
            else {
                PersonaDAO personaDAO = new PersonaDAO();
                listaPersonas = personaDAO.getPersona();
                System.out.println("Listando desde local");
            }
        }
        catch(Exception e){
            System.out.println("Error faltal listando los datos " + e.getMessage());
        }
        return  listaPersonas;
    }

    public static Persona CargarPersonaGeneral(String codigo){
        Persona persona = new Persona();
        PersonaDAO personaDAO = new PersonaDAO();
        boolean online = HelperMonitorRed.estaUsandoNube();

        try{
            if(online) {
                System.out.println("Listando desde la nube");
                persona = PersonaProvider.CargarInfoPersonaCodigo(codigo);
            }
            else {
                persona = personaDAO.getPersona(codigo);
                System.out.println("Listando desde local");
            }
        }
        catch(Exception e){
            System.out.println("Error faltal listando los datos " + e.getMessage());
        }
        return  persona;
    }

    public static boolean EliminarPersonaGeneral(String coleccion, String codigo){
        boolean exito = true;
        boolean online = HelperMonitorRed.estaUsandoNube();

        try{
            PersonaDAO personaDAO = new PersonaDAO();
            if(personaDAO.delete(codigo)){ System.out.println("eliminado desde local"); }

            if(online) {
                System.out.println("Sistema online y listo para eliminar");

                if(PersonaProvider.EliminarPersona(coleccion, codigo)){
                    System.out.println("Eliminado desde la nube.");
                }
                else{
                    System.out.println("Fallo en la eliminacion de la nube");
                }
            }
            else{
                System.out.println("No se elimino en nube ya que no hay conexion");
            }
        }
        catch(Exception e){
            System.out.println("Error eliminando los datos " + e.getMessage());
            exito = false;
        }
        return exito;
    }
}
