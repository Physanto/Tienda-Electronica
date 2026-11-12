package CapaLogicaNegocio.Helpers;

import CapaDatos.Logica_Conexion.CategoriaDAO;
import CapaDatos.Logica_Conexion.Conexion;
import CapaDatos.Logica_Conexion.SincronizadoraDAO;
import CapaLogicaNegocio.Logica_Negocio.Categoria;
import CapaLogicaNegocio.Logica_Negocio.Sincronizadora;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class HelperGestorBDTest {
    @TempDir static Path temporal;
    private static String modoAnterior;
    private static String rutaAnterior;
    private static boolean nubeAnterior;

    @BeforeAll static void configurar() {
        modoAnterior = System.getProperty("db.mode");
        rutaAnterior = System.getProperty("db.embedded.path");
        nubeAnterior = HelperMonitorRed.usandoNube;
        System.setProperty("db.mode", "embedded");
        System.setProperty("db.embedded.path", temporal.resolve("test-tienda").toString());
        HelperMonitorRed.usandoNube = false;
    }

    @BeforeEach void limpiar() throws Exception {
        try (Statement sql = Conexion.getConexionLocal().createStatement()) {
            sql.executeUpdate("DELETE FROM ColaSincronizadora");
            sql.executeUpdate("DELETE FROM Categoria");
        }
    }

    @AfterAll static void cerrar() {
        Conexion.cerrarConexionLocal();
        restaurar("db.mode", modoAnterior);
        restaurar("db.embedded.path", rutaAnterior);
        HelperMonitorRed.usandoNube = nubeAnterior;
    }

    private static void restaurar(String clave, String anterior) {
        if (anterior == null) System.clearProperty(clave);
        else System.setProperty(clave, anterior);
    }

    @Test void conservaInsertUpdateDeleteEnOrdenSinInternet() {
        CategoriaDAO dao = new CategoriaDAO();
        Categoria categoria = new Categoria("categoria-test", "Inicial");
        assertTrue(HelperGestorBD.guardarRegistro(categoria, "Categoria", categoria.getId(),
                () -> dao.agregar(categoria), () -> fail("No debe escribir en nube estando offline")));
        categoria.setNombre("Actualizada");
        assertTrue(HelperGestorBD.actualizarRegistro(categoria, "Categoria", categoria.getId(),
                () -> dao.actualizar(categoria), () -> fail("No debe escribir en nube estando offline")));
        assertTrue(HelperGestorBD.eliminarRegistro(categoria.getId(), "Categoria",
                () -> dao.eliminar(categoria.getId()), () -> fail("No debe escribir en nube estando offline")));
        assertNull(dao.obtener(categoria.getId()));
        var cola = new SincronizadoraDAO().obtenerNoSincronizados("0");
        assertEquals(3, cola.size());
        assertEquals(Sincronizadora.Accion.INSERT, cola.get(0).getAccion());
        assertTrue(cola.get(0).getRegistroJson().contains("Inicial"));
        assertEquals(Sincronizadora.Accion.UPDATE, cola.get(1).getAccion());
        assertTrue(cola.get(1).getRegistroJson().contains("Actualizada"));
        assertEquals(Sincronizadora.Accion.DELETE, cola.get(2).getAccion());
    }

    @Test void revierteElDatoCuandoNoPuedeEncolar() throws Exception {
        Connection conexion = Conexion.getConexionLocal();
        try (Statement sql = conexion.createStatement()) {
            sql.execute("ALTER TABLE ColaSincronizadora ADD CONSTRAINT rechazar_cola CHECK (estado <> '0')");
            try {
                Categoria categoria = new Categoria("sin-respaldo", "No debe persistir");
                assertFalse(HelperGestorBD.guardarRegistro(categoria, "Categoria", categoria.getId(),
                        () -> new CategoriaDAO().agregar(categoria), () -> true));
                assertNull(new CategoriaDAO().obtener(categoria.getId()));
                assertTrue(new SincronizadoraDAO().obtenerNoSincronizados("0").isEmpty());
                assertTrue(conexion.getAutoCommit());
            } finally {
                sql.execute("ALTER TABLE ColaSincronizadora DROP CONSTRAINT rechazar_cola");
            }
        }
    }

    @Test void unaLecturaLocalConExcepcionPermiteConsultarLaNube() {
        HelperMonitorRed.usandoNube = true;
        try {
            Categoria categoria = new Categoria("remota", "Remota");
            assertSame(categoria, HelperGestorBD.cargarRegistro(
                    () -> { throw new IllegalStateException("MySQL interrumpido"); }, () -> categoria));
            ArrayList<Categoria> remotas = new ArrayList<>();
            remotas.add(categoria);
            assertEquals(remotas, HelperGestorBD.cargarRegistros(
                    () -> { throw new IllegalStateException("MySQL interrumpido"); }, () -> remotas));
        } finally {
            HelperMonitorRed.usandoNube = false;
        }
    }

    @Test void unaTablaLocalVaciaNoResucitaBorradosDesdeLaNube() {
        HelperMonitorRed.usandoNube = true;
        try {
            assertTrue(HelperGestorBD.cargarRegistros(ArrayList::new,
                    () -> fail("La tabla local es autoritativa")).isEmpty());
        } finally {
            HelperMonitorRed.usandoNube = false;
        }
    }

    @Test void elModuloVentasRecibeUnaListaVaciaCuandoNoHayMovimientos() {
        var respuesta = new CapaLogicaNegocio.Controlador.VentaControlador().buscarTodos();
        assertNotNull(respuesta.dato());
        assertTrue(respuesta.dato().isEmpty());
    }

    @Test void consultarUnaVentaConservaLaHora() throws Exception {
        Connection conexion = Conexion.getConexionLocal();
        conexion.setAutoCommit(false);
        try (Statement sql = conexion.createStatement()) {
            sql.executeUpdate("INSERT INTO Cliente(id,nombre,apellido,direccion,cedula) "
                    + "VALUES ('cliente-hora','Demo','Prueba','Dirección ficticia','9900099999')");
            var fecha = java.sql.Timestamp.valueOf("2026-11-12 14:37:23");
            var venta = new CapaLogicaNegocio.Logica_Negocio.Venta("venta-hora", fecha, 1000.0,
                    CapaLogicaNegocio.Logica_Negocio.Venta.MetodoPago.PSE, "cliente-hora");
            var dao = new CapaDatos.Logica_Conexion.VentaDAO();
            assertTrue(dao.agregar(venta));
            assertEquals(fecha.getTime(), dao.obtener("venta-hora").getFechaVenta().getTime());
        } finally {
            conexion.rollback();
            conexion.setAutoCommit(true);
        }
    }
}
