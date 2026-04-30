package Helpers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 *
 * @author Santiago Lopez
 */

/**
 * Clase la cual se encarga de hacer el cifrado de datos
 */
public class HelperCifrado {

    /**
     * Cifra el mensaje pasado por argumento
     * @param mensaje mensaje a encriptar
     * @return el mensaje cifrado
     */
    public static String CifrarSHA256(String mensaje) {

        try{
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] digests = sha.digest(mensaje.getBytes());
            StringBuilder hexString = new StringBuilder();

            for(byte b : digests) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        }
        catch( NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
