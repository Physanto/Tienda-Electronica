package utilidades;

import CapaDatos.Logica_Conexion.Conexion;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/** Carga repetible de productos y ventas ficticias con sus relaciones completas. */
public final class CargarProductosVentasDemo {
    public record Registro(String tabla, Map<String, Object> datos) {
        public String id() { return (String) datos.get("id"); }
        public String ruta() { return tabla + "/" + id(); }
    }

    private CargarProductosVentasDemo() { }

    private static String id(String tipo, int indice) {
        return UUID.nameUUIDFromBytes(("tienda-electronica/demo/" + tipo + "/" + indice)
                .getBytes(StandardCharsets.UTF_8)).toString();
    }

    private static Map<String, Object> mapa(Object... pares) {
        Map<String, Object> resultado = new LinkedHashMap<>();
        for (int i = 0; i < pares.length; i += 2) resultado.put((String) pares[i], pares[i + 1]);
        return resultado;
    }

    public static List<Registro> registros() {
        List<Registro> categorias = new ArrayList<>();
        List<Registro> productos = new ArrayList<>();
        List<Registro> ventas = new ArrayList<>();
        List<Registro> detalles = new ArrayList<>();
        String[] nombresCategorias = {"Teléfonos", "Tablets", "Computadores", "Consolas", "Accesorios"};
        String[] tipos = {"Smartphone", "Tablet", "Portátil", "Consola", "Accesorio"};
        String[] marcas = {"NexByte", "NovaTech", "Circuito", "Pixelia", "TecnoSur"};
        double[] bases = {500000, 900000, 2000000, 1500000, 60000};
        for (int i = 0; i < 5; i++) categorias.add(new Registro("Categoria",
                mapa("id", id("categoria", i + 1), "nombre", nombresCategorias[i])));
        for (int i = 0; i < 50; i++) {
            int categoria = i / 10;
            productos.add(new Registro("Producto", mapa("id", id("producto", i + 1),
                    "codigo", String.format("DEMO-P%03d", i + 1),
                    "nombre", tipos[categoria] + " Demo " + (i % 10 + 1),
                    "marca", marcas[i % marcas.length], "serie", String.format("DEMO-S%03d", i + 1),
                    "stock", 40L + i % 10, "precioActual", bases[categoria] + (i % 10) * 25000,
                    "fechaVencimiento", Date.from(Instant.parse("2028-12-31T17:00:00Z")),
                    "urlImg", null, "idCategoria", categorias.get(categoria).id())));
        }
        var clientes = CargarDatosDemo.clientes();
        String[] pagos = {"TARJETA", "PSE", "EFECTY"};
        for (int i = 0; i < 50; i++) {
            String venta = id("venta", i + 1);
            double total = 0;
            for (int linea = 0; linea < 2; linea++) {
                Registro producto = productos.get((i + linea * 7) % 50);
                long cantidad = linea == 0 ? 1 + i % 3 : 1 + i % 2;
                double precio = (Double) producto.datos().get("precioActual");
                double subtotal = cantidad * precio;
                total += subtotal;
                producto.datos().compute("stock", (k, stock) -> (Long) stock - cantidad);
                detalles.add(new Registro("DetalleVenta", mapa("id", id("detalle-venta", i * 2 + linea + 1),
                        "cantidad", cantidad, "subtotal", subtotal, "precioVenta", precio,
                        "idProducto", producto.id(), "idVenta", venta)));
            }
            ventas.add(new Registro("Venta", mapa("id", venta, "fechaVenta",
                    Date.from(Instant.parse("2026-09-23T14:00:00Z").plus(i, ChronoUnit.DAYS)
                            .plus(i % 6, ChronoUnit.HOURS)),
                    "totalVenta", total, "metodoPago", pagos[i % pagos.length],
                    "idCliente", clientes.get(i).get("id"))));
        }
        List<Registro> resultado = new ArrayList<>();
        resultado.addAll(categorias);
        resultado.addAll(productos);
        resultado.addAll(ventas);
        resultado.addAll(detalles);
        return resultado;
    }

