package CapaDatos.Logica_Conexion;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class BaseLocalEmbebidaTest {
    @TempDir Path temporal;

    @Test
    void conservaDatosAlReabrirYReinicializarElArchivo() throws Exception {
        String anterior = System.getProperty("db.embedded.path");
        System.setProperty("db.embedded.path", temporal.resolve("tienda").toString());
        try {
            try (Connection conexion = BaseLocalEmbebida.abrir(); Statement sql = conexion.createStatement()) {
                sql.executeUpdate("INSERT INTO Categoria(id, nombre) VALUES ('cat-portable', 'Portátiles')");
            }
            try (Connection conexion = BaseLocalEmbebida.abrir(); Statement sql = conexion.createStatement();
                    var datos = sql.executeQuery("SELECT nombre FROM Categoria WHERE id='cat-portable'")) {
                assertTrue(datos.next());
                assertEquals("Portátiles", datos.getString(1));
            }
        } finally {
            if (anterior == null) System.clearProperty("db.embedded.path");
            else System.setProperty("db.embedded.path", anterior);
        }
    }

    @Test
    void elDatoYLaColaSeReviertenEnLaMismaTransaccion() throws Exception {
        try (Connection conexion = memoria(); Statement sql = conexion.createStatement()) {
            conexion.setAutoCommit(false);
            sql.executeUpdate("INSERT INTO Categoria(id, nombre) VALUES ('cat-rollback', 'Prueba')");
            sql.executeUpdate("INSERT INTO ColaSincronizadora(id, accion, tablaAfectada, idRegistroAfectado, registroJson, estado) "
                    + "VALUES ('evento', 'INSERT', 'Categoria', 'cat-rollback', '{}', '0')");
            conexion.rollback();
            try (var filas = sql.executeQuery("SELECT (SELECT COUNT(*) FROM Categoria) + "
                    + "(SELECT COUNT(*) FROM ColaSincronizadora)")) {
                assertTrue(filas.next());
                assertEquals(0, filas.getInt(1));
            }
            assertThrows(SQLException.class, () -> sql.executeUpdate(
                    "INSERT INTO Persona(id, email, contrasenha, estado, clienteId) "
                    + "VALUES ('persona', 'demo@example.invalid', 'hash', '1', 'cliente-inexistente')"));
        }
    }

    @Test
    void lasConsultasDeFechasYLaSecuenciaSonCompatiblesConMysql() throws Exception {
        try (Connection conexion = memoria(); Statement sql = conexion.createStatement()) {
            try (var filas = sql.executeQuery("SELECT TIMESTAMPDIFF(DAY, CAST(TIMESTAMP '2026-11-10 23:59:59' AS DATE), DATE '2026-11-12')")) {
                assertTrue(filas.next());
                assertEquals(2, filas.getInt(1));
            }
            for (String evento : new String[]{"segundo-alfabetico", "primero-alfabetico"}) {
                sql.executeUpdate("INSERT INTO ColaSincronizadora(id, accion, tablaAfectada, idRegistroAfectado, registroJson, estado) "
                        + "VALUES ('" + evento + "', 'DELETE', 'Categoria', 'id-demo', '{}', '0')");
            }
            try (var filas = sql.executeQuery("SELECT id FROM ColaSincronizadora WHERE estado='0' ORDER BY secuencia ASC")) {
                assertTrue(filas.next());
                assertEquals("segundo-alfabetico", filas.getString(1));
                assertTrue(filas.next());
                assertEquals("primero-alfabetico", filas.getString(1));
            }
        }
    }

    private Connection memoria() throws SQLException {
        Connection conexion = DriverManager.getConnection("jdbc:h2:mem:" + UUID.randomUUID()
                + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE", "sa", "");
        BaseLocalEmbebida.inicializar(conexion);
        return conexion;
    }
}
