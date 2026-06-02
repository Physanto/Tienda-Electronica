package CapaPresentacion.GUI_Admin;

/**
 *
 * @author Marlon Vargas
 */
import CapaLogicaNegocio.Helpers.OSHelper;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Ventana principal del módulo Administrador de la Tienda Electrónica.
 *
 * <p>
 * Implementa el patrón de navegación Sidebar + CardLayout:
 * <ul>
 * <li>El {@code JPanel} izquierdo ({@code sidebarPanel}) actúa como barra
 * lateral de navegación con botones para cada módulo.</li>
 * <li>El {@code JPanel} derecho ({@code contentPanel}) usa {@link CardLayout}
 * para intercambiar los sub-paneles sin abrir nuevas ventanas.</li>
 * </ul>
 * 
 * @author Marlon Vargas
 */
public class MenuAdmin extends JFrame {

    private static final Color COLOR_SIDEBAR_BG = new Color(0x1A, 0x1E, 0x29);
    private static final Color COLOR_BTN_NORMAL = new Color(0x13, 0x2D, 0x46);
    private static final Color COLOR_BTN_ACTIVO = new Color(1, 128, 95);
    private static final Color COLOR_BTN_HOVER = new Color(0x1A, 0x3D, 0x58);
    private static final Color COLOR_TEXTO_BTN = new Color(0xFF, 0xFF, 0xFF);
    private static final Color COLOR_TITULO_SIDEBAR = new Color(0x8A, 0xA5, 0xBE);
    private static final Color COLOR_CONTENIDO_BG = new Color(0x1A, 0x1E, 0x29);

    /** Ancho fijo de la sidebar en píxeles. */
    private static final int SIDEBAR_ANCHO = 245;

    private static final String CARD_PRODUCTOS = "PRODUCTOS";
    private static final String CARD_CLIENTES = "CLIENTES";
    private static final String CARD_VENTAS = "VENTAS";
    private static final String CARD_PROMOCIONES = "PROMOCIONES";
    private static final String CARD_SETTINGS = "SETTINGS";

    /** Panel izquierdo que contiene los botones de navegación. */
    private JPanel sidebarPanel;

    /**
     * Panel derecho que aloja los sub-paneles mediante CardLayout.
     * Es el "contenedor dinámico" de la interfaz.
     */
    private JPanel contentPanel;

    /**
     * Layout manager del contentPanel.
     * Permite intercambiar paneles por nombre sin destruirlos.
     */
    private CardLayout cardLayout;

    private JButton btnProductos;
    private JButton btnClientes;
    private JButton btnVentas;
    private JButton btnPromociones;
    private JButton btnSettings;

    private JButton botonActivo;

    public MenuAdmin() {
        initComponents();
        LinearLoadingDialog.mostrar(this, 1500);
        construirSidebar();
        construirAreaContenido();
        ensamblarLayout();
        contentPanel.add(new Inicio(), "INICIO");
        cardLayout.show(contentPanel, "INICIO");
        configurarVentana();
    }

    /**
     * Configura las propiedades básicas del JFrame:
     * título, tamaño, comportamiento de cierre y centrado en pantalla.
     */
    private void configurarVentana() {
        setTitle("Tienda Electrónica — Panel Administrador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setSize(1280, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /**
     * Construye el panel lateral izquierdo (sidebar).
     */
    private void construirSidebar() {
        sidebarPanel = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(
                        0, 0, COLOR_SIDEBAR_BG,
                        0, getHeight(), new Color(8, 13, 28));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setOpaque(false);
        sidebarPanel.setPreferredSize(new Dimension(SIDEBAR_ANCHO, 0));
        sidebarPanel.setMinimumSize(new Dimension(SIDEBAR_ANCHO, 0));
        sidebarPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // — Logo / Cabecera —
        sidebarPanel.add(crearCabeceraSidebar());
        sidebarPanel.add(Box.createVerticalStrut(8));

        // — Etiqueta de sección —
        sidebarPanel.add(crearEtiquetaSeccion("MÓDULOS"));
        sidebarPanel.add(Box.createVerticalStrut(4));

        // — Botones de navegación —
        btnProductos = crearBotonSidebar("Productos", CARD_PRODUCTOS, "Productos");
        btnClientes = crearBotonSidebar("Clientes", CARD_CLIENTES, "Clientes");
        btnVentas = crearBotonSidebar("Ventas", CARD_VENTAS, "Ventas");
        btnPromociones = crearBotonSidebar("️Promociones", CARD_PROMOCIONES, "Promos");

        sidebarPanel.add(btnProductos);
        sidebarPanel.add(btnClientes);
        sidebarPanel.add(btnVentas);
        sidebarPanel.add(btnPromociones);

        // Empuja el botón Settings hacia el fondo
        sidebarPanel.add(Box.createVerticalGlue());

        // — Separador visual antes de Settings —
        sidebarPanel.add(crearEtiquetaSeccion("SISTEMA"));
        sidebarPanel.add(Box.createVerticalStrut(4));

        btnSettings = crearBotonSidebar("Configuración", CARD_SETTINGS, "Config");
        sidebarPanel.add(btnSettings);
        sidebarPanel.add(Box.createVerticalStrut(16));
    }

    /**
     * Crea el panel de cabecera de la sidebar con el nombre del sistema
     * y el rol del usuario actual.
     *
     * @return JPanel configurado con logo y título
     */
    private JPanel crearCabeceraSidebar() {
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 80));
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(16, 12, 16, 12));

