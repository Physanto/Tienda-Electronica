package Helpers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
 * @version 2.0
 */
public final class OSHelper {

    /** Nombre del sistema operativo en minúsculas, evaluado una sola vez al cargar la clase. */
    private static final String OS = System.getProperty("os.name", "unknown").toLowerCase();

    /** Nombre de la carpeta donde se almacenan los recursos de imagen del proyecto. */
    private static final String IMAGES_FOLDER = "Images";

    /** Extensión de imagen por defecto utilizada por {@link #getImageFilePath(String)}. */
    private static final String DEFAULT_IMAGE_EXT = ".jpg";

    /**
     * Constructor privado para prevenir instanciación accidental.
     * Esta clase está diseñada para usarse únicamente con métodos estáticos.
     */
    private OSHelper() {
        throw new UnsupportedOperationException("OSHelper es una clase utilitaria y no debe instanciarse.");
    }

    // -------------------------------------------------------------------------
    // Detección del sistema operativo
    // -------------------------------------------------------------------------

    /**
     * Determina si el sistema operativo actual es Windows.
     *
     * <p>Detecta cualquier variante de Windows (XP, 7, 10, 11, Server, etc.)
     * inspeccionando la propiedad del sistema {@code os.name}.</p>
     *
     * @return {@code true} si el SO es Windows; {@code false} en caso contrario.
     */
    public static boolean isWindows() {
        return OS.contains("win");
    }

    /**
     * Determina si el sistema operativo actual es Linux o Unix-like.
     *
     * <p>Cubre distribuciones estándar de Linux (Ubuntu, Fedora, Debian, etc.),
     * sistemas AIX y otros derivados de Unix que contengan "nix" o "nux" en su nombre.</p>
     *
     * @return {@code true} si el SO es Linux/Unix/AIX; {@code false} en caso contrario.
     */
    public static boolean isLinux() {
        return OS.contains("nix") || OS.contains("nux") || OS.contains("aix");
    }

    /**
     * Determina si el sistema operativo actual es macOS.
     *
     * <p>Detecta cualquier versión de macOS / OS X inspeccionando
     * la propiedad del sistema {@code os.name}.</p>
     *
     * @return {@code true} si el SO es macOS; {@code false} en caso contrario.
     */
    public static boolean isMac() {
        return OS.contains("mac");
    }

    /**
     * Retorna el nombre del sistema operativo actual tal como lo reporta la JVM.
     *
     * <p>Útil para mostrar información de diagnóstico en logs o ventanas de
     * configuración del sistema.</p>
     *
     * @return Cadena con el nombre del SO (p. ej. {@code "windows 11"}, {@code "linux"}).
     */
    public static String getOSName() {
        return OS;
    }

    /**
     * Obtiene la ruta base absoluta del directorio de ejecución del proyecto.
     *
     * <p>En entornos de desarrollo (NetBeans, IntelliJ, Eclipse) apunta al directorio
     * raíz del proyecto. En producción (JAR empaquetado) apunta al directorio donde
     * reside el JAR. El separador de ruta es ajustado automáticamente por el SO.</p>
     *
     * @return Ruta absoluta como {@code String} sin separador final.
     */
    public static String getBasePath() {
        try {
            // Intentar obtener la ruta desde el JAR ejecutable (producción)
            Path jarPath = Paths.get(
                OSHelper.class.getProtectionDomain().getCodeSource().getLocation().toURI()
            );
            Path parent = jarPath.getParent();
            if (parent != null) {
                return parent.toAbsolutePath().toString();
            }
        } catch (URISyntaxException | SecurityException ignored) {
            // Fallback al directorio de trabajo actual (modo desarrollo)
        }
        return Paths.get("").toAbsolutePath().toString();
    }

    /**
     * Construye la ruta absoluta completa hacia un archivo dentro de la carpeta {@code Images/}.
     *
     * <p>Si la carpeta {@code Images/} no existe en el directorio base, la crea
     * automáticamente (incluyendo subdirectorios intermedios si fuera necesario).
     * El separador de carpetas es resuelto automáticamente por el SO.</p>
     *
     * @param imageName Nombre completo del archivo de imagen, incluyendo extensión
     *                  (p. ej. {@code "logo.png"}, {@code "fondo.jpg"}).
     *                  No debe ser {@code null} ni vacío.
     * @return Ruta absoluta al archivo de imagen como {@code String}.
     *         Ejemplo: {@code "C:\Proyectos\MiApp\Images\logo.png"}
     * @throws IllegalArgumentException Si {@code imageName} es {@code null} o está vacío.
     */
    public static String getImagePath(String imageName) {
        if (imageName == null || imageName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la imagen no puede ser nulo o vacío.");
        }

        Path imagesDir = Paths.get(getBasePath(), IMAGES_FOLDER);

        // Crear el directorio si no existe (operación segura y atómica)
        if (!Files.exists(imagesDir)) {
            try {
                Files.createDirectories(imagesDir);
            } catch (IOException e) {
                System.err.println("[OSHelper] Advertencia: no se pudo crear el directorio Images. " + e.getMessage());
            }
        }

        return imagesDir.resolve(imageName).toString();
    }

