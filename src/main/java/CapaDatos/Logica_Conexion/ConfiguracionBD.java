package CapaDatos.Logica_Conexion;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Configuración externa: propiedades JVM, entorno y archivo local, en ese orden. */
public final class ConfiguracionBD {
    private static final Path BASE = localizarBase();
    private static final Properties CONFIG = cargar();

    private ConfiguracionBD() { }

    private static Path localizarBase() {
        Path actual = Path.of("").toAbsolutePath();
        try {
            Path ubicacion = Path.of(ConfiguracionBD.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            for (Path ruta = ubicacion; ruta != null; ruta = ruta.getParent()) {
                if (Files.exists(ruta.resolve("pom.xml")) || Files.exists(ruta.resolve("config.local.properties"))) {
                    return ruta;
                }
            }
            if (Files.isRegularFile(ubicacion)) return ubicacion.getParent();
        } catch (Exception ignored) { }
        return actual;
    }

    private static Properties cargar() {
        Properties propiedades = new Properties();
        Path archivo = BASE.resolve("config.local.properties");
        if (Files.isRegularFile(archivo)) {
            try (Reader reader = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
                propiedades.load(reader);
            } catch (IOException e) {
                throw new IllegalStateException("No se pudo leer config.local.properties", e);
            }
        }
        return propiedades;
    }

    public static String valor(String propiedad, String entorno, String defecto) {
        String valor = System.getProperty(propiedad);
        if (valor == null) valor = System.getenv(entorno);
        if (valor == null) valor = CONFIG.getProperty(propiedad);
        return valor == null ? defecto : valor;
    }

    public static Path ruta(String ruta) {
        Path archivo = Path.of(ruta);
        return archivo.isAbsolute() ? archivo : BASE.resolve(archivo).normalize();
    }

    static Path credencialesFirebase() throws IOException {
        String explicita = valor("firebase.credentials", "GOOGLE_APPLICATION_CREDENTIALS", "");
        if (!explicita.isBlank()) return ruta(explicita);
        for (String nombre : new String[]{"tienda-electronica-v2.json", "tienda-electronica.json"}) {
            if (Files.isRegularFile(BASE.resolve(nombre))) return BASE.resolve(nombre);
        }
        try (var archivos = Files.newDirectoryStream(BASE, "*-firebase-adminsdk-*.json")) {
            Path encontrada = null;
            for (Path archivo : archivos) {
                if (encontrada != null) throw new IOException(
                        "Hay varias claves Firebase; indique firebase.credentials o GOOGLE_APPLICATION_CREDENTIALS");
                encontrada = archivo;
            }
            return encontrada;
        }
    }
}
