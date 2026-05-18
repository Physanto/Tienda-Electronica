package CapaPresentacion.GUI_Admin;
/**
 *
 * @author Marlon Vargas
 */
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
import java.nio.file.Paths;
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
import javax.swing.SwingUtilities;
/**
 * Ventana principal del módulo Administrador de la Tienda Electrónica.
 *
 * <p>Implementa el patrón de navegación Sidebar + CardLayout:
 * <ul>
 *   <li>El {@code JPanel} izquierdo ({@code sidebarPanel}) actúa como barra
 *       lateral de navegación con botones para cada módulo.</li>
 *   <li>El {@code JPanel} derecho ({@code contentPanel}) usa {@link CardLayout}
 *       para intercambiar los sub-paneles sin abrir nuevas ventanas.</li>
 * </ul>
 *
 * <p><b>Paneles registrados en el CardLayout:</b>
 * <ol>
 *   <li>PRODUCTOS  → {@link Productos}</li>
 *   <li>CLIENTES   → {@link Clientes}</li>
 *   <li>VENTAS     → {@link Ventas}</li>
 *   <li>PROMOCIONES → {@link Promociones}</li>
 *   <li>SETTINGS   → {@link Settings}</li>
 * </ol>
 *
 * @author Tienda-Electronica Team
 * @version 2.0
 */
public class Menu_Admin extends JFrame {
 

// ─────────────────────────────────────────────
// Constantes de diseño — Paleta Sborg
// ─────────────────────────────────────────────
    /**
     * Fondo principal de la sidebar (negro azulado).
     */
    private static final Color COLOR_SIDEBAR_BG = new Color(0x1A, 0x1E, 0x29);

    /**
     * Color del botón en estado normal.
     */
    private static final Color COLOR_BTN_NORMAL = new Color(0x13, 0x2D, 0x46);

    /**
     * Color del botón activo (sección seleccionada) — verde Sborg.
     */
    private static final Color COLOR_BTN_ACTIVO = new Color(0x01, 0xC3, 0x8E);

    /**
     * Color del botón en hover (intermedio entre normal y activo).
     */
    private static final Color COLOR_BTN_HOVER = new Color(0x1A, 0x3D, 0x58);

    /**
     * Texto de los botones — blanco puro.
     */
    private static final Color COLOR_TEXTO_BTN = new Color(0xFF, 0xFF, 0xFF);

    /**
     * Texto muted para etiquetas de sección y subtítulos.
     */
    private static final Color COLOR_TITULO_SIDEBAR = new Color(0x8A, 0xA5, 0xBE);

    /**
     * Fondo del área de contenido (el más oscuro como base).
     */
    private static final Color COLOR_CONTENIDO_BG = new Color(0x1A, 0x1E, 0x29);
 
    /** Ancho fijo de la sidebar en píxeles. */
    private static final int SIDEBAR_ANCHO = 245;
 
    // ─────────────────────────────────────────────
    // Claves para el CardLayout (identificadores)
    // ─────────────────────────────────────────────
 
    /** Clave del panel Productos en el CardLayout. */
    private static final String CARD_PRODUCTOS   = "PRODUCTOS";
 
    /** Clave del panel Clientes en el CardLayout. */
    private static final String CARD_CLIENTES    = "CLIENTES";
 
    /** Clave del panel Ventas en el CardLayout. */
    private static final String CARD_VENTAS      = "VENTAS";
 
    /** Clave del panel Promociones en el CardLayout. */
    private static final String CARD_PROMOCIONES = "PROMOCIONES";
 
    /** Clave del panel Settings en el CardLayout. */
    private static final String CARD_SETTINGS    = "SETTINGS";
 
    // ─────────────────────────────────────────────
    // Componentes principales
    // ─────────────────────────────────────────────
 
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
 
    // ─────────────────────────────────────────────
    // Botones de la sidebar
    // ─────────────────────────────────────────────
 
    /** Botón de navegación al panel Productos. */
    private JButton btnProductos;
 
    /** Botón de navegación al panel Clientes. */
    private JButton btnClientes;
 
    /** Botón de navegación al panel Ventas. */
    private JButton btnVentas;
 
    /** Botón de navegación al panel Promociones. */
    private JButton btnPromociones;
 
    /** Botón de navegación al panel Settings. */
    private JButton btnSettings;
 
    /**
     * Referencia al último botón activado.
     * Se usa para restablecer su color al cambiar de sección.
     */
    private JButton botonActivo;
    // ─────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────
    public String s;
    /**
     * Construye el JFrame principal del módulo Administrador.
     * Inicializa la sidebar, el CardLayout y registra todos los sub-paneles.
     */
    public Menu_Admin() {
        initComponents();
        s = Paths.get("").toAbsolutePath().toString();
        configurarVentana();
        construirSidebar();
        construirAreaContenido();
        ensamblarLayout();
        // Registrar el panel Inicio en el CardLayout (en construirAreaContenido)
        contentPanel.add(new Inicio(), "INICIO");
        // Mostrar Inicio sin activar ningún botón
        cardLayout.show(contentPanel, "INICIO");
    }
 
