package utilidades;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CargarProductosVentasDemoTest {
    @Test void elConjuntoEsRepetibleYLasVentasCuadranConSusDetallesYExistencias() {
        var datos = CargarProductosVentasDemo.registros();
        assertEquals(datos, CargarProductosVentasDemo.registros());
        Map<String, CargarProductosVentasDemo.Registro> productos = new HashMap<>();
        Map<String, CargarProductosVentasDemo.Registro> ventas = new HashMap<>();
        Set<String> categorias = new HashSet<>();
        Set<String> rutas = new HashSet<>();
        Set<Object> clientes = new HashSet<>();
        CargarDatosDemo.clientes().forEach(c -> clientes.add(c.get("id")));
        for (var registro : datos) {
            assertTrue(rutas.add(registro.ruta()));
            switch (registro.tabla()) {
                case "Categoria" -> categorias.add(registro.id());
                case "Producto" -> productos.put(registro.id(), registro);
                case "Venta" -> ventas.put(registro.id(), registro);
            }
        }
        assertEquals(5, categorias.size());
        assertEquals(50, productos.size());
        assertEquals(50, ventas.size());
        Map<String, Double> totales = new HashMap<>();
        Map<String, Long> unidadesVendidas = new HashMap<>();
        int detalles = 0;
        for (var registro : datos) if (registro.tabla().equals("DetalleVenta")) {
            detalles++;
            var detalle = registro.datos();
            String producto = (String) detalle.get("idProducto");
            String venta = (String) detalle.get("idVenta");
            assertTrue(productos.containsKey(producto));
            assertTrue(ventas.containsKey(venta));
            long cantidad = (Long) detalle.get("cantidad");
            double precio = (Double) detalle.get("precioVenta");
            double subtotal = (Double) detalle.get("subtotal");
            assertEquals(cantidad * precio, subtotal);
            assertEquals(productos.get(producto).datos().get("precioActual"), precio);
            totales.merge(venta, subtotal, Double::sum);
            unidadesVendidas.merge(producto, cantidad, Long::sum);
        }
        assertEquals(100, detalles);
        ventas.forEach((id, venta) -> {
            assertTrue(clientes.contains(venta.datos().get("idCliente")));
            assertEquals(totales.get(id), venta.datos().get("totalVenta"));
        });
        productos.forEach((id, producto) -> {
            assertTrue(categorias.contains(producto.datos().get("idCategoria")));
            long disponible = (Long) producto.datos().get("stock");
            assertTrue(disponible > 0);
            assertTrue(disponible + unidadesVendidas.get(id) >= 40);
        });
    }
}
