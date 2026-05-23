package CapaLogicaNegocio.Helpers;

import CapaLogicaNegocio.Excepciones.ExcepcionSQL;

import java.sql.SQLException;

public class HelperExcepciones {

    public static void capturarExSQL(SQLException sqlException) throws ExcepcionSQL {

        int codigoError = sqlException.getErrorCode();

        switch (codigoError){
            case 1062:
                throw new ExcepcionSQL("El registro ya existe");
            case 1045:
                throw new ExcepcionSQL("Error de autenticación: Credenciales de BD inválidas.");
            case 1049:
                throw new ExcepcionSQL("Error de conexión: La base de datos no fue encontrada.");
            case 1146:
                throw new ExcepcionSQL("Error de estructura: La tabla consultada no existe.");
            case 0:
                throw new ExcepcionSQL("Error de red: El servidor de base de datos no responde. Revise puertos y conexión.");
            default:
                throw new ExcepcionSQL("Error no catalogado en BD: " + sqlException.getMessage());
        }
    }
}