    /**
     * Configura las propiedades básicas del JFrame:
     * título, tamaño, comportamiento de cierre y centrado en pantalla.
     */
    private void configurarVentana() {
        setTitle("Tienda Electrónica — Panel Administrador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null); // Centra en pantalla
        setResizable(true);
        // BorderLayout divide el frame en WEST (sidebar) y CENTER (contenido)
        getContentPane().setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
 
    // ─────────────────────────────────────────────
    // Construcción de la Sidebar
    // ─────────────────────────────────────────────
 
    /**
     * Construye el panel lateral izquierdo (sidebar).
     */
    private void construirSidebar() {
        sidebarPanel = new JPanel() {
            /**
             * Sobrescribe paintComponent para aplicar un degradado vertical
             * sutil en la sidebar, de COLOR_SIDEBAR_BG a un tono ligeramente
             * más oscuro, añadiendo profundidad visual sin costo de rendimiento.
             *
             * @param g contexto gráfico del panel
             */
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                                     RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(
                    0, 0, COLOR_SIDEBAR_BG,
                    0, getHeight(), new Color(8, 13, 28)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
 
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setOpaque(false); // El degradado en paintComponent lo cubre
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
        btnProductos   = crearBotonSidebar("Productos",   CARD_PRODUCTOS, "Productos");
        btnClientes    = crearBotonSidebar("Clientes",    CARD_CLIENTES, "Clientes");
        btnVentas      = crearBotonSidebar("Ventas",      CARD_VENTAS, "Ventas");
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
            String pathLogo = s + "\\Images\\Icons\\Icono_Logo.png";
            Image img = ImageIO.read(new File(pathLogo));
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
     * Crea una etiqueta de sección pequeña en la sidebar (texto muted en mayúsculas).
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
     * <p>Al hacer clic, el botón invoca {@link #mostrarPanel(String, JButton)}
     * con la clave del panel correspondiente en el CardLayout.
     *
     * @param etiqueta  texto visible del botón (ej. "📦  Productos")
     * @param cardClave clave del panel a mostrar en el CardLayout
     * @return JButton completamente configurado
     */
    private JButton crearBotonSidebar(String etiqueta, String cardClave, String rutaIcono) {
        JButton btn = new JButton(etiqueta);

        // Cargar ícono
        try {
            String pathIcono = s + "\\Images\\Icons\\" + rutaIcono + ".png";
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
 
    // ─────────────────────────────────────────────
    // Construcción del área de contenido
    // ─────────────────────────────────────────────
 
    /**
     * Construye el panel de contenido dinámico usando {@link CardLayout}.
     *
     * <p>Cada sub-panel (JPanel hijo) se registra con una clave única de tipo
     * {@code String}. El CardLayout solo hace visible uno a la vez sin
     * destruir los demás, conservando su estado interno.
     *
     * <p>Paneles registrados:
     * <ul>
     *   <li>{@code CARD_PRODUCTOS}   → {@link Productos}</li>
     *   <li>{@code CARD_CLIENTES}    → {@link Clientes}</li>
     *   <li>{@code CARD_VENTAS}      → {@link Ventas}</li>
     *   <li>{@code CARD_PROMOCIONES} → {@link Promociones}</li>
     *   <li>{@code CARD_SETTINGS}    → {@link Settings}</li>
     * </ul>
     */
    private void construirAreaContenido() {
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(COLOR_CONTENIDO_BG);
 
        // Registrar cada sub-panel con su clave única
        contentPanel.add(new Productos(),   CARD_PRODUCTOS);
        contentPanel.add(new Clientes(),    CARD_CLIENTES);
        contentPanel.add(new Ventas(),      CARD_VENTAS);
        contentPanel.add(new Promociones(), CARD_PROMOCIONES);
        contentPanel.add(new Settings(),    CARD_SETTINGS);
    }
 
    // ─────────────────────────────────────────────
    // Ensamblaje del layout principal
    // ─────────────────────────────────────────────
 
    /**
     * Ensambla la estructura final del JFrame:
     * <ul>
     *   <li>{@code BorderLayout.WEST}   → {@code sidebarPanel}</li>
     *   <li>{@code BorderLayout.CENTER} → {@code contentPanel}</li>
     * </ul>
     * El panel CENTER se expande automáticamente con el resize del frame.
     */
    private void ensamblarLayout() {
        getContentPane().add(sidebarPanel,  BorderLayout.WEST);
        getContentPane().add(contentPanel,  BorderLayout.CENTER);
    }
 
    // ─────────────────────────────────────────────
    // Lógica de navegación
    // ─────────────────────────────────────────────
 
    /**
     * Muestra el panel indicado en el área de contenido y actualiza
     * el estado visual de los botones de la sidebar.
     *
     * <p>Pasos internos:
     * <ol>
     *   <li>Restaura el color normal del botón previamente activo.</li>
     *   <li>Aplica {@code COLOR_BTN_ACTIVO} al nuevo botón.</li>
     *   <li>Invoca {@code CardLayout.show()} con la clave del panel.</li>
     *   <li>Actualiza la referencia {@code botonActivo}.</li>
     * </ol>
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
 
    // ─────────────────────────────────────────────
    // Método generado por NetBeans (requerido)
    // ─────────────────────────────────────────────
 
    /**
     * Método requerido por NetBeans para compatibilidad con el Form Editor.
     * En este caso se delega toda la inicialización a los métodos privados
     * del constructor para mantener el código legible y estructurado.
     */

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1920, 1080));
        setPreferredSize(new java.awt.Dimension(1920, 1080));
        setResizable(false);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Menu_Admin().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
