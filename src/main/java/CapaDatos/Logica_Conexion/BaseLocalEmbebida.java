package CapaDatos.Logica_Conexion;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Almacén local de escritorio cuando el equipo no dispone de MySQL. */
public final class BaseLocalEmbebida {
    private BaseLocalEmbebida() { }

    public static Connection abrir() throws SQLException {
        String defecto = Path.of(System.getProperty("user.home"), ".tienda-electronica", "data", "tienda").toString();
        Path archivo = ConfiguracionBD.ruta(ConfiguracionBD.valor("db.embedded.path", "DB_EMBEDDED_PATH", defecto));
        try {
            Files.createDirectories(archivo.getParent());
        } catch (IOException e) {
            throw new SQLException("No se puede crear la carpeta de datos locales", e);
        }
        String url = "jdbc:h2:file:" + archivo.toAbsolutePath().toString().replace('\\', '/')
                + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_ON_EXIT=FALSE";
        Connection conexion = DriverManager.getConnection(url, "sa", "");
        try {
            inicializar(conexion);
            return conexion;
        } catch (SQLException e) {
            conexion.close();
            throw e;
        }
    }

    /** Inicialización idempotente; se conserva la información existente. */
    public static void inicializar(Connection conexion) throws SQLException {
        try (InputStream recurso = BaseLocalEmbebida.class.getResourceAsStream("/db/schema-h2.sql")) {
            if (recurso == null) throw new SQLException("No se encontró el esquema de la base local embebida");
            String esquema = new String(recurso.readAllBytes(), StandardCharsets.UTF_8);
            try (Statement statement = conexion.createStatement()) {
                for (String consulta : esquema.split(";")) {
                    if (!consulta.isBlank()) statement.execute(consulta);
                }
            }
        } catch (IOException e) {
            throw new SQLException("No se pudo cargar el esquema de la base local embebida", e);
        }
    }
}
