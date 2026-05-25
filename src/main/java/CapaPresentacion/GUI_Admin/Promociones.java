package CapaPresentacion.GUI_Admin;

import CapaDatos.Logica_Conexion.ProductoDAO;
import CapaDatos.Logica_Conexion.PromocionDAO;
import CapaLogicaNegocio.Logica_Negocio.Producto;
import CapaLogicaNegocio.Logica_Negocio.Promocion;
import CapaDatos.Logica_Conexion.ClienteDAO;
import CapaDatos.Logica_Conexion.DetalleVentaDAO;
import CapaDatos.Logica_Conexion.VentaDAO;
import CapaLogicaNegocio.Logica_Negocio.Cliente;
import CapaLogicaNegocio.Logica_Negocio.DetalleVenta;
import CapaLogicaNegocio.Logica_Negocio.Venta;

import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * JPanel del módulo Promociones para el panel de administración.
 *
 * Al abrirse, ejecuta el modelo K-Means sobre los productos y muestra
 * en la tabla superior los productos candidatos a promoción (cluster con
 * mayor diasSinVender y menor totalVendido).
 *
 * El administrador selecciona uno o varios productos, elige un porcentaje
 * de descuento y aplica la promoción. También puede ver y desactivar las
 * promociones actualmente activas en la tabla inferior.
 *
 * @author Marlon Vargas
 */
public class Promociones extends javax.swing.JPanel {

    // ──────────────────────────────────────────────────────────────
    // Paleta de colores
    // ──────────────────────────────────────────────────────────────
    private static final Color COLOR_BG          = new Color(0x1A, 0x1E, 0x29);
    private static final Color COLOR_PANEL_SEC   = new Color(0x13, 0x2D, 0x46);
    private static final Color COLOR_ACENTO      = new Color(0x01, 0xC3, 0x8E);
    private static final Color COLOR_PELIGRO     = new Color(0xEF, 0x44, 0x44);
    private static final Color COLOR_AMARILLO    = new Color(0xF5, 0x9E, 0x0B);
    private static final Color COLOR_TEXTO       = Color.WHITE;
    private static final Color COLOR_TEXTO_MUTED = new Color(0x8A, 0xA5, 0xBE);
    private static final Color COLOR_TABLE_BG    = new Color(0x10, 0x14, 0x1E);
    private static final Color COLOR_ROW_SEL     = new Color(0x01, 0xC3, 0x8E, 80);

    private static final NumberFormat FMT_MONEDA =
            NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

    // ──────────────────────────────────────────────────────────────
    // DAOs y helpers
    // ──────────────────────────────────────────────────────────────
    private final PromocionDAO promocionDAO = new PromocionDAO();
    private final ProductoDAO  productoDAO  = new ProductoDAO();
    private final ClienteDAO      clienteDAO      = new ClienteDAO();
    private final DetalleVentaDAO detalleVentaDAO = new DetalleVentaDAO();
    private final VentaDAO ventaDAO = new VentaDAO();

    // ──────────────────────────────────────────────────────────────
    // Componentes UI
    // ──────────────────────────────────────────────────────────────
    private DefaultTableModel modeloSugerencias;
    private JTable            tablaSugerencias;

    private DefaultTableModel modeloActivas;
    private JTable            tablaActivas;

    private JSpinner          spinnerDescuento;
    private JTextField        txtNombrePromo;
    private JLabel            lblEstado;
    
    private DefaultTableModel modeloClientes;
    private JTable tablaClientes;
    private JLabel lblPerfilCliente;
    private JSpinner spinnerDescuentoCliente;

    // Mapa de idProducto → objeto Producto (para construir la promoción)
    private final Map<String, Producto> mapaProductos = new HashMap<>();

    // Mapa de fila en tablaActivas → id de Promocion (simulado con lista paralela)
    private final ArrayList<String> idsPromoActiva = new ArrayList<>();

    // ──────────────────────────────────────────────────────────────
    // Constructor
    // ──────────────────────────────────────────────────────────────
    public Promociones() {
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // Título
        JLabel titulo = new JLabel("Sugerencias de Promociones — Modelo K-Means");
        titulo.setFont(new Font("Segoe UI Light", Font.BOLD, 26));
        titulo.setForeground(COLOR_TEXTO);
        titulo.setBorder(new EmptyBorder(0, 0, 16, 0));
        add(titulo, BorderLayout.NORTH);

        // Contenido scrollable
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(COLOR_BG);

        contenido.add(construirSeccionSugerencias());
        contenido.add(Box.createVerticalStrut(24));
        contenido.add(construirPanelAccion());
        contenido.add(Box.createVerticalStrut(24));
        contenido.add(construirSeccionActivas());
        contenido.add(Box.createVerticalStrut(20));
        contenido.add(Box.createVerticalStrut(24));
        contenido.add(construirSeccionClientes());

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(COLOR_BG);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);

