package CapaPresentacion.GUI_Admin;

import CapaLogicaNegocio.Controlador.PromocionControlador;
import CapaLogicaNegocio.Controlador.RespuestaControlador;
import CapaLogicaNegocio.DTOS.PromocionesDTO;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PromocionesTest {
    @Test void losBotonesPermanecenDentroDelPanelEnTodasLasPestanas() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JPanel panel = new Promociones(new PromocionControlador(), false);
            JTabbedPane pestanas = (JTabbedPane) buscar(panel, JTabbedPane.class).get(0);
            for (int ancho : new int[]{650, 1000, 1550}) {
                panel.setSize(ancho, 650);
                for (int tab = 0; tab < pestanas.getTabCount(); tab++) {
                    pestanas.setSelectedIndex(tab);
                    distribuir(panel);
                    verificarBotones(panel);
                }
            }
        });
    }

    @Test void cargaLasSugerenciasAlAbrirSinConsultarDatosEnElHiloDeSwing() throws Exception {
        AtomicBoolean fueraDelHilo = new AtomicBoolean(true);
        PromocionControlador falso = new PromocionControlador() {
            @Override public RespuestaControlador<ArrayList<PromocionesDTO.PromocionAplicadaDTO>> obtenerProductosPromocion() {
                fueraDelHilo.compareAndSet(true, !SwingUtilities.isEventDispatchThread());
                return new RespuestaControlador<>(true, "Prueba", new ArrayList<>(List.of(
                        new PromocionesDTO.PromocionAplicadaDTO("producto", "Producto de prueba", "Marca", "20", "100000", "30", "4"))));
            }
            @Override public RespuestaControlador<ArrayList<PromocionesDTO.PromocioPersonalizadaCliente>> obtenerResumeComprasClientes() {
                fueraDelHilo.compareAndSet(true, !SwingUtilities.isEventDispatchThread());
                return new RespuestaControlador<>(true, "Prueba", new ArrayList<>(List.of(
                        new PromocionesDTO.PromocioPersonalizadaCliente("cliente", "Ana", "Prueba", "9900", "0", null))));
            }
            @Override public ArrayList<PromocionVista> listarParaAdministrar() { return new ArrayList<>(); }
        };
        JPanel[] panel = new JPanel[1];
        SwingUtilities.invokeAndWait(() -> panel[0] = new Promociones(falso, true));
        long limite = System.nanoTime() + 5_000_000_000L;
        AtomicBoolean cargado = new AtomicBoolean();
        while (!cargado.get() && System.nanoTime() < limite) {
            SwingUtilities.invokeAndWait(() -> {
                var tablas = buscar(panel[0], JTable.class);
                cargado.set(!tablas.isEmpty() && ((JTable) tablas.get(0)).getRowCount() == 1);
            });
            if (!cargado.get()) Thread.sleep(25);
        }
        assertTrue(cargado.get(), "Las sugerencias deben cargarse automáticamente");
        assertTrue(fueraDelHilo.get(), "Las consultas deben ejecutarse en segundo plano");
    }

    private static List<Component> buscar(Container padre, Class<?> tipo) {
        List<Component> resultado = new ArrayList<>();
        for (Component hijo : padre.getComponents()) {
            if (tipo.isInstance(hijo)) resultado.add(hijo);
            if (hijo instanceof Container contenedor) resultado.addAll(buscar(contenedor, tipo));
        }
        return resultado;
    }

    private static void distribuir(Container padre) {
        padre.doLayout();
        for (Component hijo : padre.getComponents()) if (hijo instanceof Container c) distribuir(c);
    }

    private static void verificarBotones(Container padre) {
        for (Component hijo : padre.getComponents()) {
            if (!hijo.isVisible()) continue;
            if (hijo instanceof JButton b && !b.getText().isBlank()) {
                assertTrue(b.getX() >= 0 && b.getX() + b.getWidth() <= padre.getWidth(), b.getText());
                assertTrue(b.getWidth() >= b.getPreferredSize().width, "Botón recortado: " + b.getText());
            }
            if (hijo instanceof Container c) verificarBotones(c);
        }
    }
}