    private static Map<String, Object> leerLocal(Connection conexion, Registro registro) throws Exception {
        String columnas = String.join(",", registro.datos().keySet());
        try (var consulta = conexion.prepareStatement("SELECT " + columnas + " FROM " + registro.tabla() + " WHERE id=?")) {
            consulta.setString(1, registro.id());
            try (var fila = consulta.executeQuery()) {
                if (!fila.next()) return null;
                Map<String, Object> datos = new LinkedHashMap<>();
                for (var campo : registro.datos().entrySet()) {
                    datos.put(campo.getKey(), campo.getValue() instanceof Date
                            ? fila.getTimestamp(campo.getKey()) : fila.getObject(campo.getKey()));
                }
                return datos;
            }
        }
    }

    private static Map<String, Object> normalizar(Map<String, Object> datos) {
        if (datos == null) return null;
        Map<String, Object> resultado = new LinkedHashMap<>();
        datos.forEach((clave, valor) -> {
            if (valor instanceof com.google.cloud.Timestamp fecha) valor = fecha.toDate().getTime();
            else if (valor instanceof Date fecha) valor = fecha.getTime();
            if (valor instanceof Number numero) valor = new BigDecimal(numero.toString()).stripTrailingZeros();
            resultado.put(clave, valor);
        });
        return resultado;
    }

    private static void comprobar(Registro esperado, Map<String, Object> actual, String base) {
        if (actual != null && !normalizar(esperado.datos()).equals(normalizar(actual))) {
            throw new IllegalStateException("Conflicto en " + base + ": " + esperado.ruta()
                    + ". Se conserva el registro existente y se cancela la carga.");
        }
    }

    private static Map<String, Map<String, Object>> leerNube(Firestore nube, List<Registro> datos) throws Exception {
        Map<String, Map<String, Object>> encontrados = new LinkedHashMap<>();
        if (nube != null) {
            DocumentReference[] refs = datos.stream().map(r -> nube.document(r.ruta())).toArray(DocumentReference[]::new);
            for (var documento : nube.getAll(refs).get(30, TimeUnit.SECONDS)) {
                if (documento.exists()) encontrados.put(documento.getReference().getPath(), documento.getData());
            }
        }
        return encontrados;
    }

    private static void comprobarClientes(Connection local, Firestore nube, List<Registro> datos) throws Exception {
        Set<String> clientes = datos.stream().filter(r -> r.tabla().equals("Venta"))
                .map(r -> (String) r.datos().get("idCliente")).collect(Collectors.toSet());
        List<Registro> referencias = CargarDatosDemo.clientes().stream()
                .filter(c -> clientes.contains(c.get("id"))).map(c -> new Registro("Cliente", c)).toList();
        var remotos = leerNube(nube, referencias);
        for (Registro cliente : referencias) {
            var actual = leerLocal(local, cliente);
            if (actual == null || (nube != null && !remotos.containsKey(cliente.ruta()))) {
                throw new IllegalStateException("Falta un cliente de referencia: " + cliente.id()
                        + ". Ejecute primero CargarDatosDemo.");
            }
            comprobar(cliente, actual, "base local");
            if (nube != null) comprobar(cliente, remotos.get(cliente.ruta()), "Firestore");
        }
    }

