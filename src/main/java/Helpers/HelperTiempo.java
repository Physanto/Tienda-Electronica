/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Helpers;

/**
 *
 * @author Santiago Lopez
 */
public class HelperTiempo {
     public static void RetornarTiempo(long fin, long inicio)
    {
      double tiempo= (double) (fin-inicio);
      System.out.println("Tiempo en milisegundos" + tiempo);
      System.out.println("El tiempo que se demoro fue:"+"\t"+tiempo/1000+"\t"+"En segundos");
    }  
}
