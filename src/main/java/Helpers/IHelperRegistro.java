package Helpers;

import Logica_Negocio.Cliente;

/**
 *
 * @author Santiago Lopez
 */

/**
 * Intefaz/Contrato
 */
public interface IHelperRegistro {
  
    public <T> boolean registrarNube(T object);
    //public void eliminarCliente(Cliente cliente, String id);
}