    public static void ejecutar(boolean soloLocal, boolean soloVerificar) throws Exception {
        Connection local = Conexion.getConexionLocal();
        if (local == null) throw new IllegalStateException("La base local no está disponible");
        if (!local.getAutoCommit()) throw new IllegalStateException("Hay una transacción activa");
        Firestore nube = soloLocal ? null : Conexion.getConexionNube();
        if (!soloLocal && nube == null) throw new IllegalStateException("Firestore no está configurado");
        List<Registro> datos = registros();
        comprobarClientes(local, nube, datos);
        var remotos = leerNube(nube, datos);
        List<Registro> faltantes = new ArrayList<>();
        for (Registro registro : datos) {
            var actual = leerLocal(local, registro);
            comprobar(registro, actual, "base local");
            if (nube != null) comprobar(registro, remotos.get(registro.ruta()), "Firestore");
            if (actual == null) faltantes.add(registro);
            if (soloVerificar && (actual == null || (nube != null && !remotos.containsKey(registro.ruta())))) {
                throw new IllegalStateException("Registro no encontrado: " + registro.ruta());
            }
        }
        if (!soloVerificar) {
            System.out.println("Registros nuevos: local=" + faltantes.size()
                    + (nube == null ? "" : ", Firestore=" + (datos.size() - remotos.size())));
            local.setAutoCommit(false);
            try {
                for (Registro registro : faltantes) {
                    String marcadores = registro.datos().keySet().stream().map(k -> "?").collect(Collectors.joining(","));
                    try (PreparedStatement insertar = local.prepareStatement("INSERT INTO " + registro.tabla()
                            + " (" + String.join(",", registro.datos().keySet()) + ") VALUES (" + marcadores + ")")) {
                        int posicion = 1;
                        for (Object valor : registro.datos().values()) {
                            if (valor instanceof Date fecha) insertar.setTimestamp(posicion++, new Timestamp(fecha.getTime()));
                            else insertar.setObject(posicion++, valor);
                        }
                        insertar.executeUpdate();
                    }
                }
                if (nube != null && remotos.size() < datos.size()) {
                    var lote = nube.batch();
                    for (Registro registro : datos) {
                        if (!remotos.containsKey(registro.ruta())) lote.create(nube.document(registro.ruta()), registro.datos());
                    }
                    lote.commit().get(30, TimeUnit.SECONDS);
                }
                local.commit();
            } catch (Exception error) {
                local.rollback();
                throw error;
            } finally {
                local.setAutoCommit(true);
            }
            remotos = leerNube(nube, datos);
        }
        for (Registro registro : datos) {
            var actual = leerLocal(local, registro);
            if (actual == null || (nube != null && !remotos.containsKey(registro.ruta()))) {
                throw new IllegalStateException("Carga incompleta: " + registro.ruta());
            }
            comprobar(registro, actual, "base local");
            if (nube != null) comprobar(registro, remotos.get(registro.ruta()), "Firestore");
        }
        for (String tabla : List.of("Categoria", "Producto", "Venta", "DetalleVenta")) {
            try (var sql = local.createStatement(); var filas = sql.executeQuery("SELECT COUNT(*) FROM " + tabla)) {
                filas.next();
                String conteoNube = nube == null ? "no consultada"
                        : String.valueOf(nube.collection(tabla).get().get(30, TimeUnit.SECONDS).size());
                System.out.println(tabla + ": total local=" + filas.getInt(1) + ", total Firestore=" + conteoNube);
            }
        }
        System.out.println("Verificados: 50 productos, 50 ventas, 100 detalles y 5 categorías"
                + (nube == null ? " en local" : "; todos los campos e IDs idénticos en ambas bases"));
    }

    public static void main(String[] args) {
        int resultado = 0;
        try {
            for (String arg : args) if (!Set.of("--solo-local", "--verificar").contains(arg)) {
                throw new IllegalArgumentException("Uso: CargarProductosVentasDemo [--solo-local] [--verificar]");
            }
            ejecutar(Arrays.asList(args).contains("--solo-local"), Arrays.asList(args).contains("--verificar"));
        } catch (Exception error) {
            resultado = 1;
            System.err.println("Carga incompleta: " + error.getMessage());
        } finally {
            Conexion.cerrarConexionLocal();
            for (FirebaseApp app : FirebaseApp.getApps()) app.delete();
        }
        if (resultado != 0) System.exit(resultado);
    }
}
