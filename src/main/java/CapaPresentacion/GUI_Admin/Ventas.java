package CapaPresentacion.GUI_Admin;

import CapaLogicaNegocio.Controlador.RespuestaControlador;
import CapaLogicaNegocio.Controlador.VentaControlador;
import CapaLogicaNegocio.DTOS.VentasDTO.VentaPorCategoriaDTO;
import CapaLogicaNegocio.Logica_Negocio.Venta;

import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
//import org.jfree.chart.JFreeChart;

/**
 * JPanel de Ventas para el panel de administración.
 * 
 * @author Marlon Vargas
 */
public class Ventas extends javax.swing.JPanel {

    private static final Color COLOR_BG = new Color(0x1A, 0x1E, 0x29);
    private static final Color COLOR_PANEL_SEC = new Color(0x13, 0x2D, 0x46);
    private static final Color COLOR_ACENTO = new Color(1, 128, 95);
    private static final Color COLOR_TEXTO = Color.WHITE;
    private static final Color COLOR_TEXTO_MUTED = new Color(0x8A, 0xA5, 0xBE);
    private static final Color COLOR_HOVER = new Color(0x1A, 0x3D, 0x58);
    private static final Color COLOR_TABLE_BG = new Color(0x10, 0x14, 0x1E);
    private static final Color COLOR_ROW_SEL = new Color(0x01, 0xC3, 0x8E, 80);
    private static final Color COLOR_CARD_1 = new Color(0x01, 0xC3, 0x8E); // verde
    private static final Color COLOR_CARD_2 = new Color(0x3B, 0x82, 0xF6); // azul
    private static final Color COLOR_CARD_3 = new Color(0xF5, 0x9E, 0x0B); // amarillo

    private final VentaControlador ventaControlador = new VentaControlador();

    private DefaultTableModel modeloTablaVentas;
    private JTable tablaVentas;

    private DefaultTableModel modeloTablaCliente;
    private JTable tablaCliente;

    private JTextField txtBuscarCliente;
    private JLabel lblTotalSesion;
    private JLabel lblTotalCliente;
    private JLabel lblTotalIngresos;
    private JPanel panelCategoria;

    private static final NumberFormat FMT_MONEDA = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
    private static final SimpleDateFormat FMT_FECHA = new SimpleDateFormat("dd/MM/yyyy");

    // ──────────────────────────────────────────────────────────────
    // Constructor
    // ──────────────────────────────────────────────────────────────
    public Ventas() {
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // Título del panel
        JLabel titulo = new JLabel("Dashboard Ventas");
        titulo.setFont(new Font("Segoe UI Light", Font.BOLD, 26));
        titulo.setForeground(COLOR_TEXTO);
        titulo.setBorder(new EmptyBorder(0, 0, 16, 0));
        add(titulo, BorderLayout.NORTH);

        // Scroll sobre todo el contenido central
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(COLOR_BG);

        // ── Fila de tarjetas KPI ──
        contenido.add(construirFilaTarjetas());
        contenido.add(Box.createVerticalStrut(20));

        // ── Tabla general de ventas ──
        contenido.add(construirSeccionTablaGeneral());
        contenido.add(Box.createVerticalStrut(20));

        // ── Sección búsqueda por cliente ──
        contenido.add(construirSeccionCliente());
        contenido.add(Box.createVerticalStrut(20));

        // ── Sección ventas por categoría ──
        contenido.add(construirSeccionCategoria());
        contenido.add(Box.createVerticalStrut(20));

        // contenido.add(Box.createVerticalStrut(20));
        // contenido.add(construirSeccionGraficas());
        // poblarDatasets();
        // iniciarTimerGraficas();

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(COLOR_BG);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);

