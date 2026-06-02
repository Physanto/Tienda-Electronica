package CapaPresentacion.GUI_Cliente;

import CapaPresentacion.GUI_Admin.InicioSesion;
import CapaLogicaNegocio.Logica_Negocio.Carrito;
import CapaLogicaNegocio.Logica_Negocio.SesionCliente;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuCliente extends JFrame {

    private JPanel panelPrincipal;
    private JPanel navBar;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Carrito compartido entre el catalogo y el panel del carrito (vive en memoria
    // durante la sesion)
    private final transient Carrito carrito = new Carrito();

    // Panels
    private Productos panelProductos;
    private CarritoCompras panelCarrito;
    private Promociones panelPromociones;

    public MenuCliente() {
        initComponents();
        configurarVentana();
        inicializarPanels();
        configurarLayoutPrincipal();
        configurarNavegacion();
    }

    private void configurarVentana() {
        setTitle("Menú Cliente");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1920, 1080));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    private void inicializarPanels() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(0x1A, 0x1E, 0x29));

        panelProductos = new Productos(carrito);
        panelCarrito = new CarritoCompras(carrito);
        panelPromociones = new Promociones();

        // El banner de promociones del catalogo navega a la pestania de Promociones.
        panelProductos.setOnVerPromociones(() -> {
            cardLayout.show(contentPanel, "Promociones");
            panelPromociones.cargarPromociones();
        });

        // Tras una compra exitosa, el stock cambio en la nube: recargar el catalogo.
        panelCarrito.setOnCompraExitosa(() -> panelProductos.cargarCatalogo());

        contentPanel.add(panelProductos, "Productos");
        contentPanel.add(panelCarrito, "Carrito");
        contentPanel.add(panelPromociones, "Promociones");

        // Apenas ingresa el cliente se muestra el panel de Promociones (con las promos vigentes
        // recien leidas de la nube). Al pasar luego a Productos, el catalogo se recarga y refleja la
        // promocion y su precio con descuento sin necesidad de pulsar "Actualizar".
        cardLayout.show(contentPanel, "Promociones");
        panelPromociones.cargarPromociones();
    }

    private void configurarLayoutPrincipal() {
        panelPrincipal = new JPanel(new BorderLayout());
        navBar = crearNavBar();

        panelPrincipal.add(navBar, BorderLayout.NORTH);
        panelPrincipal.add(contentPanel, BorderLayout.CENTER);

        setContentPane(panelPrincipal);
    }

    private void configurarNavegacion() {
    }

    private JPanel crearNavBar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(new Color(0x13, 0x2D, 0x46));
        nav.setPreferredSize(new Dimension(1920, 80));

        JLabel logo = new JLabel("Tienda Online");
        logo.setFont(new Font("Segoe UI Light", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);
        logo.setBorder(new javax.swing.border.EmptyBorder(0, 30, 0, 0));
        nav.add(logo, BorderLayout.WEST);

        JPanel opciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        opciones.setOpaque(false);

        JButton btnProductos = crearBotonNav("Productos");
        JButton btnCarrito = crearBotonNav("Carrito");
        JButton btnPromociones = crearBotonNav("Promociones");
        JButton btnSalir = crearBotonNav("Salir");

        btnProductos.addActionListener(e -> {
            // Se recarga el catalogo para traer los descuentos vigentes desde la nube: asi cualquier
            // promocion lanzada por el Admin se ve reflejada al instante (precio tachado + precio con
            // descuento) sin que el cliente tenga que pulsar "Actualizar".
            panelProductos.cargarCatalogo();
            cardLayout.show(contentPanel, "Productos");
            activarBoton(btnProductos, btnCarrito, btnPromociones, btnSalir);
        });

        btnCarrito.addActionListener(e -> {
            panelCarrito.refrescar();
            cardLayout.show(contentPanel, "Carrito");
            activarBoton(btnCarrito, btnProductos, btnPromociones, btnSalir);
        });

        btnPromociones.addActionListener(e -> {
            panelPromociones.cargarPromociones();
            cardLayout.show(contentPanel, "Promociones");
            activarBoton(btnPromociones, btnProductos, btnCarrito, btnSalir);
        });

        btnSalir.addActionListener(e -> cerrarSesion());

        opciones.add(btnProductos);
        opciones.add(btnCarrito);
        opciones.add(btnPromociones);
        opciones.add(btnSalir);

        // Se inicia mostrando Promociones, asi que ese boton arranca resaltado como activo.
        activarBoton(btnPromociones, btnProductos, btnCarrito, btnSalir);

        nav.add(opciones, BorderLayout.CENTER);
        return nav;
    }

    private JButton crearBotonNav(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI Light", Font.PLAIN, 20));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(0x13, 0x2D, 0x46));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(new Color(0x01, 0xC3, 0x8E));
            }

            public void mouseExited(MouseEvent e) {
                btn.setForeground(Color.WHITE);
            }
        });

        return btn;
    }

    private void activarBoton(JButton activo, JButton... otros) {
        Color verdeActivo = new Color(1, 128, 95);
        Color azulNormal = new Color(0x13, 0x2D, 0x46);

        activo.setBackground(verdeActivo);
        for (JButton b : otros) {
            b.setBackground(azulNormal);
        }
    }

    private void cerrarSesion() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            SesionCliente.cerrarSesion();
            dispose();
            new InicioSesion().setVisible(true);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Menu Cliente");
        setMinimumSize(new java.awt.Dimension(1920, 1080));
        setPreferredSize(new java.awt.Dimension(1920, 1080));
        setResizable(false);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MenuCliente.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuCliente.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuCliente.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuCliente.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        }
        // </editor-fold>
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuCliente().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
