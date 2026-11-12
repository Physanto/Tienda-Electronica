package CapaPresentacion.GUI_Admin;

import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InicioTest {
    @Test void elLogoSeCentraSinRecortarseAlCambiarElTamano() throws Exception {
        Inicio[] panel = new Inicio[1];
        SwingUtilities.invokeAndWait(() -> panel[0] = new Inicio());
        for (Dimension tamano : new Dimension[]{new Dimension(650, 500), new Dimension(1035, 680),
                new Dimension(1675, 1000)}) {
            SwingUtilities.invokeAndWait(() -> {
                panel[0].setSize(tamano);
                panel[0].doLayout();
            });
            SwingUtilities.invokeAndWait(() -> {
                JLabel logo = (JLabel) panel[0].getComponent(0);
                ImageIcon icono = (ImageIcon) logo.getIcon();
                assertNotNull(icono);
                assertEquals(JLabel.CENTER, logo.getHorizontalAlignment());
                assertEquals(JLabel.CENTER, logo.getVerticalAlignment());
                assertEquals(tamano.width, logo.getWidth());
                assertEquals(tamano.height, logo.getHeight());
                assertTrue(icono.getIconWidth() <= logo.getWidth() - 63);
                assertTrue(icono.getIconHeight() <= logo.getHeight() - 63);
                assertTrue(icono.getIconWidth() > 100);
            });
        }
    }
}
