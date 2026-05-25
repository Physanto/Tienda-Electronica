package CapaPresentacion.GUI_Admin;
 
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
 
/**
 * Panel de Configuración del módulo Administrador.
 *
 * Permite al administrador ajustar preferencias visuales y de comportamiento
 * que afectan los demás módulos (Clientes, Productos, Ventas, Promociones).
 * También contiene el botón de Salir para volver al Login.
 *
 * @author Marlon Vargas
 */
public class Settings extends javax.swing.JPanel {
 
    // ──────────────────────────────────────────────────────────────
    // Paleta de colores (coherente con MenuAdmin y demás paneles)
    // ──────────────────────────────────────────────────────────────
    private static final Color COLOR_BG        = new Color(0x1A, 0x1E, 0x29);
    private static final Color COLOR_PANEL_SEC = new Color(0x13, 0x2D, 0x46);
    private static final Color COLOR_ACENTO    = new Color(0x01, 0xC3, 0x8E);
    private static final Color COLOR_HOVER     = new Color(0x1A, 0x3D, 0x58);
    private static final Color COLOR_PELIGRO   = new Color(0xEF, 0x44, 0x44);
    private static final Color COLOR_TEXTO     = Color.WHITE;
    private static final Color COLOR_MUTED     = new Color(0x8A, 0xA5, 0xBE);
 
    // ──────────────────────────────────────────────────────────────
    // Controles de configuración
    // ──────────────────────────────────────────────────────────────
 
    // — Tabla
    private final JSpinner  spnFilasTabla  = crearSpinner(10, 5, 100, 5);
    private final JCheckBox chkGridLines   = crearCheck("Mostrar líneas de cuadrícula en tablas", true);
    private final JCheckBox chkRowHover    = crearCheck("Resaltar fila al hacer clic", true);
 
    // — Formulario lateral
    private final JCheckBox chkAutoCargar  = crearCheck("Cargar datos automáticamente al seleccionar fila", true);
    private final JCheckBox chkConfirmElim = crearCheck("Pedir confirmación antes de eliminar", true);
    private final JCheckBox chkLimpiarForm = crearCheck("Limpiar formulario al cancelar operación", true);
 
    // — Sincronización / Datos
    private final JCheckBox chkModoOffline = crearCheck("Activar modo offline al perder conexión", true);
    private final JCheckBox chkSyncAuto    = crearCheck("Sincronizar datos automáticamente al reconectar", true);
    private final JSpinner  spnIntervalo   = crearSpinner(30, 5, 300, 5);
 
    // — Módulo Promociones / K-Means
    private final JSpinner  spnClusters    = crearSpinner(3, 2, 5, 1);
    private final JCheckBox chkAutoKmeans  = crearCheck("Ejecutar K-Means al abrir módulo Promociones", true);
 