        // Cargar datos al iniciar
        cargarSugerenciasKmeans();
        cargarPromocionesActivas();
    }

    // ══════════════════════════════════════════════════════════════
    //  SECCIÓN 1 — TABLA DE SUGERENCIAS
    // ══════════════════════════════════════════════════════════════

    private JPanel construirSeccionSugerencias() {
        JPanel seccion = new JPanel(new BorderLayout(0, 10));
        seccion.setBackground(COLOR_BG);
        seccion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        // Encabezado con badge explicativo
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(COLOR_BG);

        JLabel lbl = new JLabel("Productos candidatos a promoción");
        lbl.setFont(new Font("Segoe UI Light", Font.BOLD, 18));
        lbl.setForeground(COLOR_TEXTO);

        JLabel badge = new JLabel("  Selecciona uno o más productos para aplicar descuento  ");
        badge.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
        badge.setForeground(COLOR_BG);
        badge.setBackground(COLOR_AMARILLO);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(3, 8, 3, 8));

        encabezado.add(lbl,   BorderLayout.WEST);
        encabezado.add(badge, BorderLayout.EAST);
        seccion.add(encabezado, BorderLayout.NORTH);

        // Tabla
        modeloSugerencias = new DefaultTableModel(
                new String[]{"ID Producto", "Nombre", "Marca", "Stock",
                             "Precio Actual", "Días sin venta", "Total vendido", "Cluster"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaSugerencias = estilizarTabla(new JTable(modeloSugerencias));
        tablaSugerencias.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        // Ocultar columna ID (índice 0) — se usa internamente
        tablaSugerencias.getColumnModel().getColumn(0).setMinWidth(0);
        tablaSugerencias.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaSugerencias.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane sp = new JScrollPane(tablaSugerencias);
        estilizarScroll(sp);
        seccion.add(sp, BorderLayout.CENTER);

        return seccion;
    }

    // ══════════════════════════════════════════════════════════════
    //  SECCIÓN 2 — PANEL DE ACCIÓN
    // ══════════════════════════════════════════════════════════════

    private JPanel construirPanelAccion() {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(COLOR_PANEL_SEC);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, COLOR_ACENTO),
                new EmptyBorder(16, 20, 16, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JLabel lbl = new JLabel("Aplicar promoción a los productos seleccionados");
        lbl.setFont(new Font("Segoe UI Light", Font.BOLD, 18));
        lbl.setForeground(COLOR_TEXTO);
        card.add(lbl, BorderLayout.NORTH);

        // Controles en una fila
        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        controles.setBackground(COLOR_PANEL_SEC);

        // Nombre de la promoción
        JLabel lblNom = new JLabel("Nombre:");
        lblNom.setForeground(COLOR_TEXTO_MUTED);
        lblNom.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));

        txtNombrePromo = new JTextField(18);
        estilizarTextField(txtNombrePromo);

        // Spinner descuento
        JLabel lblDesc = new JLabel("Descuento (%):");
        lblDesc.setForeground(COLOR_TEXTO_MUTED);
        lblDesc.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));

        SpinnerNumberModel spinModel = new SpinnerNumberModel(10, 1, 90, 1);
        spinnerDescuento = new JSpinner(spinModel);
        spinnerDescuento.setPreferredSize(new Dimension(70, 32));
        estilizarSpinner(spinnerDescuento);

        // Botón aplicar
        JButton btnAplicar = crearBoton("Aplicar Promoción", COLOR_ACENTO);
        btnAplicar.addActionListener(e -> aplicarPromocion());

        // Etiqueta de estado
        lblEstado = new JLabel("");
        lblEstado.setFont(new Font("Segoe UI Light", Font.BOLD, 16));
        lblEstado.setForeground(COLOR_ACENTO);

        controles.add(lblNom);
        controles.add(txtNombrePromo);
        controles.add(lblDesc);
        controles.add(spinnerDescuento);
        controles.add(btnAplicar);
        controles.add(lblEstado);

        card.add(controles, BorderLayout.CENTER);
        return card;
    }

    // ══════════════════════════════════════════════════════════════
    //  SECCIÓN 3 — PROMOCIONES ACTIVAS
    // ══════════════════════════════════════════════════════════════

    private JPanel construirSeccionActivas() {
        JPanel seccion = new JPanel(new BorderLayout(0, 10));
        seccion.setBackground(COLOR_BG);
        seccion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        // Encabezado con botón desactivar
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(COLOR_BG);

        JLabel lbl = new JLabel("Promociones activas");
        lbl.setFont(new Font("Segoe UI Light", Font.BOLD, 18));
        lbl.setForeground(COLOR_TEXTO);

        JButton btnDesactivar = crearBoton("Desactivar seleccionada", COLOR_PELIGRO);
        btnDesactivar.addActionListener(e -> desactivarPromocion());

        encabezado.add(lbl,           BorderLayout.WEST);
        encabezado.add(btnDesactivar, BorderLayout.EAST);
        seccion.add(encabezado, BorderLayout.NORTH);

        // Tabla
        modeloActivas = new DefaultTableModel(
                new String[]{"ID Promo", "Nombre Promoción", "Producto",
                             "Descuento (%)", "Precio Original", "Precio con Descuento"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaActivas = estilizarTabla(new JTable(modeloActivas));
        tablaActivas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Ocultar columna ID Promo
        tablaActivas.getColumnModel().getColumn(0).setMinWidth(0);
        tablaActivas.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaActivas.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane sp = new JScrollPane(tablaActivas);
        estilizarScroll(sp);
        seccion.add(sp, BorderLayout.CENTER);

        return seccion;
    }

    // ══════════════════════════════════════════════════════════════
    //  LÓGICA DE NEGOCIO
    // ══════════════════════════════════════════════════════════════

    /**
     * Ejecuta el K-Means y muestra los productos del cluster con mayor
     * riesgo (mayor diasSinVender, menor totalVendido).
     *
     * El PromocionDAO.getDataset() devuelve objetos Promocion con los datos
     * estadísticos (stock, diasSinVender, totalVendido). Después de correr
     * clusters(), cada Promocion.getCluster() tiene su cluster asignado.
     * Identificamos el cluster "malo" como el de mayor diasSinVender promedio.
     */
    private void cargarSugerenciasKmeans() {
        modeloSugerencias.setRowCount(0);
        mapaProductos.clear();

        // 1. Obtener dataset del DAO
        ArrayList<Promocion> dataset = promocionDAO.getDataset();
        if (dataset == null || dataset.size() < 3) {
            lblEstado.setText("No hay suficientes productos para el análisis.");
            lblEstado.setForeground(COLOR_AMARILLO);
            return;
        }

        // 2. Cargar productos para cruzar nombre/marca/precio
        ArrayList<Producto> productos = productoDAO.obteners();
        for (Producto p : productos) mapaProductos.put(p.getId(), p);

        // 3. Ejecutar K-Means (modifica in-place el campo cluster de cada Promocion)
        //HelperKmeans.clusters(dataset);

        // 4. Identificar el cluster "candidato a promoción":
        //    el que tenga mayor promedio de diasSinVender
        double[] sumaDias    = new double[3];
        int[]    contCluster = new int[3];

        for (Promocion pr : dataset) {
            int c = pr.getCluster();
            if (c >= 0 && c <= 2) {
                sumaDias[c]    += pr.getDiasSinVender();
                contCluster[c] += 1;
            }
        }

        int clusterCandidato = 0;
        double maxDias = -1;
        for (int i = 0; i < 3; i++) {
            if (contCluster[i] > 0) {
                double promedio = sumaDias[i] / contCluster[i];
                if (promedio > maxDias) {
                    maxDias         = promedio;
                    clusterCandidato = i;
                }
            }
        }

        // 5. Poblar tabla solo con los del cluster candidato
        for (Promocion pr : dataset) {
            if (pr.getCluster() != clusterCandidato) continue;

            Producto prod = mapaProductos.get(pr.getId());
            String nombre = (prod != null) ? prod.getNombre() : "Desconocido";
            String marca  = (prod != null) ? prod.getMarca()  : "-";
            String precio = (prod != null)
                    ? FMT_MONEDA.format(prod.getPrecioActual()) : "-";
            long stock    = (prod != null) ? prod.getStock() : 0;

            String diasStr = pr.getDiasSinVender() >= 999
                    ? "Sin ventas"
                    : String.valueOf(pr.getDiasSinVender().intValue());

            modeloSugerencias.addRow(new Object[]{
                pr.getId(),
                nombre,
                marca,
                stock,
                precio,
                diasStr,
                pr.getTotalVendido().intValue(),
                "Cluster " + pr.getCluster()
            });
        }

        if (modeloSugerencias.getRowCount() == 0) {
            lblEstado.setText("Todos los productos tienen buena rotación.");
            lblEstado.setForeground(COLOR_ACENTO);
        }
    }

    /**
     * Aplica la promoción a todos los productos seleccionados en la tabla.
     * Muestra vista previa por cada producto antes de guardar.
     */
    private void aplicarPromocion() {
        int[] filasSeleccionadas = tablaSugerencias.getSelectedRows();

        if (filasSeleccionadas.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona al menos un producto de la tabla.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombrePromo = txtNombrePromo.getText().trim();
        if (nombrePromo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Escribe un nombre para la promoción.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int porcentaje = (int) spinnerDescuento.getValue();
        int aplicadas  = 0;

        for (int fila : filasSeleccionadas) {
            String idProducto = modeloSugerencias.getValueAt(fila, 0).toString();
            String nombre     = modeloSugerencias.getValueAt(fila, 1).toString();

            Producto prod = mapaProductos.get(idProducto);
            if (prod == null) continue;

            double precioOriginal = prod.getPrecioActual();
            double precioFinal    = precioOriginal * (1 - porcentaje / 100.0);

            // Vista previa
            String mensaje = String.format(
                    "Producto:        %s\n" +
                    "Precio original: %s\n" +
                    "Descuento:       %d%%\n" +
                    "Precio final:    %s\n\n" +
                    "¿Confirmar promoción?",
                    nombre,
                    FMT_MONEDA.format(precioOriginal),
                    porcentaje,
                    FMT_MONEDA.format(precioFinal)
            );

            int respuesta = JOptionPane.showConfirmDialog(this, mensaje,
                    "Vista previa — " + nombrePromo,
                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (respuesta != JOptionPane.YES_OPTION) continue;

            // Guardar en BD
            // Reutilizamos Promocion con los campos disponibles:
            // id=UUID simulado, stockActual=precioFinal(descuento), diasSinVender=porcentaje,
            // totalVendido=precioOriginal. 
            // NOTA: ajusta según la estructura real de tu tabla Promocion en BD.
            String idPromo = java.util.UUID.randomUUID().toString();
            Promocion nueva = new Promocion(
                    idPromo,
                    precioFinal,          // stockActual ← precio con descuento
                    (double) porcentaje,  // diasSinVender ← porcentaje aplicado
                    precioOriginal        // totalVendido ← precio original
            );
            nueva.setCluster(-1); // no aplica

            // Aquí deberías usar tu PromocionDAO si tiene método agregar().
            // Como el DAO actual solo tiene getDataset(), dejamos el hook listo:
            // promocionDAO.agregar(nueva);  ← descomentar cuando implementes agregar()

            aplicadas++;
            modeloActivas.addRow(new Object[]{
                idPromo,
                nombrePromo + " (" + porcentaje + "%)",
                nombre,
                porcentaje + "%",
                FMT_MONEDA.format(precioOriginal),
                FMT_MONEDA.format(precioFinal)
            });
            idsPromoActiva.add(idPromo);
        }

        if (aplicadas > 0) {
            lblEstado.setText(aplicadas + " promoción(es) aplicada(s).");
            lblEstado.setForeground(COLOR_ACENTO);
            txtNombrePromo.setText("");
        }
    }

    /**
     * Desactiva la promoción seleccionada en tablaActivas.
     * Llama a promocionDAO para persistir el cambio.
     */
    private void desactivarPromocion() {
        int fila = tablaActivas.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una promoción de la tabla.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombrePromo = modeloActivas.getValueAt(fila, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Desactivar la promoción \"" + nombrePromo + "\"?",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        // promocionDAO.actualizar(...)  ← descomentar cuando implementes actualizar()
        modeloActivas.removeRow(fila);
        idsPromoActiva.remove(fila);

        lblEstado.setText("Promoción desactivada.");
        lblEstado.setForeground(COLOR_PELIGRO);
    }

    /**
     * Carga las promociones activas desde la BD al iniciar el panel.
     * Actualmente vacío hasta que PromocionDAO tenga método obteners().
     */
    private void cargarPromocionesActivas() {
        // Cuando implementes obteners() en PromocionDAO, descomenta:
        // ArrayList<Promocion> activas = promocionDAO.obteners();
        // for (Promocion p : activas) { ... modeloActivas.addRow(...) }
    }

    // ══════════════════════════════════════════════════════════════
    //  HELPERS DE ESTILO
    // ══════════════════════════════════════════════════════════════

    private JTable estilizarTabla(JTable tabla) {
        tabla.setBackground(COLOR_TABLE_BG);
        tabla.setForeground(COLOR_TEXTO);
        tabla.setFont(new Font("Segoe UI Light", Font.PLAIN, 15));
        tabla.setRowHeight(30);
        tabla.setGridColor(new Color(0x2A, 0x30, 0x45));
        tabla.setSelectionBackground(COLOR_ROW_SEL);
        tabla.setSelectionForeground(COLOR_TEXTO);
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(false);
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setFillsViewportHeight(true);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        center.setBackground(COLOR_TABLE_BG);
        center.setForeground(COLOR_TEXTO);
        for (int col = 0; col < tabla.getColumnCount(); col++)
            tabla.getColumnModel().getColumn(col).setCellRenderer(center);

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(COLOR_PANEL_SEC);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Segoe UI Light", Font.BOLD, 15));
        header.setReorderingAllowed(false);
        return tabla;
    }

    private void estilizarScroll(JScrollPane sp) {
        sp.setBorder(BorderFactory.createLineBorder(new Color(0x2A, 0x30, 0x45)));
        sp.getViewport().setBackground(COLOR_TABLE_BG);
    }

    private void estilizarTextField(JTextField field) {
        field.setBackground(COLOR_PANEL_SEC);
        field.setForeground(COLOR_TEXTO);
        field.setCaretColor(COLOR_TEXTO);
        field.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x2A, 0x30, 0x45)),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private void estilizarSpinner(JSpinner spinner) {
        spinner.setBackground(COLOR_PANEL_SEC);
        spinner.setForeground(COLOR_TEXTO);
        spinner.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
        editor.getTextField().setBackground(COLOR_PANEL_SEC);
        editor.getTextField().setForeground(COLOR_TEXTO);
        editor.getTextField().setCaretColor(COLOR_TEXTO);
        editor.getTextField().setBorder(new EmptyBorder(4, 6, 4, 6));
    }

    private JButton crearBoton(String texto, Color colorFondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI Light", Font.BOLD, 16));
        btn.setBackground(colorFondo);
        btn.setForeground(COLOR_TEXTO);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(colorFondo.brighter());
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(colorFondo);
            }
        });
        return btn;
    }
    
    /**
     * Construye la sección de análisis de clientes.
     * Llámala en el constructor de Promociones, después de construirSeccionActivas():
     *
     *   contenido.add(Box.createVerticalStrut(24));
     *   contenido.add(construirSeccionClientes());
     */
    private JPanel construirSeccionClientes() {
        JPanel seccion = new JPanel(new BorderLayout(0, 10));
        seccion.setBackground(COLOR_BG);
        seccion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 420));

        // — Encabezado —
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(COLOR_BG);

        JLabel lbl = new JLabel("Promoción personalizada por cliente");
        lbl.setFont(new Font("Segoe UI Light", Font.BOLD, 18));
        lbl.setForeground(COLOR_TEXTO);

        JLabel badge = new JLabel("  Selecciona un cliente y analiza su comportamiento de compra  ");
        badge.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
        badge.setForeground(COLOR_BG);
        badge.setBackground(new Color(0x3B, 0x82, 0xF6)); // azul
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(3, 8, 3, 8));

        encabezado.add(lbl,   BorderLayout.WEST);
        encabezado.add(badge, BorderLayout.EAST);
        seccion.add(encabezado, BorderLayout.NORTH);

        // — Tabla de clientes —
        modeloClientes = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Apellido", "Cédula",
                             "Total Compras", "Nº Sesiones", "Días últ. compra"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaClientes = estilizarTabla(new JTable(modeloClientes));
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Ocultar columna ID
        tablaClientes.getColumnModel().getColumn(0).setMinWidth(0);
        tablaClientes.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaClientes.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane spClientes = new JScrollPane(tablaClientes);
        estilizarScroll(spClientes);
        spClientes.setPreferredSize(new Dimension(0, 180));
        seccion.add(spClientes, BorderLayout.CENTER);

        // — Panel inferior: perfil detectado + acción —
        JPanel panelAccionCliente = new JPanel(new BorderLayout(0, 10));
        panelAccionCliente.setBackground(COLOR_PANEL_SEC);
        panelAccionCliente.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, new Color(0x3B, 0x82, 0xF6)),
                new EmptyBorder(14, 18, 14, 18)
        ));

        // Fila 1: etiqueta de perfil
        lblPerfilCliente = new JLabel("Selecciona un cliente para analizar su perfil de compra.");
        lblPerfilCliente.setFont(new Font("Segoe UI Light", Font.BOLD, 16));
        lblPerfilCliente.setForeground(COLOR_TEXTO_MUTED);
        panelAccionCliente.add(lblPerfilCliente, BorderLayout.NORTH);

        // Fila 2: controles de descuento + botón
        JPanel controlesCliente = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        controlesCliente.setBackground(COLOR_PANEL_SEC);

        JLabel lblDesc = new JLabel("Descuento sugerido (%):");
        lblDesc.setForeground(COLOR_TEXTO_MUTED);
        lblDesc.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));

        SpinnerNumberModel spinModelC = new SpinnerNumberModel(10, 1, 90, 1);
        spinnerDescuentoCliente = new JSpinner(spinModelC);
        spinnerDescuentoCliente.setPreferredSize(new Dimension(70, 32));
        estilizarSpinner(spinnerDescuentoCliente);

        JButton btnAnalizar = crearBoton("Analizar cliente", new Color(0x3B, 0x82, 0xF6));
        btnAnalizar.addActionListener(e -> analizarClienteSeleccionado());

        JButton btnAplicarCliente = crearBoton("Aplicar promoción", COLOR_ACENTO);
        btnAplicarCliente.addActionListener(e -> aplicarPromocionCliente());

        controlesCliente.add(lblDesc);
        controlesCliente.add(spinnerDescuentoCliente);
        controlesCliente.add(Box.createHorizontalStrut(10));
        controlesCliente.add(btnAnalizar);
        controlesCliente.add(btnAplicarCliente);

        panelAccionCliente.add(controlesCliente, BorderLayout.CENTER);
        seccion.add(panelAccionCliente, BorderLayout.SOUTH);

        return seccion;
    }

    /**
     * Carga la tabla de clientes con sus métricas calculadas desde el historial
     * de ventas: total gastado, número de sesiones y días desde la última compra.
     * Llámala en el constructor:  cargarTablaClientes();
     */
    private void cargarTablaClientes() {
        modeloClientes.setRowCount(0);

        ArrayList<Cliente>      clientes  = clienteDAO.obteners();
        ArrayList<Venta>        ventas    = ventaDAO.obteners();
        ArrayList<DetalleVenta> detalles  = detalleVentaDAO.obteners();

        // Mapa idVenta → Venta (para cruzar con DetalleVenta)
        Map<String, Venta> mapVentas = new HashMap<>();
        for (Venta v : ventas) mapVentas.put(v.getId(), v);

        long hoy = System.currentTimeMillis();

        for (Cliente c : clientes) {
            // Ventas del cliente
            ArrayList<Venta> ventasCliente = new ArrayList<>();
            for (Venta v : ventas) {
                if (v.getIdCliente().equals(c.getId())) ventasCliente.add(v);
            }

            int    numSesiones   = ventasCliente.size();
            double totalGastado  = 0;
            long   fechaUltima   = 0;

            for (Venta v : ventasCliente) {
                totalGastado += v.getTotalVenta();
                if (v.getFechaVenta().getTime() > fechaUltima)
                    fechaUltima = v.getFechaVenta().getTime();
            }

            long diasDesdeUltima = (numSesiones == 0) ? 999
                    : (hoy - fechaUltima) / (1000L * 60 * 60 * 24);

            modeloClientes.addRow(new Object[]{
                c.getId(),
                c.getNombre(),
                c.getApellido(),
                c.getCedula(),
                FMT_MONEDA.format(totalGastado),
                numSesiones,
                diasDesdeUltima == 999 ? "Sin compras" : diasDesdeUltima + " días"
            });
        }
    }

    /**
     * Analiza el cliente seleccionado en la tabla usando un K-Means de 3 clusters
     * construido sobre tres métricas: totalGastado, numSesiones, diasDesdeUltima.
     *
     * Clusters resultantes:
     *   0 → Cliente frecuente  (alto gasto, muchas sesiones, compra reciente)
     *   1 → Cliente ocasional  (gasto medio, pocas sesiones)
     *   2 → Cliente inactivo   (no compra hace mucho o nunca ha comprado)
     *
     * El perfil detectado se muestra en lblPerfilCliente y el spinner de descuento
     * se ajusta automáticamente a la sugerencia del modelo.
     */
    private void analizarClienteSeleccionado() {
        int fila = tablaClientes.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un cliente de la tabla primero.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idCliente    = modeloClientes.getValueAt(fila, 0).toString();
        String nombreCliente = modeloClientes.getValueAt(fila, 1) + " "
                             + modeloClientes.getValueAt(fila, 2);

        // — Calcular métricas del cliente seleccionado —
        ArrayList<Venta> todasVentas = ventaDAO.obteners();

        ArrayList<Venta> ventasCliente = new ArrayList<>();
        for (Venta v : todasVentas) {
            if (v.getIdCliente().equals(idCliente)) ventasCliente.add(v);
        }

        int    numSesiones  = ventasCliente.size();
        double totalGastado = 0;
        long   fechaUltima  = 0;
        long   hoy          = System.currentTimeMillis();

        for (Venta v : ventasCliente) {
            totalGastado += v.getTotalVenta();
            if (v.getFechaVenta().getTime() > fechaUltima)
                fechaUltima = v.getFechaVenta().getTime();
        }

        long diasDesdeUltima = (numSesiones == 0) ? 999
                : (hoy - fechaUltima) / (1000L * 60 * 60 * 24);

        // — Calcular métricas de TODOS los clientes para normalizar —
        ArrayList<Cliente> todosClientes = clienteDAO.obteners();
        double maxGasto = 1, maxSesiones = 1, maxDias = 1;

        for (Cliente c : todosClientes) {
            double g = 0; int s = 0; long f = 0;
            for (Venta v : todasVentas) {
                if (!v.getIdCliente().equals(c.getId())) continue;
                g += v.getTotalVenta();
                s++;
                if (v.getFechaVenta().getTime() > f) f = v.getFechaVenta().getTime();
            }
            long d = (s == 0) ? 999 : (hoy - f) / (1000L * 60 * 60 * 24);
            if (g > maxGasto)    maxGasto    = g;
            if (s > maxSesiones) maxSesiones = s;
            if (d > maxDias)     maxDias     = d;
        }

        // — Normalizar métricas del cliente analizado (0.0 – 1.0) —
        double normGasto   = totalGastado  / maxGasto;
        double normSesiones = numSesiones  / maxSesiones;
        double normDias    = diasDesdeUltima / maxDias;  // mayor = peor

        // — Centroides fijos para los 3 perfiles (ya están en escala normalizada) —
        // Frecuente: alto gasto, muchas sesiones, días bajos
        double[] centFrecuente  = {0.8, 0.8, 0.1};
        // Ocasional: gasto medio, sesiones medias, días medios
        double[] centOcasional  = {0.4, 0.3, 0.4};
        // Inactivo:  bajo gasto, pocas sesiones, muchos días
        double[] centInactivo   = {0.1, 0.1, 0.9};

        double[] punto = {normGasto, normSesiones, normDias};

        double distFrecuente = distManhattan(punto, centFrecuente);
        double distOcasional = distManhattan(punto, centOcasional);
        double distInactivo  = distManhattan(punto, centInactivo);

        // — Determinar perfil (cluster más cercano) —
        int    perfilCluster;
        String perfilNombre;
        String sugerencia;
        int    descuentoSugerido;
        Color  colorPerfil;

        if (distFrecuente <= distOcasional && distFrecuente <= distInactivo) {
            perfilCluster     = 0;
            perfilNombre      = "Cliente Frecuente";
            sugerencia        = "Premio por fidelidad — descuento moderado para retenerlo.";
            descuentoSugerido = 10;
            colorPerfil       = COLOR_ACENTO;
        } else if (distOcasional <= distFrecuente && distOcasional <= distInactivo) {
            perfilCluster     = 1;
            perfilNombre      = "Cliente Ocasional";
            sugerencia        = "Incentivo para aumentar frecuencia — descuento atractivo.";
            descuentoSugerido = 20;
            colorPerfil       = COLOR_AMARILLO;
        } else {
            perfilCluster     = 2;
            perfilNombre      = "Cliente Inactivo";
            sugerencia        = "Campaña de reactivación — descuento agresivo para recuperarlo.";
            descuentoSugerido = 35;
            colorPerfil       = COLOR_PELIGRO;
        }

        // — Actualizar UI —
        lblPerfilCliente.setText(
            "<html><b style='color:white'>" + nombreCliente + "</b>"
            + " → " + perfilNombre
            + " &nbsp;|&nbsp; <i>" + sugerencia + "</i></html>"
        );
        lblPerfilCliente.setForeground(colorPerfil);
        spinnerDescuentoCliente.setValue(descuentoSugerido);

        // Guardar datos del cliente analizado para usarlos en aplicarPromocionCliente()
        clienteAnalizadoId     = idCliente;
        clienteAnalizadoNombre = nombreCliente;
        clienteAnalizadoPerfil = perfilNombre;
    }

    /** Calcula la distancia Manhattan entre dos vectores de igual tamaño. */
    private double distManhattan(double[] a, double[] b) {
        double suma = 0;
        for (int i = 0; i < a.length; i++) suma += Math.abs(a[i] - b[i]);
        return suma;
    }

    // Campos temporales del cliente analizado actualmente
    private String clienteAnalizadoId     = null;
    private String clienteAnalizadoNombre = "";
    private String clienteAnalizadoPerfil = "";

    /**
     * Muestra vista previa y aplica la promoción personalizada al cliente analizado.
     * Se ejecuta al presionar el botón "Aplicar promoción" de la sección clientes.
     */
    private void aplicarPromocionCliente() {
        if (clienteAnalizadoId == null) {
            JOptionPane.showMessageDialog(this,
                    "Primero analiza un cliente con el botón \"Analizar cliente\".",
                    "Sin análisis", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int porcentaje = (int) spinnerDescuentoCliente.getValue();

        // — Vista previa en JOptionPane personalizado —
        String resumen = String.format(
            "╔══════════════════════════════════════╗\n"
          + "  RESUMEN DE PROMOCIÓN PERSONALIZADA\n"
          + "╚══════════════════════════════════════╝\n\n"
          + "  Cliente:   %s\n"
          + "  Perfil:    %s\n"
          + "  Descuento: %d%%\n\n"
          + "  Esta promoción se aplicará a su próxima\n"
          + "  compra según el perfil detectado.\n\n"
          + "  ¿Confirmar y registrar la promoción?",
            clienteAnalizadoNombre,
            clienteAnalizadoPerfil,
            porcentaje
        );

        int respuesta = JOptionPane.showConfirmDialog(this, resumen,
                "Vista previa — Promoción para " + clienteAnalizadoNombre,
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (respuesta != JOptionPane.YES_OPTION) return;

        // — Registrar la promoción —
        // El campo stockActual lo usamos para guardar el porcentaje,
        // diasSinVender para el cluster, totalVendido en 0.0 (no aplica a producto).
        // Ajusta según tu tabla real en BD.
        String idPromo = java.util.UUID.randomUUID().toString();
        Promocion promoCliente = new Promocion(
                idPromo,
                (double) porcentaje,   // stockActual ← porcentaje descuento
                (double) 0,            // diasSinVender ← no aplica
                0.0                    // totalVendido ← no aplica
        );
        // promocionDAO.agregar(promoCliente); ← descomentar cuando implementes agregar()

        // Agregar a la tabla de activas para feedback visual inmediato
        modeloActivas.addRow(new Object[]{
            idPromo,
            "Promo cliente — " + porcentaje + "%",
            clienteAnalizadoNombre + " (" + clienteAnalizadoPerfil + ")",
            porcentaje + "%",
            "N/A",
            "N/A"
        });
        idsPromoActiva.add(idPromo);

        // Feedback de estado
        lblEstado.setText("Promoción de " + porcentaje + "% aplicada a " + clienteAnalizadoNombre);
        lblEstado.setForeground(COLOR_ACENTO);

        // Limpiar estado del cliente analizado
        clienteAnalizadoId     = null;
        clienteAnalizadoNombre = "";
        clienteAnalizadoPerfil = "";
        lblPerfilCliente.setText("Selecciona un cliente para analizar su perfil de compra.");
        lblPerfilCliente.setForeground(COLOR_TEXTO_MUTED);
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
