package CapaPresentacion.GUI_Admin;
 
import CapaDatos.Logica_Conexion.ClienteDAO;
import CapaDatos.Logica_Conexion.DetalleVentaDAO;
import CapaDatos.Logica_Conexion.ProductoDAO;
import CapaDatos.Logica_Conexion.VentaDAO;
import CapaDatos.Logica_Conexion.CategoriaDAO;
import CapaLogicaNegocio.Logica_Negocio.Cliente;
import CapaLogicaNegocio.Logica_Negocio.DetalleVenta;
import CapaLogicaNegocio.Logica_Negocio.Producto;
import CapaLogicaNegocio.Logica_Negocio.Venta;
import CapaLogicaNegocio.Logica_Negocio.Categoria;
 
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
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
 * JPanel de Ventas para el panel de administración.
 * Muestra un dashboard con:
 *   - Tabla general de todas las ventas (Producto, Precio, Fecha, Cliente)
 *   - Búsqueda por cliente: detalle de sesión y total acumulado
 *   - Tarjeta de total de ingresos de todos los clientes
 *   - Tarjeta de ventas por categoría
 *
 * @author Generated for Tienda Electronica
 */
public class Ventas extends javax.swing.JPanel {
 
    // ──────────────────────────────────────────────────────────────
    // Paleta de colores (coherente con el resto del proyecto)
    // ──────────────────────────────────────────────────────────────
    private static final Color COLOR_BG          = new Color(0x1A, 0x1E, 0x29);
    private static final Color COLOR_PANEL_SEC   = new Color(0x13, 0x2D, 0x46);
    private static final Color COLOR_ACENTO      = new Color(0x01, 0xC3, 0x8E);
    private static final Color COLOR_HOVER       = new Color(0x1A, 0x3D, 0x58);
    private static final Color COLOR_TEXTO       = Color.WHITE;
    private static final Color COLOR_TEXTO_MUTED = new Color(0x8A, 0xA5, 0xBE);
    private static final Color COLOR_TABLE_BG    = new Color(0x10, 0x14, 0x1E);
    private static final Color COLOR_ROW_SEL     = new Color(0x01, 0xC3, 0x8E, 80);
    private static final Color COLOR_CARD_1      = new Color(0x01, 0xC3, 0x8E);   // verde
    private static final Color COLOR_CARD_2      = new Color(0x3B, 0x82, 0xF6);   // azul
    private static final Color COLOR_CARD_3      = new Color(0xF5, 0x9E, 0x0B);   // amarillo
 
    // ──────────────────────────────────────────────────────────────
    // DAOs
    // ──────────────────────────────────────────────────────────────
    private final VentaDAO       ventaDAO       = new VentaDAO();
    private final DetalleVentaDAO detalleDAO    = new DetalleVentaDAO();
    private final ClienteDAO     clienteDAO     = new ClienteDAO();
    private final ProductoDAO    productoDAO    = new ProductoDAO();
    private final CategoriaDAO   categoriaDAO   = new CategoriaDAO();
 
    // ──────────────────────────────────────────────────────────────
    // Componentes de la UI
    // ──────────────────────────────────────────────────────────────
    private DefaultTableModel modeloTablaVentas;
    private JTable            tablaVentas;
 
    private DefaultTableModel modeloTablaCliente;
    private JTable            tablaCliente;
 
    private JTextField        txtBuscarCliente;
    private JLabel            lblTotalSesion;
    private JLabel            lblTotalCliente;
    private JLabel            lblTotalIngresos;
    private JPanel            panelCategoria;
 
