package CapaLogicaNegocio.Controlador;

import CapaDatos.Logica_Conexion.Conexion;
import CapaDatos.Logica_Conexion.PromocionDAO;
import CapaDatos.Logica_Conexion.SincronizadoraDAO;
import CapaLogicaNegocio.DTOS.PromocionesDTO;
import CapaLogicaNegocio.Helpers.HelperMonitorRed;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class PromocionControladorTest {
    @TempDir static Path temporal;
    static String modo, ruta;
    static boolean nube;
    String producto;
    String cliente;
    final PromocionControlador controlador = new PromocionControlador();

    @BeforeAll static void configurar() {
        modo = System.getProperty("db.mode"); ruta = System.getProperty("db.embedded.path");
        nube = HelperMonitorRed.usandoNube;
        System.setProperty("db.mode", "embedded");
        System.setProperty("db.embedded.path", temporal.resolve("promociones").toString());
        HelperMonitorRed.usandoNube = false;
    }

    @AfterAll static void cerrar() {
        Conexion.cerrarConexionLocal();
        if (modo == null) System.clearProperty("db.mode"); else System.setProperty("db.mode", modo);
        if (ruta == null) System.clearProperty("db.embedded.path"); else System.setProperty("db.embedded.path", ruta);
        HelperMonitorRed.usandoNube = nube;
    }

    @BeforeEach void preparar() throws Exception {
        try (var sql = Conexion.getConexionLocal().createStatement()) {
            for (String tabla : new String[]{"ColaSincronizadora", "PromocionProducto", "PromocionCliente", "Promocion", "Producto", "Categoria", "Cliente"})
                sql.executeUpdate("DELETE FROM " + tabla);
            producto = UUID.randomUUID().toString(); cliente = UUID.randomUUID().toString();
            sql.executeUpdate("INSERT INTO Categoria(id,nombre) VALUES ('categoria','Pruebas')");
            sql.executeUpdate("INSERT INTO Producto(id,codigo,nombre,marca,serie,stock,precioActual,fechaVencimiento,idCategoria) "
                    + "VALUES ('" + producto + "','PRUEBA','Producto','Marca','Serie',20,100000,CURRENT_TIMESTAMP,'categoria')");
            sql.executeUpdate("INSERT INTO Cliente(id,nombre,apellido,direccion,cedula) "
                    + "VALUES ('" + cliente + "','Ana','Prueba','Ficticia','9900000001')");
        }
    }

    private PromocionesDTO.PromocionessDTO dto(String tipo, int horas) {
        var formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return new PromocionesDTO.PromocionessDTO(UUID.randomUUID().toString(), "Descuento de prueba", "15",
                LocalDateTime.now().plusHours(horas).format(formato), LocalDateTime.now().plusHours(horas + 2).format(formato), tipo);
    }

    private int contar(String tabla) throws Exception {
        try (var sql = Conexion.getConexionLocal().createStatement(); var r = sql.executeQuery("SELECT COUNT(*) FROM " + tabla)) {
            r.next(); return r.getInt(1);
        }
    }

    @Test void guardaLaPromocionYElVinculoConDosEventosYSeVenSinInternet() throws Exception {
        assertTrue(controlador.aplicarPromocionProducto(producto, dto("ESPECIFICA", 1)).exito());
        assertEquals(1, contar("Promocion")); assertEquals(1, contar("PromocionProducto"));
        var cola = new SincronizadoraDAO().obtenerNoSincronizados("0");
        assertEquals(2, cola.size());
        assertTrue(cola.get(0).getRegistroJson().contains(producto));
        var lista = controlador.obtenerPromocionesActivas().dato();
        assertEquals(1, lista.size()); assertEquals(producto, lista.get(0).getIdProducto());
        assertEquals("Producto", controlador.listarParaAdministrar().get(0).destino());
    }

    @Test void revierteTodoSiFallaElSegundoEvento() throws Exception {
        try (var sql = Conexion.getConexionLocal().createStatement()) {
            sql.execute("ALTER TABLE ColaSincronizadora ADD CONSTRAINT rechazar_vinculo CHECK (tablaAfectada <> 'PromocionProducto')");
            try {
                assertFalse(controlador.aplicarPromocionProducto(producto, dto("ESPECIFICA", 1)).exito());
                assertEquals(0, contar("Promocion")); assertEquals(0, contar("PromocionProducto"));
                assertEquals(0, contar("ColaSincronizadora"));
            } finally { sql.execute("ALTER TABLE ColaSincronizadora DROP CONSTRAINT rechazar_vinculo"); }
        }
    }

    @Test void noDejaUnaPromocionHuerfanaSiElVinculoNoPuedeGuardarse() throws Exception {
        try (var sql = Conexion.getConexionLocal().createStatement()) {
            sql.execute("ALTER TABLE PromocionProducto ADD CONSTRAINT rechazar_relacion CHECK (idProducto <> '" + producto + "')");
            try {
                assertFalse(controlador.aplicarPromocionProducto(producto, dto("ESPECIFICA", 1)).exito());
                assertEquals(0, contar("Promocion")); assertEquals(0, contar("PromocionProducto"));
                assertEquals(0, contar("ColaSincronizadora"));
            } finally { sql.execute("ALTER TABLE PromocionProducto DROP CONSTRAINT rechazar_relacion"); }
        }
    }

    @Test void rechazaDestinoInexistenteYDescuentosSuperpuestosAunqueSeRecreeElControlador() throws Exception {
        assertFalse(controlador.aplicarPromocionProducto(UUID.randomUUID().toString(), dto("ESPECIFICA", 1)).exito());
        assertEquals(0, contar("Promocion"));
        assertTrue(controlador.aplicarPromocionProducto(producto, dto("ESPECIFICA", 1)).exito());
        assertFalse(new PromocionControlador().aplicarPromocionProducto(producto, dto("ESPECIFICA", 1)).exito());
        assertTrue(controlador.aplicarPromocionProducto(producto, dto("ESPECIFICA", 4)).exito());
        assertEquals(2, contar("Promocion"));
    }

    @Test void desactivarSinConexionConservaElDestinoYEncolaLaActualizacion() {
        var dto = dto("GENERAL", 1);
        assertTrue(controlador.aplicarPromocionCliente(cliente, dto).exito());
        assertTrue(controlador.desactivarPromocion(new Date(), dto.id()).exito());
        assertTrue(controlador.obtenerPromocionesActivas().dato().isEmpty());
        var cola = new SincronizadoraDAO().obtenerNoSincronizados("0");
        assertEquals(3, cola.size());
        assertTrue(cola.get(2).getRegistroJson().contains(cliente));
        assertEquals("UPDATE", cola.get(2).getAccion().name());
        Date original = new PromocionDAO().obtenerPromocionesPersonalizadas().get(0).getFechaFin();
        controlador.obtenerPromocionesActivas();
        assertEquals(original, new PromocionDAO().obtenerPromocionesPersonalizadas().get(0).getFechaFin());
    }

    @Test void validaCamposNulosPorcentajesYFechasRealesAntesDeEscribir() throws Exception {
        assertFalse(controlador.aplicarPromocionProducto(producto, null).exito());
        var valido = dto("ESPECIFICA", 1);
        for (String porcentaje : new String[]{"0", "91", "NaN", "Infinity", "-5"}) {
            var invalido = new PromocionesDTO.PromocionessDTO(valido.id(), valido.nombre(), porcentaje, valido.fechaInicio(), valido.fechaFin(), valido.tipo());
            assertFalse(controlador.aplicarPromocionProducto(producto, invalido).exito());
        }
        var fechaInvalida = new PromocionesDTO.PromocionessDTO(valido.id(), valido.nombre(), "10", "31/02/2027 10:00", "01/03/2027 10:00", valido.tipo());
        assertFalse(controlador.aplicarPromocionProducto(producto, fechaInvalida).exito());
        assertEquals(0, contar("Promocion")); assertEquals(0, contar("ColaSincronizadora"));
    }

    @Test void analizaUnClienteSinComprasYUnInventarioDeUnSoloProducto() {
        var analisis = controlador.analizarCliente(cliente);
        assertTrue(analisis.exito()); assertEquals("NUEVO", analisis.dato().getEtiquetaNegocio());
        assertEquals("5", analisis.dato().getDescuentoRecomendado());
        var sugerencias = controlador.obtenerProductosPromocion();
        assertTrue(sugerencias.exito()); assertEquals(1, sugerencias.dato().size());
        assertEquals("999", sugerencias.dato().get(0).diasSinVenta());
        assertNull(controlador.obtenerResumeComprasClientes().dato().get(0).ultCompra());
    }
}
