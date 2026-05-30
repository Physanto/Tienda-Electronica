package CapaDatos.Logica_Conexion;

import CapaLogicaNegocio.Excepciones.ExcepcionSQL;

import java.util.ArrayList;

/**
 * Intefaz/contrato que se define para toda la implementacion del CRUD en la base de datos local,
 * esta se hace generica para la reutilizacion del codigo y minimizar la complejidad de manejar varias interfaces
 *
 * @author Santiago Lopez
 * @author Manuel Figueroa (Physanto)
 */
public interface ILocalDAO<T> {
    
    public boolean agregar(T object) throws ExcepcionSQL;
    public boolean eliminar(String id) throws ExcepcionSQL;
    public T obtener(String id) throws ExcepcionSQL;
    public ArrayList<T> obteners() throws ExcepcionSQL;
    public boolean actualizar(T object) throws ExcepcionSQL;
}