    /**
     * Construye la ruta absoluta hacia una imagen {@code .jpg} dado su nombre base.
     *
     * <p>Método conveniente que delega en {@link #getImagePath(String)} agregando
     * automáticamente la extensión {@code .jpg}. Útil cuando todas las imágenes
     * del proyecto comparten el mismo formato.</p>
     *
     * @param fileName Nombre del archivo <b>sin extensión</b>
     *                 (p. ej. {@code "perfil"} → busca {@code Images/perfil.jpg}).
     *                 No debe ser {@code null} ni vacío.
     * @return Ruta absoluta al archivo {@code .jpg} correspondiente.
     * @throws IllegalArgumentException Si {@code fileName} es {@code null} o está vacío.
     */
    public static String getImageFilePath(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede ser nulo o vacío.");
        }
        // Probar primero .jpg, luego .png; retorna el primero que exista en disco
        for (String ext : new String[]{".jpg", ".png", ".jpeg", ".gif"}) {
            String path = getImagePath(fileName + ext);
            if (Files.isRegularFile(Paths.get(path))) {
                return path;
            }
        }
        // Si ninguno existe, retorna .jpg por defecto (comportamiento original)
        return getImagePath(fileName + DEFAULT_IMAGE_EXT);
    }

    /**
     * Construye la ruta absoluta hacia un recurso dentro de una subcarpeta personalizada.
     *
     * <p>Permite acceder a carpetas distintas de {@code Images/}, como {@code Docs/},
     * {@code Reports/}, {@code Icons/}, entre otras. Si la carpeta no existe, la crea.</p>
     *
     * @param folder   Nombre de la subcarpeta relativa al directorio base (p. ej. {@code "Icons"}).
     *                 No debe ser {@code null} ni vacío.
     * @param fileName Nombre del archivo dentro de esa carpeta, incluyendo extensión.
     *                 No debe ser {@code null} ni vacío.
     * @return Ruta absoluta al archivo solicitado.
     * @throws IllegalArgumentException Si alguno de los parámetros es {@code null} o vacío.
     */
    public static String getResourcePath(String folder, String fileName) {
        if (folder == null || folder.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la carpeta no puede ser nulo o vacío.");
        }
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede ser nulo o vacío.");
        }

        Path resourceDir = Paths.get(getBasePath(), folder);

        if (!Files.exists(resourceDir)) {
            try {
                Files.createDirectories(resourceDir);
            } catch (IOException e) {
                System.err.println("[OSHelper] Advertencia: no se pudo crear el directorio '" + folder + "'. " + e.getMessage());
            }
        }

        return resourceDir.resolve(fileName).toString();
    }

    /**
     * Carga una imagen desde la carpeta {@code Images/} y la asigna directamente a un {@link javax.swing.JLabel}.
     *
     * <p>Permite integrar OSHelper en cualquier JFrame con <b>una sola línea por imagen</b>,
     * sin modificar los métodos existentes del formulario. Basta con llamar este método
     * al final del constructor, después de los métodos originales de carga, ya que
     * sobreescribe el ícono con la ruta resuelta correctamente para el SO actual.</p>
     *
     * <p>Si la imagen no existe o no puede leerse, imprime un mensaje en {@code System.err}
     * y deja el JLabel sin cambios, sin lanzar excepción.</p>
     *
     * @param label     El {@code JLabel} destino donde se mostrará la imagen.
     *                  Si es {@code null}, el método no hace nada.
     * @param imageName Nombre completo del archivo de imagen incluyendo extensión
     *                  (p. ej. {@code "Fondo.png"}, {@code "logo.jpg"}).
     *                  Si es {@code null} o vacío, el método no hace nada.
     */
    public static void setImage(javax.swing.JLabel label, String imageName) {
        if (label == null || imageName == null || imageName.trim().isEmpty()) return;
        try {
            File file = new File(getImagePath(imageName));
            if (file.exists()) {
                label.setIcon(new javax.swing.ImageIcon(javax.imageio.ImageIO.read(file)));
            } else {
                System.err.println("[OSHelper] Imagen no encontrada: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("[OSHelper] Error al cargar imagen '" + imageName + "': " + e.getMessage());
        }
    }

    /**
     * Verifica si un archivo de imagen existe en la carpeta {@code Images/}.
     *
     * <p>Método auxiliar para validar la existencia de un recurso antes de
     * intentar cargarlo en la interfaz, evitando excepciones en tiempo de ejecución.</p>
     *
     * @param imageName Nombre completo del archivo de imagen, incluyendo extensión.
     * @return {@code true} si el archivo existe y es un fichero regular; {@code false} en caso contrario.
     */
    public static boolean imageExists(String imageName) {
        if (imageName == null || imageName.trim().isEmpty()) return false;
        return Files.isRegularFile(Paths.get(getImagePath(imageName)));
    }
}