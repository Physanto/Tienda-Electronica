/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Helpers;

import java.util.ArrayList;

/**
 *
 * @author Santiago Lopez
 */
public class HelperValidacion {
    
    
      public static int RetornarValor(String nombre)
    {
        int numero=0;
        int letra=0;

        
        for (int j = 0; j < nombre.length(); j++) {
             boolean flag = Character.isDigit(nombre.charAt(j));
             if(flag) {
                //System.out.println("'"+ nombre.charAt(j)+"' is a number");
                numero++;
             }
             else {
                //System.out.println("'"+ nombre.charAt(j)+"' is a letter");
                letra++;
             }

        }
        return numero;
    }
     
      public static int RetornarCEV2(String nombre)
    {
        int ce=0;
        
        ArrayList<Character> lscaracteres = new ArrayList<>();
        
        lscaracteres.add('@');
        lscaracteres.add('~');
        lscaracteres.add('/');
        lscaracteres.add(';');
        lscaracteres.add(':');
        lscaracteres.add('"');
        lscaracteres.add('!');
        lscaracteres.add(' ');

        for (int j = 0; j < nombre.length(); j++) {
             boolean flag = Character.isLetter(nombre.charAt(j));
             if(!flag) {
                 for (int i = 0; i < lscaracteres.size(); i++) {
                      
                     if(lscaracteres.get(i).compareTo(nombre.charAt(j))==0)
                     {
                        ce++;
                     }
                 }
             }
        }
        return ce;
    }
      
      public static int ValidarVacio(String cadena) {
        if (cadena.equals("")) {
            return 1;
        } else {
            return 0;
        }

    }
      
      
       public static int RetornarLetra(String nombre)
    {
        int numero=0;
        int letra=0;

        
        for (int j = 0; j < nombre.length(); j++) {
             boolean flag = Character.isDigit(nombre.charAt(j));
             if(flag) {
                //System.out.println("'"+ nombre.charAt(j)+"' is a number");
                numero++;
             }
             else {
                //System.out.println("'"+ nombre.charAt(j)+"' is a letter");
                letra++;
             }

        }
        return letra;
    }
       
   public static int ValidarCantidadRango(int cantidad)
   {
       if(cantidad>0 && cantidad<1000){
           return 1;
       }
      else
       {
           return 0;
       }
   }
   
     public static int RetornarCEDireccionV2(String nombre)
    {
        int ce=0;
        
        ArrayList<Character> lscaracteres = new ArrayList<>();
        
        lscaracteres.add('@');
        lscaracteres.add('/');
        lscaracteres.add(';');
        lscaracteres.add(':');
        lscaracteres.add('"');
        lscaracteres.add('!');

        for (int j = 0; j < nombre.length(); j++) {
             boolean flag = Character.isLetter(nombre.charAt(j));
             if(!flag) {
                 for (int i = 0; i < lscaracteres.size(); i++) {
                      
                     if(lscaracteres.get(i).compareTo(nombre.charAt(j))==0)
                     {
                        ce++;
                     }
                 }
             }
        }
        return ce;
    }
     
       public static int RetornarCEV2Contraseña(String nombre)
    {
        int ce=0;
        
        ArrayList<Character> lscaracteres = new ArrayList<>();
        
       
     
        lscaracteres.add('/');
        lscaracteres.add(';');
        lscaracteres.add(':');
        lscaracteres.add('"');
        lscaracteres.add('!');
        lscaracteres.add(' ');

        for (int j = 0; j < nombre.length(); j++) {
             boolean flag = Character.isLetter(nombre.charAt(j));
             if(!flag) {
                 for (int i = 0; i < lscaracteres.size(); i++) {
                      
                     if(lscaracteres.get(i).compareTo(nombre.charAt(j))==0)
                     {
                        ce++;
                     }
                 }
             }
        }
        return ce;
    }
     
      public static int ValidarTodo(String cadena)
   {
       int conteo=ValidarVacio(cadena)+RetornarValor(cadena)+RetornarCEV2(cadena);
       return conteo;
   }
   
    public static int ValidarTodoLetra(String cadena)
   {
       int conteo=ValidarVacio(cadena)+RetornarLetra(cadena)+RetornarCEV2(cadena);
       return conteo;
   }
    
    public static int ValidarTodoDireccion(String cadena)
   {
       int conteo=ValidarVacio(cadena)+RetornarCEDireccionV2(cadena);
       return conteo;
   }
    
      public static int ValidarTodoSerial(String cadena)
   {
       int conteo=ValidarVacio(cadena)+RetornarCEV2(cadena);
       return conteo;
   }
      
          public static int ValidarTodoContraseña(String cadena)
   {
       int conteo=ValidarVacio(cadena)+RetornarCEV2Contraseña(cadena);
       return conteo;
   }
}