        // Ícono del logo
        JLabel lblLogo = new JLabel();
        try {
            String ruta = OSHelper.getImageFilePath("Icons/" + "Icono_Logo");
            Image img = ImageIO.read(new File(ruta));
            Image escalada = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(escalada));
        } catch (IOException e) {
            System.err.println("Logo no encontrado: " + e);
        }
        lblLogo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        // Panel vertical: nombre + rol
        JPanel txtPanel = new JPanel();
        txtPanel.setOpaque(false);
        txtPanel.setLayout(new BoxLayout(txtPanel, BoxLayout.Y_AXIS));

        JLabel lblNombre = new JLabel("NexByte");
        lblNombre.setFont(new Font("Segoe UI Light", Font.BOLD, 24));
        lblNombre.setForeground(Color.WHITE);

        JLabel lblRol = new JLabel("Menu Administrador");
        lblRol.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
        lblRol.setForeground(COLOR_TITULO_SIDEBAR);

        txtPanel.add(lblNombre);
        txtPanel.add(Box.createVerticalStrut(2));
        txtPanel.add(lblRol);

        logoPanel.add(lblLogo, BorderLayout.WEST);
        logoPanel.add(txtPanel, BorderLayout.CENTER);

        return logoPanel;
    }

    /**
     * Crea una etiqueta de sección pequeña en la sidebar (texto muted en
     * mayúsculas).
     *
     * @param texto texto de la etiqueta (ej. "MÓDULOS", "SISTEMA")
     * @return JLabel estilizado como etiqueta de grupo
     */
    private JLabel crearEtiquetaSeccion(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI light", Font.BOLD, 18));
        lbl.setForeground(new Color(100, 116, 139));
        lbl.setBorder(BorderFactory.createEmptyBorder(8, 12, 4, 0));
        lbl.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    /**
     * Crea un botón de navegación para la sidebar con los tres estados
     * visuales: normal, hover y activo.
     *
     * <p>
     * Al hacer clic, el botón invoca {@link #mostrarPanel(String, JButton)}
     * con la clave del panel correspondiente en el CardLayout.
     *
     * @param etiqueta  texto visible del botón (ej. " Productos")
     * @param cardClave clave del panel a mostrar en el CardLayout
     * @return JButton completamente configurado
     */
    private JButton crearBotonSidebar(String etiqueta, String cardClave, String rutaIcono) {
        JButton btn = new JButton(etiqueta);

        // Cargar ícono
        try {
            String pathIcono = OSHelper.getImageFilePath("Icons/" + rutaIcono);
            Image img = ImageIO.read(new File(pathIcono));
            Image escalada = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(escalada));
        } catch (IOException e) {
            System.err.println("Icono no encontrado: " + e);
        }

        btn.setIconTextGap(10);
        btn.setFont(new Font("Segoe UI Light", Font.PLAIN, 18));
        btn.setForeground(COLOR_TEXTO_BTN);
        btn.setBackground(COLOR_BTN_NORMAL);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(11, 16, 11, 16));
        btn.setMinimumSize(new Dimension(SIDEBAR_ANCHO, 44));
        btn.setPreferredSize(new Dimension(Short.MAX_VALUE, 44));
        btn.setMaximumSize(new Dimension(Short.MAX_VALUE, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Estados hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != botonActivo) {
                    btn.setBackground(COLOR_BTN_HOVER);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != botonActivo) {
                    btn.setBackground(COLOR_BTN_NORMAL);
                }
            }
        });

        btn.addActionListener(e -> mostrarPanel(cardClave, btn));

        return btn;
    }

    /**
     * Construye el panel de contenido dinámico usando {@link CardLayout}.
     *
     * <p>
     * Cada sub-panel (JPanel hijo) se registra con una clave única de tipo
     * {@code String}. El CardLayout solo hace visible uno a la vez sin
     * destruir los demás, conservando su estado interno.
     */
    private void construirAreaContenido() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(COLOR_CONTENIDO_BG);

        // Registrar cada sub-panel con su clave única
        contentPanel.add(new Productos(), CARD_PRODUCTOS);
        contentPanel.add(new Clientes(), CARD_CLIENTES);
        contentPanel.add(new Ventas(), CARD_VENTAS);
        contentPanel.add(new Promociones(), CARD_PROMOCIONES);
        contentPanel.add(new Settings(), CARD_SETTINGS);
    }

    /**
     * Ensambla la estructura final del JFrame:
     * <ul>
     * <li>{@code BorderLayout.WEST} → {@code sidebarPanel}</li>
     * <li>{@code BorderLayout.CENTER} → {@code contentPanel}</li>
     * </ul>
     * El panel CENTER se expande automáticamente con el resize del frame.
     */
    private void ensamblarLayout() {
        getContentPane().add(sidebarPanel, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Muestra el panel indicado en el área de contenido y actualiza
     * el estado visual de los botones de la sidebar.
     *
     * @param cardClave clave del panel a mostrar (debe coincidir con el nombre
     *                  registrado en {@link #construirAreaContenido()})
     * @param boton     botón de la sidebar que disparó la navegación
     */
    private void mostrarPanel(String cardClave, JButton boton) {
        // 1. Desactivar botón anterior
        if (botonActivo != null) {
            botonActivo.setBackground(COLOR_BTN_NORMAL);
            botonActivo.setFont(new Font("Segoe UI Light", Font.PLAIN, 18));
        }

        // 2. Activar nuevo botón
        boton.setBackground(COLOR_BTN_ACTIVO);
        boton.setFont(new Font("Segoe UI Light", Font.BOLD, 18));
        botonActivo = boton;

        // 3. Cambiar el panel visible en el CardLayout
        cardLayout.show(contentPanel, cardClave);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1920, 1080));
        setPreferredSize(new java.awt.Dimension(1920, 1080));
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
        // (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the default
         * look and feel.
         * For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MenuAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        // </editor-fold>
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuAdmin().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