    // Formato moneda y fecha
    private static final NumberFormat FMT_MONEDA =
            NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
    private static final SimpleDateFormat FMT_FECHA =
            new SimpleDateFormat("dd/MM/yyyy");
 
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
    //  SECCIÓN 1 – TARJETAS KPI
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
                new EmptyBorder(12, 16, 12, 16)
        ));
 
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
    //  SECCIÓN 2 – TABLA GENERAL DE VENTAS
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
                new String[]{"Producto", "Precio Unitario", "Fecha", "Cliente"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
 
        tablaVentas = estilizarTabla(new JTable(modeloTablaVentas));
 
        JScrollPane sp = new JScrollPane(tablaVentas);
        estilizarScroll(sp);
        seccion.add(sp, BorderLayout.CENTER);
 
        return seccion;
    }
 
    // ══════════════════════════════════════════════════════════════
    //  SECCIÓN 3 – BÚSQUEDA POR CLIENTE
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
        estilizarTextField(txtBuscarCliente, "Nombre del cliente...");
 
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
                new String[]{"Producto", "Precio Unitario", "Cantidad", "Subtotal", "Fecha Venta"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaCliente = estilizarTabla(new JTable(modeloTablaCliente));
 
        JScrollPane spCliente = new JScrollPane(tablaCliente);
        estilizarScroll(spCliente);
        centro.add(spCliente, BorderLayout.CENTER);
 
        // Totales de cliente
        JPanel panelTotales = new JPanel(new FlowLayout(FlowLayout.RIGHT, 24, 0));
        panelTotales.setBackground(COLOR_PANEL_SEC);
        panelTotales.setBorder(new EmptyBorder(8, 16, 8, 16));
 
        JLabel ltsTes = new JLabel("Total sesión:");
        ltsTes.setForeground(COLOR_TEXTO_MUTED);
        ltsTes.setFont(new Font("Segoe UI Light", Font.PLAIN, 13));
 
        lblTotalSesion = new JLabel("$0");
        lblTotalSesion.setFont(new Font("Segoe UI Light", Font.BOLD, 14));
        lblTotalSesion.setForeground(COLOR_ACENTO);
 
        JLabel ltsAcu = new JLabel("Total acumulado:");
        ltsAcu.setForeground(COLOR_TEXTO_MUTED);
        ltsAcu.setFont(new Font("Segoe UI Light", Font.PLAIN, 13));
 
        lblTotalCliente = new JLabel("$0");
        lblTotalCliente.setFont(new Font("Segoe UI Light", Font.BOLD, 14));
        lblTotalCliente.setForeground(COLOR_CARD_3);
 
        panelTotales.add(ltsTes);
        panelTotales.add(lblTotalSesion);
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
    //  SECCIÓN 4 – VENTAS POR CATEGORÍA
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
    //  LÓGICA DE DATOS
    // ══════════════════════════════════════════════════════════════
 
    /**
     * Carga todas las ventas en la tabla general.
     * Hace JOIN manual: Venta → DetalleVenta → Producto → Cliente.
     */
    private void cargarTablaGeneral() {
        modeloTablaVentas.setRowCount(0);
 
        ArrayList<Venta>       ventas    = ventaDAO.obteners();
        ArrayList<DetalleVenta> detalles = detalleDAO.obteners();
        ArrayList<Producto>    productos = productoDAO.obteners();
        ArrayList<Cliente>     clientes  = clienteDAO.obteners();
 
        // Mapas para búsqueda rápida
        Map<String, Producto> mapProductos = new HashMap<>();
        for (Producto p : productos) mapProductos.put(p.getId(), p);
 
        Map<String, Cliente> mapClientes = new HashMap<>();
        for (Cliente c : clientes) mapClientes.put(c.getId(), c);
 
        Map<String, Venta> mapVentas = new HashMap<>();
        for (Venta v : ventas) mapVentas.put(v.getId(), v);
 
        for (DetalleVenta dv : detalles) {
            Venta    v = mapVentas.get(dv.getIdVenta());
            Producto p = mapProductos.get(dv.getIdProducto());
            if (v == null || p == null) continue;
 
            Cliente c = mapClientes.get(v.getIdCliente());
            String  nombreCliente = (c != null)
                    ? c.getNombre() + " " + c.getApellido()
                    : "Desconocido";
 
            modeloTablaVentas.addRow(new Object[]{
                p.getNombre(),
                FMT_MONEDA.format(dv.getPrecioVenta()),
                FMT_FECHA.format(v.getFechaVenta()),
                nombreCliente
            });
        }
    }
 
    /**
     * Actualiza las tarjetas KPI.
     */
    private void actualizarKPIs() {
        ArrayList<Venta>   ventas  = ventaDAO.obteners();
        ArrayList<Cliente> clients = clienteDAO.obteners();
 
        double totalIngresos = ventas.stream()
                .mapToDouble(Venta::getTotalVenta).sum();
 
        long clientesUnicos = ventas.stream()
                .map(Venta::getIdCliente).distinct().count();
 
        lblTotalIngresos.setText(FMT_MONEDA.format(totalIngresos));
        lblNumVentasRef.setText(String.valueOf(ventas.size()));
        lblClientesRef.setText(String.valueOf(clientesUnicos));
 
        // Actualizar también la sección categoría
        cargarVentasPorCategoria();
    }
 
    /**
     * Busca las ventas del cliente cuyo nombre coincide con el texto ingresado.
     * Muestra el detalle de productos, el total de la sesión más reciente
     * y el total acumulado histórico del cliente.
     */
    private void buscarPorCliente() {
        String busqueda = txtBuscarCliente.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) return;
 
        modeloTablaCliente.setRowCount(0);
        lblTotalSesion.setText("$0");
        lblTotalCliente.setText("$0");
 
        ArrayList<Cliente>     clientes  = clienteDAO.obteners();
        ArrayList<Venta>       ventas    = ventaDAO.obteners();
        ArrayList<DetalleVenta> detalles = detalleDAO.obteners();
        ArrayList<Producto>    productos = productoDAO.obteners();
 
        // Encontrar cliente por nombre o apellido
        Cliente clienteEncontrado = null;
        for (Cliente c : clientes) {
            String nombreCompleto = (c.getNombre() + " " + c.getApellido()).toLowerCase();
            if (nombreCompleto.contains(busqueda)) {
                clienteEncontrado = c;
                break;
            }
        }
 
        if (clienteEncontrado == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró ningún cliente con ese nombre.",
                    "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
 
        // Ventas del cliente
        ArrayList<Venta> ventasCliente = new ArrayList<>();
        for (Venta v : ventas) {
            if (v.getIdCliente().equals(clienteEncontrado.getId()))
                ventasCliente.add(v);
        }
 
        if (ventasCliente.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El cliente no tiene ventas registradas.",
                    "Sin ventas", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
 
        // Mapas auxiliares
        Map<String, Producto> mapProductos = new HashMap<>();
        for (Producto p : productos) mapProductos.put(p.getId(), p);
 
        Map<String, Venta> mapVentas = new HashMap<>();
        for (Venta v : ventasCliente) mapVentas.put(v.getId(), v);
 
        // Sesión más reciente = venta con fecha mayor
        Venta ventaReciente = ventasCliente.stream()
                .max((a, b) -> a.getFechaVenta().compareTo(b.getFechaVenta()))
                .orElse(null);
 
        double totalSesion    = 0;
        double totalAcumulado = 0;
 
        for (DetalleVenta dv : detalles) {
            if (!mapVentas.containsKey(dv.getIdVenta())) continue;
 
            Producto p = mapProductos.get(dv.getIdProducto());
            String nombreProd = (p != null) ? p.getNombre() : "N/A";
            Venta v = mapVentas.get(dv.getIdVenta());
 
            modeloTablaCliente.addRow(new Object[]{
                nombreProd,
                FMT_MONEDA.format(dv.getPrecioVenta()),
                dv.getCantidad(),
                FMT_MONEDA.format(dv.getSubtotal()),
                FMT_FECHA.format(v.getFechaVenta())
            });
 
            totalAcumulado += dv.getSubtotal();
 
            if (ventaReciente != null && dv.getIdVenta().equals(ventaReciente.getId()))
                totalSesion += dv.getSubtotal();
        }
 
        lblTotalSesion.setText(FMT_MONEDA.format(totalSesion));
        lblTotalCliente.setText(FMT_MONEDA.format(totalAcumulado));
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
 
        ArrayList<Categoria>    categorias = categoriaDAO.obteners();
        ArrayList<Producto>     productos  = productoDAO.obteners();
        ArrayList<DetalleVenta> detalles   = detalleDAO.obteners();
 
        // Mapa idProducto → idCategoria
        Map<String, String> prodCat = new HashMap<>();
        for (Producto p : productos) prodCat.put(p.getId(), p.getIdCategoria());
 
        // Mapa idCategoria → nombre
        Map<String, String> catNombre = new HashMap<>();
        for (Categoria c : categorias) catNombre.put(c.getId(), c.getNombre());
 
        // Suma de subtotales por categoría
        Map<String, Double> totalPorCat = new HashMap<>();
        for (DetalleVenta dv : detalles) {
            String idCat = prodCat.getOrDefault(dv.getIdProducto(), "sin_cat");
            totalPorCat.merge(idCat, dv.getSubtotal(), Double::sum);
        }
 
        Color[] colores = {COLOR_CARD_1, COLOR_CARD_2, COLOR_CARD_3,
                           new Color(0xEC, 0x48, 0x99), new Color(0x8B, 0x5C, 0xF6)};
        int i = 0;
 
        for (Map.Entry<String, Double> entry : totalPorCat.entrySet()) {
            String nombre = catNombre.getOrDefault(entry.getKey(), "Sin categoría");
            Color  color  = colores[i % colores.length];
 
            JPanel card = new JPanel(new BorderLayout(0, 4));
            card.setBackground(COLOR_PANEL_SEC);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 4, 0, 0, color),
                    new EmptyBorder(12, 14, 12, 20)
            ));
            card.setPreferredSize(new Dimension(200, 80));
 
            JLabel lblNom = new JLabel(nombre);
            lblNom.setFont(new Font("Segoe UI Light", Font.BOLD, 14));
            lblNom.setForeground(COLOR_TEXTO);
 
            JLabel lblTotal = new JLabel(FMT_MONEDA.format(entry.getValue()));
            lblTotal.setFont(new Font("Segoe UI Light", Font.PLAIN, 13));
            lblTotal.setForeground(color);
 
            card.add(lblNom,   BorderLayout.NORTH);
            card.add(lblTotal, BorderLayout.CENTER);
            panelCategoria.add(card);
            i++;
        }
 
        if (totalPorCat.isEmpty()) {
            JLabel sinDatos = new JLabel("No hay datos de categorías disponibles.");
            sinDatos.setForeground(COLOR_TEXTO_MUTED);
            panelCategoria.add(sinDatos);
        }
 
        panelCategoria.revalidate();
        panelCategoria.repaint();
    }
 
    // ══════════════════════════════════════════════════════════════
    //  HELPERS DE ESTILO
    // ══════════════════════════════════════════════════════════════
 
    /** Aplica estilo oscuro a una JTable y la retorna. */
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
 
        // Centrado de celdas
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        center.setBackground(COLOR_TABLE_BG);
        center.setForeground(COLOR_TEXTO);
        for (int col = 0; col < tabla.getColumnCount(); col++)
            tabla.getColumnModel().getColumn(col).setCellRenderer(center);
 
        // Encabezado
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
                new EmptyBorder(6, 10, 6, 10)
        ));
        field.setText(placeholder);
        field.setForeground(COLOR_TEXTO_MUTED);
 
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(COLOR_TEXTO);
                }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
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
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(colorFondo.brighter());
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(colorFondo);
            }
        });
        return btn;
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
