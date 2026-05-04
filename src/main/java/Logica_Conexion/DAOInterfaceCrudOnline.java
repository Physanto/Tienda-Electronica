package Logica_Conexion;

import java.util.ArrayList;
import java.util.Map;

public interface DAOInterfaceCrudOnline<T> {

   public boolean agregar(String documento, String coleccion, Map<String, Object> datos);
   public boolean eliminar(int id);
   public ArrayList<T> obtener(String id);
   public ArrayList<T> obteners();
   public boolean actualizar(T object);
}
