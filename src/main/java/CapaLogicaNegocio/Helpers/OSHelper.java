package CapaLogicaNegocio.Helpers;

import javax.swing.ImageIcon;
import java.io.File;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Container;
/**
 * OSHelper — Utilidad multiplataforma para manejo de rutas y recursos del proyecto.
 *
 * <p>Esta clase centraliza toda la lógica de detección de sistema operativo y
 * construcción de rutas, garantizando compatibilidad entre Windows, Linux y macOS
 * sin necesidad de hardcodear separadores ni rutas absolutas.</p>
 *
 * <p><b>Uso recomendado en JFrames:</b></p>
 * <pre>
 *   // Cargar imagen en un JLabel
 *   String ruta = OSHelper.getImagePath("logo.png");
 *   ImageIcon icon = new ImageIcon(ruta);
 *   jLabel.setIcon(icon);
 *
 *   // Cargar imagen .jpg por nombre base
 *   String ruta = OSHelper.getImageFilePath("perfil"); // → .../Images/perfil.jpg
 * </pre>
 *
 * <p><b>Nota:</b> Esta clase no debe instanciarse. Todos sus métodos son estáticos.</p>
 *
 * @author Marlon Vargas
 */
public class OSHelper {

    private static final boolean ES_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");

    private OSHelper() { }
    
    /**
     * Devuelve la ruta completa de una imagen en la carpeta Images, ajustando el separador según el sistema operativo.
     *
     * @param nombreBase nombre del archivo sin extensión (ej: "Logo_Inicio")
     * @return ruta absoluta de la imagen con extensión .png
     */
    public static String getImageFilePath(String nombreBase) {
        // Obtiene la ruta base del proyecto (directorio actual)
        String basePath = System.getProperty("user.dir");

        // Construye la ruta con separadores compatibles
        String ruta = basePath + File.separator + "Images" + File.separator + nombreBase + ".png";

        // Ajusta separadores según el SO
        return ajustarSeparador(ruta);
    }


    /**
     * Recorre todos los JLabel del formulario, detecta los que tienen una imagen
     * cargada desde la carpeta Images y corrige el separador de ruta segun el SO.
     * Llamar una vez al final del constructor del formulario, despues de initComponents().
     *
     * @param formulario el JFrame o contenedor del formulario (pasar "this")
     */
    public static void ajustarImagenes(Container formulario) {
        for (Component comp : formulario.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getIcon() instanceof ImageIcon) {
                    ImageIcon icono = (ImageIcon) label.getIcon();
                    String ruta = icono.getDescription();
                    if (ruta != null && ruta.contains("Images")) {
                        label.setIcon(new ImageIcon(ajustarSeparador(ruta)));
                    }
                }
            }
            if (comp instanceof Container) {
                ajustarImagenes((Container) comp);
            }
        }
    }

    /**
     * ajusta el separador dependiendo del sistema operativo
     * @param ruta ruta de la imagen
     * @return la ruta de forma correcta
     */
    private static String ajustarSeparador(String ruta) {
        if (ES_WINDOWS) {
            return ruta.replace("/", "\\");
        }
        return ruta.replace("\\", "/");
    }
}