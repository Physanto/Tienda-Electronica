/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logica_Negocio;

import Helpers.HelperCifrado;

/**
 *
 * @author Santiago Lopez Patron Template Method
 */
public abstract class Usuario {
    
    private String usu;
    private String contra;

    public Usuario(String usu, String contra) {
        this.usu = usu;
        this.contra = contra;
    }

    public String getUsu() {
        return usu;
    }

    public void setUsu(String usu) {
        this.usu = usu;
    }

    public String getContra() {
        return contra;
    }

    public void setContra(String contra) {
        this.contra = contra;
    }
    
    
    
    
    public abstract boolean  LogOn(String usuario, String contraseña);
    
      
    
    
    



}