        // Cargar datos al iniciar
        cargarTablaGeneral();
        actualizarKPIs();
    }

    // ══════════════════════════════════════════════════════════════
    // SECCIÓN 1 – TARJETAS KPI
    // ══════════════════════════════════════════════════════════════

    private JPanel construirFilaTarjetas() {
        JPanel fila = new JPanel(new GridLayout(1, 3, 16, 0));
        fila.setBackground(COLOR_BG);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        // Tarjeta 1: Total ingresos
        lblTotalIngresos = new JLabel("$0");
        fila.add(crearTarjeta("Total Ingresos", lblTotalIngresos, COLOR_CARD_1));

        // Tarjeta 2: Número de ventas
        JLabel lblNumVentas = new JLabel("0");
        fila.add(crearTarjeta("Ventas Realizadas", lblNumVentas, COLOR_CARD_2));
        // Se rellena en actualizarKPIs()
        this.lblNumVentasRef = lblNumVentas;

        // Tarjeta 3: Clientes únicos
        JLabel lblClientes = new JLabel("0");
        fila.add(crearTarjeta("Clientes Únicos", lblClientes, COLOR_CARD_3));
        this.lblClientesRef = lblClientes;

        return fila;
    }

    // Referencias auxiliares para actualizar tarjetas desde actualizarKPIs()
    private JLabel lblNumVentasRef;
    private JLabel lblClientesRef;

    /**
     * Crea una tarjeta KPI con título, valor grande y color de borde superior.
     */
    private JPanel crearTarjeta(String titulo, JLabel lblValor, Color colorAccent) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(COLOR_PANEL_SEC);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, colorAccent),
                new EmptyBorder(12, 16, 12, 16)));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
        lblTit.setForeground(COLOR_TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI Light", Font.BOLD, 24));
        lblValor.setForeground(COLOR_TEXTO);

        card.add(lblTit, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        return card;
    }

    // ══════════════════════════════════════════════════════════════
    // SECCIÓN 2 – TABLA GENERAL DE VENTAS
    // ══════════════════════════════════════════════════════════════

    private JPanel construirSeccionTablaGeneral() {
        JPanel seccion = new JPanel(new BorderLayout(0, 10));
        seccion.setBackground(COLOR_BG);
        seccion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));

        JLabel lbl = new JLabel("Todas las Ventas");
        lbl.setFont(new Font("Segoe UI light", Font.BOLD, 18));
        lbl.setForeground(COLOR_TEXTO);
        seccion.add(lbl, BorderLayout.NORTH);

        // Modelo tabla
        modeloTablaVentas = new DefaultTableModel(
                new String[] { "Producto", "Precio Unitario", "Fecha", "Cliente" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tablaVentas = estilizarTabla(new JTable(modeloTablaVentas));

        JScrollPane sp = new JScrollPane(tablaVentas);
        estilizarScroll(sp);
        seccion.add(sp, BorderLayout.CENTER);

        return seccion;
    }

    // ══════════════════════════════════════════════════════════════
    // SECCIÓN 3 – BÚSQUEDA POR CLIENTE
    // ══════════════════════════════════════════════════════════════

    private JPanel construirSeccionCliente() {
        JPanel seccion = new JPanel(new BorderLayout(0, 12));
        seccion.setBackground(COLOR_BG);
        seccion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        JLabel lbl = new JLabel("Buscar por Cliente");
        lbl.setFont(new Font("Segoe UI Light", Font.BOLD, 18));
        lbl.setForeground(COLOR_TEXTO);
        seccion.add(lbl, BorderLayout.NORTH);

        // ── Barra de búsqueda ──
        JPanel barraBusq = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBusq.setBackground(COLOR_BG);

        txtBuscarCliente = new JTextField(22);
        estilizarTextField(txtBuscarCliente, "ID del cliente...");

        JButton btnBuscar = crearBoton("Buscar", COLOR_ACENTO);
        btnBuscar.addActionListener(e -> buscarPorCliente());

        JButton btnLimpiar = crearBoton("Limpiar", COLOR_PANEL_SEC);
        btnLimpiar.addActionListener(e -> limpiarBusqueda());

        barraBusq.add(txtBuscarCliente);
        barraBusq.add(Box.createHorizontalStrut(10));
        barraBusq.add(btnBuscar);
        barraBusq.add(Box.createHorizontalStrut(8));
        barraBusq.add(btnLimpiar);
        seccion.add(barraBusq, BorderLayout.BEFORE_FIRST_LINE); // north slot ya usado

        // Centro: tabla resultado + totales
        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setBackground(COLOR_BG);

        modeloTablaCliente = new DefaultTableModel(
                new String[] { "Producto", "Precio Unitario", "Cantidad", "Subtotal", "Fecha Venta" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tablaCliente = estilizarTabla(new JTable(modeloTablaCliente));

        JScrollPane spCliente = new JScrollPane(tablaCliente);
        estilizarScroll(spCliente);
        centro.add(spCliente, BorderLayout.CENTER);

        // Totales de cliente
        JPanel panelTotales = new JPanel(new FlowLayout(FlowLayout.RIGHT, 24, 0));
        panelTotales.setBackground(COLOR_PANEL_SEC);
        panelTotales.setBorder(new EmptyBorder(8, 16, 8, 16));

        lblTotalSesion = new JLabel("$0");
        lblTotalSesion.setFont(new Font("Segoe UI Light", Font.BOLD, 16));
        lblTotalSesion.setForeground(COLOR_ACENTO);

        JLabel ltsAcu = new JLabel("Total acumulado:");
        ltsAcu.setForeground(COLOR_TEXTO);
        ltsAcu.setFont(new Font("Segoe UI Light", Font.PLAIN, 13));

        lblTotalCliente = new JLabel("$0");
        lblTotalCliente.setFont(new Font("Segoe UI Light", Font.BOLD, 16));
        lblTotalCliente.setForeground(COLOR_CARD_3);

        panelTotales.add(Box.createHorizontalStrut(24));
        panelTotales.add(ltsAcu);
        panelTotales.add(lblTotalCliente);

        centro.add(panelTotales, BorderLayout.SOUTH);

        // Ensamble: barra + centro dentro del seccion
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setBackground(COLOR_BG);
        wrapper.add(barraBusq, BorderLayout.NORTH);
        wrapper.add(centro, BorderLayout.CENTER);

        seccion.add(wrapper, BorderLayout.CENTER);
        return seccion;
    }

    // ══════════════════════════════════════════════════════════════
    // SECCIÓN 4 – VENTAS POR CATEGORÍA
    // ══════════════════════════════════════════════════════════════

    private JPanel construirSeccionCategoria() {
        JPanel seccion = new JPanel(new BorderLayout(0, 12));
        seccion.setBackground(COLOR_BG);
        seccion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        JLabel lbl = new JLabel("Ventas por Categoría");
        lbl.setFont(new Font("Segoe UI Light", Font.BOLD, 20));
        lbl.setForeground(COLOR_TEXTO);
        seccion.add(lbl, BorderLayout.NORTH);

        panelCategoria = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panelCategoria.setBackground(COLOR_BG);

        JScrollPane sp = new JScrollPane(panelCategoria,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(COLOR_BG);
        seccion.add(sp, BorderLayout.CENTER);

        return seccion;
    }

    // ══════════════════════════════════════════════════════════════
    // LÓGICA DE DATOS
    // ══════════════════════════════════════════════════════════════

    /**
     * Carga todas las ventas en la tabla general.
     * Hace JOIN manual: Venta → DetalleVenta → Producto → Cliente.
     */
    private void cargarTablaGeneral() {
        modeloTablaVentas.setRowCount(0);

        try {
            RespuestaControlador<ArrayList<Venta>> respuesta = ventaControlador.buscarTodos();

            if (!respuesta.exito()) {
                JOptionPane.showMessageDialog(this,
                        respuesta.mensaje(),
                        "Error al cargar ventas",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            ArrayList<Venta> ventas = respuesta.dato();

            for (Venta v : ventas) {
                modeloTablaVentas.addRow(new Object[] {
                        v.getId(),
                        FMT_MONEDA.format(v.getTotalVenta()),
                        FMT_FECHA.format(v.getFechaVenta()),
                        v.getIdCliente()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado al cargar la tabla de ventas: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Actualiza las tarjetas KPI.
     */
    private void actualizarKPIs() {
        try {
            RespuestaControlador<Double> respuestaTotal = ventaControlador.totalVentas();
            if (respuestaTotal.exito() && respuestaTotal.dato() != null) {
                lblTotalIngresos.setText(FMT_MONEDA.format(respuestaTotal.dato()));
            } else {
                lblTotalIngresos.setText("$0");
            }

            RespuestaControlador<Long> respuestaCantidad = ventaControlador.cantidadVentas();
            if (respuestaCantidad.exito() && respuestaCantidad.dato() != null) {
                lblNumVentasRef.setText(String.valueOf(respuestaCantidad.dato()));
            } else {
                lblNumVentasRef.setText("0");
            }

            RespuestaControlador<ArrayList<Venta>> respuestaVentas = ventaControlador.buscarTodos();
            if (respuestaVentas.exito() && respuestaVentas.dato() != null) {
                long clientesUnicos = respuestaVentas.dato().stream()
                        .map(Venta::getIdCliente)
                        .distinct()
                        .count();
                lblClientesRef.setText(String.valueOf(clientesUnicos));
            } else {
                lblClientesRef.setText("0");
            }

            cargarVentasPorCategoria();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar los indicadores: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Busca las ventas del cliente cuyo nombre coincide con el texto ingresado.
     * Muestra el detalle de productos, el total de la sesión más reciente
     * y el total acumulado histórico del cliente.
     */
    private void buscarPorCliente() {
        String busqueda = txtBuscarCliente.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            return;
        }

        modeloTablaCliente.setRowCount(0);
        lblTotalCliente.setText("$0");

        try {
            RespuestaControlador<ArrayList<Venta>> respuesta = ventaControlador.buscarTodos();

            if (!respuesta.exito() || respuesta.dato() == null) {
                JOptionPane.showMessageDialog(this, "No se pudieron cargar las ventas: " + respuesta.mensaje(),
                        "Error de búsqueda", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ArrayList<Venta> ventasDelCliente = new ArrayList<>();
            for (Venta v : respuesta.dato()) {
                if (v.getIdCliente().toLowerCase().contains(busqueda)) {
                    ventasDelCliente.add(v);
                }
            }

            if (ventasDelCliente.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron ventas para el cliente indicado.",
                        "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            double totalAcumulado = 0;

            for (Venta v : ventasDelCliente) {
                modeloTablaCliente.addRow(new Object[] {
                        v.getId(),
                        FMT_MONEDA.format(v.getTotalVenta()),
                        "—",
                        FMT_MONEDA.format(v.getTotalVenta()),
                        FMT_FECHA.format(v.getFechaVenta())
                });
                totalAcumulado += v.getTotalVenta();
            }

            lblTotalCliente.setText(FMT_MONEDA.format(totalAcumulado));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error inesperado en la búsqueda: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Limpia los resultados de búsqueda por cliente. */
    private void limpiarBusqueda() {
        txtBuscarCliente.setText("");
        modeloTablaCliente.setRowCount(0);
        lblTotalSesion.setText("$0");
        lblTotalCliente.setText("$0");
    }

    /**
     * Carga tarjetas con el total vendido por cada categoría de productos.
     */
    private void cargarVentasPorCategoria() {
        panelCategoria.removeAll();

        try {
            // ventaControlador.ventasPorCategoria() — devuelve lista de
            // VentaPorCategoriaDTO con nombre y total por categoría
            RespuestaControlador<ArrayList<VentaPorCategoriaDTO>> respuesta = ventaControlador.ventasPorCategoria();

            if (!respuesta.exito() || respuesta.dato() == null || respuesta.dato().isEmpty()) {
                JLabel sinDatos = new JLabel("No hay datos de categorías disponibles.");
                sinDatos.setForeground(COLOR_TEXTO_MUTED);
                panelCategoria.add(sinDatos);
                panelCategoria.revalidate();
                panelCategoria.repaint();
                return;
            }

            Color[] colores = { COLOR_CARD_1, COLOR_CARD_2, COLOR_CARD_3,
                    new Color(0xEC, 0x48, 0x99), new Color(0x8B, 0x5C, 0xF6) };
            int i = 0;

            for (VentaPorCategoriaDTO dto : respuesta.dato()) {
                Color color = colores[i % colores.length];

                JPanel card = new JPanel(new BorderLayout(0, 4));
                card.setBackground(COLOR_PANEL_SEC);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, color),
                        new EmptyBorder(12, 14, 12, 20)));
                card.setPreferredSize(new Dimension(200, 80));

                JLabel lblNom = new JLabel(dto.categoria());
                lblNom.setFont(new Font("Segoe UI Light", Font.BOLD, 14));
                lblNom.setForeground(COLOR_TEXTO);

                JLabel lblTotal = new JLabel(FMT_MONEDA.format(dto.totalVentas()));
                lblTotal.setFont(new Font("Segoe UI Light", Font.PLAIN, 13));
                lblTotal.setForeground(color);

                card.add(lblNom, BorderLayout.NORTH);
                card.add(lblTotal, BorderLayout.CENTER);
                panelCategoria.add(card);
                i++;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar las categorías: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        panelCategoria.revalidate();
        panelCategoria.repaint();
    }

    /**
     * Aplica estilo oscuro a una JTable y la retorna.
     */
    private JTable estilizarTabla(JTable tabla) {
        tabla.setBackground(COLOR_TABLE_BG);
        tabla.setForeground(COLOR_TEXTO);
        tabla.setFont(new Font("Segoe UI Light", Font.PLAIN, 14));
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
        center.setForeground(COLOR_TEXTO);
        for (int col = 0; col < tabla.getColumnCount(); col++) {
            tabla.getColumnModel().getColumn(col).setCellRenderer(center);
        }

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(COLOR_PANEL_SEC);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Segoe UI Light", Font.BOLD, 14));
        header.setReorderingAllowed(false);

        return tabla;
    }

    /** Aplica estilo al JScrollPane de una tabla. */
    private void estilizarScroll(JScrollPane sp) {
        sp.setBorder(BorderFactory.createLineBorder(new Color(0x2A, 0x30, 0x45)));
        sp.getViewport().setBackground(COLOR_TABLE_BG);
    }

    /** Aplica estilo oscuro a un JTextField con placeholder. */
    private void estilizarTextField(JTextField field, String placeholder) {
        field.setBackground(COLOR_PANEL_SEC);
        field.setForeground(COLOR_TEXTO);
        field.setCaretColor(COLOR_TEXTO);
        field.setFont(new Font("Segoe UI Light", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x2A, 0x30, 0x45)),
                new EmptyBorder(6, 10, 6, 10)));
        field.setText(placeholder);
        field.setForeground(COLOR_TEXTO_MUTED);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(COLOR_TEXTO);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isBlank()) {
                    field.setText(placeholder);
                    field.setForeground(COLOR_TEXTO_MUTED);
                }
            }
        });
    }

    /** Crea un botón estilizado. */
    private JButton crearBoton(String texto, Color colorFondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI Light", Font.BOLD, 13));
        btn.setBackground(colorFondo);
        btn.setForeground(COLOR_TEXTO);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(colorFondo.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(colorFondo);
            }
        });
        return btn;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jlb_fondo = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        add(jlb_fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -20, 1300, 900));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jlb_fondo;
    // End of variables declaration//GEN-END:variables
}