    // ──────────────────────────────────────────────────────────────
    // Constructor
    // ──────────────────────────────────────────────────────────────
    public Settings() {
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));
 
        add(crearEncabezado(),    BorderLayout.NORTH);
        add(crearCuerpo(),        BorderLayout.CENTER);
        add(crearPiePagina(),     BorderLayout.SOUTH);
    }
 
    // ──────────────────────────────────────────────────────────────
    // Secciones principales
    // ──────────────────────────────────────────────────────────────
 
    /** Título del panel. */
    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
 
        JLabel titulo = new JLabel("Configuración del Sistema");
        titulo.setFont(new Font("Segoe UI Light", Font.BOLD, 26));
        titulo.setForeground(COLOR_TEXTO);
 
        JLabel subtitulo = new JLabel("Ajusta el comportamiento y la visualización de los módulos.");
        subtitulo.setFont(new Font("Segoe UI Light", Font.PLAIN, 14));
        subtitulo.setForeground(COLOR_MUTED);
 
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);
 
        panel.add(textos, BorderLayout.WEST);
        return panel;
    }
 
    /** Área central con todas las tarjetas de configuración en scroll. */
    private JScrollPane crearCuerpo() {
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(COLOR_BG);
 
        contenido.add(crearTarjeta("Tablas y Visualización", new JComponent[]{
            crearFilaSpin("Filas máximas por tabla:", spnFilasTabla),
            chkGridLines,
            chkRowHover
        }));
        contenido.add(Box.createVerticalStrut(16));
 
        contenido.add(crearTarjeta("Formulario Lateral (CRUD)", new JComponent[]{
            chkAutoCargar,
            chkConfirmElim,
            chkLimpiarForm
        }));
        contenido.add(Box.createVerticalStrut(16));
 
        contenido.add(crearTarjeta("Sincronización y Conectividad", new JComponent[]{
            chkModoOffline,
            chkSyncAuto,
            crearFilaSpin("Intervalo de sincronización (seg):", spnIntervalo)
        }));
        contenido.add(Box.createVerticalStrut(16));
 
        contenido.add(crearTarjeta("Módulo Promociones / K-Means", new JComponent[]{
            crearFilaSpin("Número de clusters K-Means:", spnClusters),
            chkAutoKmeans
        }));
 
        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(COLOR_BG);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }
 
    /** Pie con botones Guardar y Salir. */
    private JPanel crearPiePagina() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));
 
        JButton btnGuardar = crearBoton("Guardar configuración", COLOR_ACENTO);
        btnGuardar.addActionListener(e -> guardarConfiguracion());
 
        JButton btnSalir = crearBoton("Salir", COLOR_PELIGRO);
        btnSalir.addActionListener(e -> cerrarSesion());
 
        panel.add(btnGuardar);
        panel.add(btnSalir);
        return panel;
    }
 
    // ──────────────────────────────────────────────────────────────
    // Constructores de componentes reutilizables
    // ──────────────────────────────────────────────────────────────
 
    /**
     * Crea una tarjeta de sección con título y lista de controles.
     *
     * @param titulo   nombre de la sección
     * @param controles componentes a mostrar dentro de la tarjeta
     */
    private JPanel crearTarjeta(String titulo, JComponent[] controles) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COLOR_PANEL_SEC);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        TitledBorder borde = BorderFactory.createTitledBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_ACENTO),
            "  " + titulo + "  ",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI Light", Font.BOLD, 14), COLOR_ACENTO
        );
        card.setBorder(BorderFactory.createCompoundBorder(
            borde, new EmptyBorder(10, 18, 16, 18)
        ));
 
        for (JComponent c : controles) {
            card.add(c);
            card.add(Box.createVerticalStrut(8));
        }
        return card;
    }
 
    /**
     * Fila de etiqueta + spinner para valores numéricos.
     *
     * @param etiqueta texto descriptivo
     * @param spinner  control de valor numérico
     */
    private JPanel crearFilaSpin(String etiqueta, JSpinner spinner) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        JLabel lbl = new JLabel(etiqueta);
        lbl.setForeground(COLOR_TEXTO);
        lbl.setFont(new Font("Segoe UI Light", Font.PLAIN, 14));
 
        fila.add(lbl);
        fila.add(spinner);
        return fila;
    }
 
    /**
     * Crea un JCheckBox estilizado con el tema oscuro.
     *
     * @param texto      etiqueta del checkbox
     * @param seleccionado estado inicial
     */
    private static JCheckBox crearCheck(String texto, boolean seleccionado) {
        JCheckBox chk = new JCheckBox(texto, seleccionado);
        chk.setForeground(Color.WHITE);
        chk.setFont(new Font("Segoe UI Light", Font.PLAIN, 14));
        chk.setOpaque(false);
        chk.setAlignmentX(Component.LEFT_ALIGNMENT);
        chk.setFocusPainted(false);
        return chk;
    }
 
    /**
     * Crea un JSpinner estilizado con el tema oscuro.
     *
     * @param valor   valor inicial
     * @param min     valor mínimo
     * @param max     valor máximo
     * @param paso    incremento por paso
     */
    private static JSpinner crearSpinner(int valor, int min, int max, int paso) {
        JSpinner sp = new JSpinner(new SpinnerNumberModel(valor, min, max, paso));
        sp.setPreferredSize(new Dimension(80, 30));
        sp.setBackground(new Color(0x0D, 0x12, 0x1E));
        sp.setForeground(Color.WHITE);
        sp.setFont(new Font("Segoe UI Light", Font.PLAIN, 14));
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) sp.getEditor();
        editor.getTextField().setBackground(new Color(0x0D, 0x12, 0x1E));
        editor.getTextField().setForeground(Color.WHITE);
        editor.getTextField().setCaretColor(new Color(0x01, 0xC3, 0x8E));
        editor.getTextField().setBorder(new EmptyBorder(4, 6, 4, 6));
        return sp;
    }
 
    /**
     * Crea un JButton estilizado con efecto hover.
     *
     * @param texto      texto del botón
     * @param fondo      color de fondo base
     */
    private JButton crearBoton(String texto, Color fondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI Light", Font.BOLD, 14));
        btn.setBackground(fondo);
        btn.setForeground(COLOR_TEXTO);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 24, 10, 24));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(fondo.brighter()); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(fondo); }
        });
        return btn;
    }
 
    // ──────────────────────────────────────────────────────────────
    // Acciones
    // ──────────────────────────────────────────────────────────────
 
    /**
     * Muestra confirmación y persiste la configuración actual.
     * La lógica de persistencia real se conecta aquí cuando esté disponible.
     */
    private void guardarConfiguracion() {
        JOptionPane.showMessageDialog(this,
            "Configuración guardada correctamente.",
            "Ajustes", JOptionPane.INFORMATION_MESSAGE);
    }
 
    /**
     * Cierra la sesión del administrador: destruye la ventana actual (MenuAdmin)
     * y abre el formulario de InicioSesión.
     */
    private void cerrarSesion() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Deseas cerrar la sesión y volver al inicio?",
            "Cerrar sesión", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
 
        if (confirm != JOptionPane.YES_OPTION) return;
 
        // Subir por la jerarquía de contenedores hasta encontrar el JFrame padre
        Window ventanaPadre = SwingUtilities.getWindowAncestor(this);
        if (ventanaPadre != null) ventanaPadre.dispose();
 
        // Abrir el Login en el hilo de Swing
        SwingUtilities.invokeLater(() -> new InicioSesion().setVisible(true));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jlb_fondo = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        add(jlb_fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -20, 1300, 900));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jlb_fondo;
    // End of variables declaration//GEN-END:variables
}
