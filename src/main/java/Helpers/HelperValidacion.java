package Helpers;


// Se pueden simplificar los metodos de validacion que estan al final
// se pueden hacer 2 metodos que sean genericos
// se puede optimizar un poco el metodo de validacion de caracteres especiales

import java.util.ArrayList;
/**
 *
 * @author Santiago Lopez
 */

/**
 * Clase que se encarga de hacer las validaciones a los campos correspondientes
 */
public class HelperValidacion {

    /**
     * Verifica si la cadena pasada por argumento contiene numeros
     * @param cadena es la cadena que se quiere verificar
     * @return 0 si la cadena no contiene numeros, 1 si contiene al menos un numero
     */
    public static int RetornarValor(String cadena) {
        for (int i = 0; i < cadena.length(); i++) {
            if (Character.isDigit(cadena.charAt(i))) { return 1; }
        }
        return 0;
    }

    /**
     * Verifica si la cadena pasada por argumento contiene caracteres especiales
     * @param cadena es la cadena que se quirer verificar
     * @return 0 si la cadena no contiene caracteres especiales, 1 si contiene al menos un caracter especial
     */
    public static int RetornarCEV2(String cadena) {
        //se podria usar un Set para hacer esta validacion ya que este tiene una complejidad de O(1)
        char[] listaCaracteres = new char[7];
        listaCaracteres[0] = '@';
        listaCaracteres[1] = '/';
        listaCaracteres[2] = ';';
        listaCaracteres[3] = ':';
        listaCaracteres[4] = '"';
        listaCaracteres[5] = '!';
        //listaCaracteres[6] = ' ';

        for (int i = 0; i < cadena.length(); i++) {
             if(!Character.isLetter(cadena.charAt(i)) && !Character.isDigit(cadena.charAt(i))) {

                 for(Character c : listaCaracteres){
                    if(c.equals(cadena.charAt(i))){ return 1; }
                 }
             }
        }
        return 0;
    }

    /**
     * Verifica si la cadena pasada por argumento esta vacia
     * @param cadena es la cadena que se quiere validar
     * @return 1 si esta vacia, 0 si no esta vacia
     */
    public static int ValidarVacio(String cadena) {
        return cadena.isEmpty() ? 1 : 0;
    }

    /**
     * Verifica si la cadena pasada por argumento contiene letras
     * @param cadena es la cadena que se quiere verificar
     * @return 0 si la cadena no contiene letras, 1 si contiene al menos una letra
     */
    public static int RetornarLetra(String cadena ) {
        for (int i = 0; i < cadena.length(); i++) {
             if(Character.isLetter(cadena.charAt(i))) {
                return 1;
             }
        }
        return 0;
    }

    /**
     * Verifica si la cantidad pasada por argumento esta en el rango de 1 y 999
     * @param cantidad es el valor que se quiere verificar
     * @return 1 si la cantidad esta en el rango, 0 sino lo esta
     */
    public static int ValidarCantidadRango(int cantidad) {
       return (cantidad > 0 && cantidad < 1000) ? 1 : 0;
    }

    /**
     * Verifica si la cadena pasada por argumento contiene caracteres especiales
     * @param cadena es la cadena que se quirer verificar
     * @return 0 si la cadena no contiene caracteres especiales, 1 si contiene al menos un caracter especial
     */
    public static int RetornarCEDireccionV2(String cadena) {
       return RetornarCEV2(cadena);
    }

    /**
     * Verifica si la cadena pasada por argumento contiene caracteres especiales
     * @param cadena es la cadena que se quiere verificar
     * @return 0 si la cadena no contiene caracteres especiales, 1 si contiene al menos un caracter especial
     */
    public static int RetornarCEV2Contrasenha(String cadena) {
        return RetornarCEV2(cadena);
    }

    /**
     * Valida si la cadena pasada por argumento no esta vacia, no contiene numeros y no contiene caracteres especiales
     * @param cadena es la cadena que se quiere verificar
     * @return 0 si cumple lo anterior, 1 si no lo cumple
     */
    public static int ValidarTodo(String cadena) {
        return ValidarVacio(cadena)+RetornarValor(cadena)+RetornarCEV2(cadena);
    }
    /**
     * Valida si la cadena pasada por argumento no esta vacia, no contiene letras y ademas no contiene caracteres especiales
     * @param cadena es la cadena que se quiere verificar
     * @return 0 si cumple lo anterior, 1 si no lo cumple
     */
    public static int ValidarTodoLetra(String cadena) {
       return ValidarVacio(cadena)+RetornarLetra(cadena)+RetornarCEV2(cadena);
    }
    /**
     * Valida si la cadena pasada por argumento no esta vacia y no contiene caracteres especiales
     * @param cadena es la cadena que se quiere verificar
     * @return 0 si cumple lo anterior, 1 si no lo cumple
     */
    public static int ValidarTodoDireccion(String cadena) {
       return ValidarVacio(cadena)+RetornarCEDireccionV2(cadena);
    }
    /**
     * Valida si la cadena pasada por argumento no esta vacia, y no contiene caracteres especiales
     * @param cadena es la cadena que se quiere verificar
     * @return 0 si cumple lo anterior, 1 si no lo cumple
     */
    public static int ValidarTodoSerial(String cadena) {
       return ValidarVacio(cadena)+RetornarCEV2(cadena);
    }
    /**
     * Valida si la cadena pasada por argumento no esta vacia y no contiene caracteres especiales
     * @param cadena es la cadena que se quiere verificar
     * @return 0 si cumple lo anterior, 1 si no lo cumple
     */
    public static int ValidarTodoContrasenha(String cadena) {
       return ValidarVacio(cadena)+RetornarCEV2Contrasenha(cadena);
    }
}

