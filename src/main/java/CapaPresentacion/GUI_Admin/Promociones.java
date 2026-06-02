package CapaPresentacion.GUI_Admin;

import CapaLogicaNegocio.Controlador.CatalogoControlador;
import CapaLogicaNegocio.Controlador.PromocionClienteControlador;
import CapaLogicaNegocio.Controlador.PromocionControlador;
import CapaLogicaNegocio.Controlador.RespuestaControlador;
import CapaLogicaNegocio.DTOS.AnalisisCliente;
import CapaLogicaNegocio.DTOS.PromocionesDTO;
import CapaLogicaNegocio.Logica_Negocio.Producto;
import CapaLogicaNegocio.Logica_Negocio.Promocion;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * JPanel del módulo Promociones para el panel de administración. Al abrirse,
 * ejecuta el modelo K-Means sobre los productos y muestra en la tabla superior
 * los productos candidatos a promoción (cluster con mayor diasSinVender y menor
 * totalVendido). El administrador selecciona uno o varios productos, elige un
 * porcentaje de descuento y aplica la promoción. También puede ver y desactivar
 * las promociones actualmente activas en la tabla inferior.
 *
 * @author Marlon Vargas
 */
public class Promociones extends javax.swing.JPanel {

    private static final Color COLOR_BG = new Color(0x1A, 0x1E, 0x29);
    private static final Color COLOR_PANEL_SEC = new Color(0x13, 0x2D, 0x46);
    private static final Color COLOR_ACENTO = new Color(1, 128, 95);
    private static final Color COLOR_PELIGRO = new Color(0xEF, 0x44, 0x44);
    private static final Color COLOR_AMARILLO = new Color(0xF5, 0x9E, 0x0B);
    private static final Color COLOR_TEXTO = Color.WHITE;
    private static final Color COLOR_TEXTO_MUTED = new Color(0x8A, 0xA5, 0xBE);
    private static final Color COLOR_TABLE_BG = new Color(0x10, 0x14, 0x1E);
    private static final Color COLOR_ROW_SEL = new Color(0x01, 0xC3, 0x8E, 80);

    private final PromocionControlador promoControlador = new PromocionControlador();
    private final Promocion promo = new Promocion();

    private DefaultTableModel modeloSugerencias;
    private JTable tablaSugerencias;

    private DefaultTableModel modeloActivas;
    private JTable tablaActivas;

    private JSpinner spinnerDescuento;
    private JTextField txtNombrePromo;
    private JLabel lblEstado;
    // Vigencia de la promocion por producto (formato dd/MM/yyyy, compatible con el
    // DTO/controlador)
    private JSpinner spinnerInicio;
    private JSpinner spinnerFin;

    private DefaultTableModel modeloClientes;
    private JTable tablaClientes;
    private JLabel lblPerfilCliente;
    private JSpinner spinnerDescuentoCliente;
    // Vigencia de la promocion por cliente (mismo formato dd/MM/yyyy)
    private JSpinner spinnerInicioCliente;
    private JSpinner spinnerFinCliente;

    // Formato unico de fecha (con hora) para promociones; debe coincidir con el que
    // parsea PromocionControlador.
    private static final String FORMATO_FECHA = "dd/MM/yyyy HH:mm";
    private static final long TREINTA_DIAS_MS = 30L * 24 * 60 * 60 * 1000;

    // Mapa de idProducto → objeto Producto (para construir la promoción)
    private final Map<String, Producto> mapaProductos = new HashMap<>();

    // Mapa de fila en tablaActivas → id de Promocion (simulado con lista paralela)
    private final ArrayList<String> idsPromoActiva = new ArrayList<>();

    // Control de "una sola promocion por momento": un producto/cliente al que ya se
    // le aplico una
    // promocion en esta sesion no puede volver a recibir otra (pero si se elige
    // otro distinto, si).
    private final java.util.Set<String> productosConPromoSesion = new java.util.HashSet<>();
    private final java.util.Set<String> clientesConPromoSesion = new java.util.HashSet<>();

    public Promociones() {
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel titulo = new JLabel("Sugerencias de Promociones — Modelo K-Means");
        titulo.setFont(new Font("Segoe UI Light", Font.BOLD, 26));
        titulo.setForeground(COLOR_TEXTO);
        titulo.setBorder(new EmptyBorder(0, 0, 16, 0));
        add(titulo, BorderLayout.NORTH);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(COLOR_BG);

        contenido.add(construirSeccionSugerencias());
        contenido.add(Box.createVerticalStrut(24));
        contenido.add(construirPanelAccion());
        contenido.add(Box.createVerticalStrut(24));
        contenido.add(construirSeccionClientes());
        contenido.add(Box.createVerticalStrut(24));
        contenido.add(construirSeccionActivas());
        contenido.add(Box.createVerticalStrut(20));

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(COLOR_BG);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);

        cargarTablaClientes();
        cargarPromocionesActivas();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        promoControlador.desactivarVencidas();
                        return null;
                    }
                    @Override
                    protected void done() {
                        cargarTablaClientes();
                        cargarPromocionesActivas();
                    }
                }.execute();
            }
        });
    }

    // ══════════════════════════════════════════════════════════════
    // SECCIÓN 1 — TABLA DE SUGERENCIAS
    // ══════════════════════════════════════════════════════════════
    private JPanel construirSeccionSugerencias() {
        JPanel seccion = new JPanel(new BorderLayout(0, 10));
        seccion.setBackground(COLOR_BG);
        seccion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        JPanel encabezado = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
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

        encabezado.add(lbl);
        encabezado.add(badge);
        seccion.add(encabezado, BorderLayout.NORTH);

        modeloSugerencias = new DefaultTableModel(
                new String[] { "ID Producto", "Nombre", "Marca", "Stock",
                        "Precio Actual", "Días sin venta", "Total vendido" },
                0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tablaSugerencias = estilizarTabla(new JTable(modeloSugerencias));
        tablaSugerencias.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tablaSugerencias.getColumnModel().getColumn(0).setMinWidth(0);
        tablaSugerencias.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaSugerencias.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane sp = new JScrollPane(tablaSugerencias);
        estilizarScroll(sp);
        seccion.add(sp, BorderLayout.CENTER);

        return seccion;
    }

    // ══════════════════════════════════════════════════════════════
    // SECCIÓN 2 — PANEL DE ACCIÓN
    // ══════════════════════════════════════════════════════════════
    private JPanel construirPanelAccion() {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(COLOR_PANEL_SEC);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, COLOR_ACENTO),
                new EmptyBorder(16, 20, 16, 20)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));

        JLabel lbl = new JLabel("Aplicar promoción a los productos seleccionados");
        lbl.setFont(new Font("Segoe UI Light", Font.BOLD, 18));
        lbl.setForeground(COLOR_TEXTO);
        card.add(lbl, BorderLayout.NORTH);

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        controles.setBackground(COLOR_PANEL_SEC);

        JLabel lblNom = new JLabel("Nombre:");
        lblNom.setForeground(COLOR_TEXTO_MUTED);
        lblNom.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));

        txtNombrePromo = new JTextField(18);
        estilizarTextField(txtNombrePromo);

        JLabel lblDesc = new JLabel("Descuento (%):");
        lblDesc.setForeground(COLOR_TEXTO_MUTED);
        lblDesc.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));

        SpinnerNumberModel spinModel = new SpinnerNumberModel(10, 1, 90, 1);
        spinnerDescuento = new JSpinner(spinModel);
        spinnerDescuento.setPreferredSize(new Dimension(70, 32));
        estilizarSpinner(spinnerDescuento);

        JLabel lblIni = new JLabel("Inicio:");
        lblIni.setForeground(COLOR_TEXTO_MUTED);
        lblIni.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
        spinnerInicio = crearSpinnerFecha(new Date());

        JLabel lblFin = new JLabel("Fin:");
        lblFin.setForeground(COLOR_TEXTO_MUTED);
        lblFin.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
        spinnerFin = crearSpinnerFecha(new Date(System.currentTimeMillis() + TREINTA_DIAS_MS));

        JButton btnAplicar = crearBoton("Aplicar Promoción", COLOR_ACENTO);
        btnAplicar.addActionListener(e -> aplicarPromocion());

        JButton btnKMeans = crearBoton("Generar Sugerencias", COLOR_ACENTO);
        btnKMeans.addActionListener(e -> cargarSugerenciasKmeans());

        controles.add(lblNom);
        controles.add(txtNombrePromo);
        controles.add(lblDesc);
        controles.add(spinnerDescuento);
        controles.add(lblIni);
        controles.add(spinnerInicio);
        controles.add(lblFin);
        controles.add(spinnerFin);
        controles.add(btnAplicar);
        controles.add(btnKMeans);

        card.add(controles, BorderLayout.CENTER);
        return card;
    }

    // ══════════════════════════════════════════════════════════════
    // SECCIÓN 3 — PROMOCIONES ACTIVAS
    // ══════════════════════════════════════════════════════════════
    private JPanel construirSeccionActivas() {
        JPanel seccion = new JPanel(new BorderLayout(0, 10));
        seccion.setBackground(COLOR_BG);
        seccion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(COLOR_BG);
        encabezado.setPreferredSize(new Dimension(600, 50));

        JLabel lbl = new JLabel("Promociones activas");
        lbl.setFont(new Font("Segoe UI Light", Font.BOLD, 18));
        lbl.setForeground(COLOR_TEXTO);

        JButton btnDesactivar = crearBoton("Desactivar seleccionada", COLOR_PELIGRO);
        btnDesactivar.addActionListener(e -> desactivarPromocion());
        btnDesactivar.setPreferredSize(new Dimension(220, 35));

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnWrapper.setBackground(COLOR_BG);
        btnWrapper.setPreferredSize(new Dimension(240, 50));
        btnWrapper.add(btnDesactivar);

        encabezado.add(lbl, BorderLayout.WEST);
        encabezado.add(btnWrapper, BorderLayout.EAST);

        seccion.add(encabezado, BorderLayout.NORTH);

        modeloActivas = new DefaultTableModel(
                new String[] { "ID Promo", "Nombre Promoción", "Producto",
                        "Descuento (%)", "Precio Original", "Precio con Descuento" },
                0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tablaActivas = estilizarTabla(new JTable(modeloActivas));
        tablaActivas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaActivas.getColumnModel().getColumn(0).setMinWidth(0);
        tablaActivas.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaActivas.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane sp = new JScrollPane(tablaActivas);
        estilizarScroll(sp);
        seccion.add(sp, BorderLayout.CENTER);

        return seccion;
    }

    /**
     * Puebla tablaClientes con el resumen histórico de compras de cada cliente.
     * Llamar en el constructor: cargarTablaClientes();
     */
    private void cargarTablaClientes() {
        modeloClientes.setRowCount(0);

        try {
            RespuestaControlador<ArrayList<PromocionesDTO.PromocioPersonalizadaCliente>> respuesta = promoControlador
                    .obtenerResumeComprasClientes();

            if (!respuesta.exito() || respuesta.dato() == null) {
                lblPerfilCliente.setText("No se pudieron cargar los clientes: " + respuesta.mensaje());
                return;
            }

            for (PromocionesDTO.PromocioPersonalizadaCliente cliente : respuesta.dato()) {
                modeloClientes.addRow(new Object[] {
                        cliente.id(),
                        cliente.nombre(),
                        cliente.apellido(),
                        cliente.cedula(),
                        cliente.totalCompras(),
                        cliente.ultCompra()
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar la tabla de clientes: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Puebla tablaSugerencias con los productos candidatos a promoción detectados
     * por el modelo K-Means del sistema. Llamar en el constructor:
     * cargarSugerenciasKmeans();
     */
    private void cargarSugerenciasKmeans() {
        modeloSugerencias.setRowCount(0);

        try {
            RespuestaControlador<ArrayList<PromocionesDTO.PromocionAplicadaDTO>> respuesta = promoControlador
                    .obtenerProductosPromocion();

            if (!respuesta.exito() || respuesta.dato() == null) {
                JOptionPane.showMessageDialog(this, "No hay sugerencias por el momento", "Aviso",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            for (PromocionesDTO.PromocionAplicadaDTO dto : respuesta.dato()) {
                modeloSugerencias.addRow(new Object[] {
                        dto.id(),
                        dto.nombre(),
                        dto.marca(),
                        dto.Stock(),
                        dto.PrecioActual(),
                        dto.diasSinVenta(),
                        dto.totalVendido()
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar sugerencias de promoción: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Aplica una promoción de descuento a cada producto seleccionado en
     * tablaSugerencias. Por cada fila seleccionada construye un PromocionessDTO y
     * llama al controlador.
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

        Date fechaInicio = (Date) spinnerInicio.getValue();
        Date fechaFin = (Date) spinnerFin.getValue();
        if (fechaFin.before(fechaInicio)) {
            JOptionPane.showMessageDialog(this,
                    "La fecha de fin no puede ser anterior a la de inicio.",
                    "Fechas inválidas", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!fechaInicioEsValida(fechaInicio)) {
            JOptionPane.showMessageDialog(this,
                    "La fecha de inicio no puede ser anterior a la fecha actual.",
                    "Fechas inválidas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int porcentaje = (int) spinnerDescuento.getValue();
        int aplicadas = 0;

        for (int fila : filasSeleccionadas) {
            String idProducto = modeloSugerencias.getValueAt(fila, 0).toString();
            String nombre = modeloSugerencias.getValueAt(fila, 1).toString();

            // Solo una promocion por producto en este momento: si ya se le aplico una en
            // esta sesion,
            // no se vuelve a aplicar (elegir otro producto si esta permitido).
            if (productosConPromoSesion.contains(idProducto)) {
                JOptionPane.showMessageDialog(this,
                        "Ya aplicaste una promoción a \"" + nombre + "\" en este momento. "
                                + "Elige otro producto si deseas aplicar otra.",
                        "Promoción ya aplicada", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Aplicar " + porcentaje + "% de descuento a \"" + nombre + "\"?",
                    "Confirmar promoción — " + nombrePromo,
                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (confirmacion != JOptionPane.YES_OPTION) {
                continue;
            }

            try {
                PromocionesDTO.PromocionessDTO dto = new PromocionesDTO.PromocionessDTO(
                        UUID.randomUUID().toString(),
                        nombrePromo,
                        String.valueOf(porcentaje),
                        fechaDeSpinner(spinnerInicio),
                        fechaDeSpinner(spinnerFin),
                        "ESPECIFICA");

                RespuestaControlador<Boolean> respuesta = promoControlador.aplicarPromocionProducto(idProducto, dto);

                if (respuesta.exito()) {
                    aplicadas++;
                    productosConPromoSesion.add(idProducto);

                    Double precioOriginal = parsearPrecio(modeloSugerencias.getValueAt(fila, 4));
                    String textoOriginal = precioOriginal != null ? formatoPrecio(precioOriginal) : "—";
                    String textoConDescuento = precioOriginal != null
                            ? formatoPrecio(precioOriginal * (1 - porcentaje / 100.0))
                            : "—";

                    modeloActivas.addRow(new Object[] {
                            dto.id(),
                            nombrePromo,
                            nombre,
                            porcentaje + "%",
                            textoOriginal,
                            textoConDescuento
                    });
                    idsPromoActiva.add(dto.id());
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo aplicar la promoción a \"" + nombre + "\": " + respuesta.mensaje(),
                            "Error al aplicar", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error inesperado al aplicar promoción al producto \"" + nombre + "\": " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (aplicadas > 0) {
            lblEstado.setText(aplicadas + " promoción(es) aplicada(s) correctamente.");
            lblEstado.setForeground(COLOR_ACENTO);
            txtNombrePromo.setText("");
        }
    }

    /**
     * Desactiva en BD la promoción seleccionada en tablaActivas usando la fecha
     * actual.
     */
    private void desactivarPromocion() {
        int fila = tablaActivas.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una promoción de la tabla para desactivarla.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombrePromo = modeloActivas.getValueAt(fila, 1).toString();
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Desactivar la promoción \"" + nombrePromo + "\"?",
                "Confirmar desactivación", JOptionPane.YES_NO_OPTION);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            String idPromocion = idsPromoActiva.get(fila);
            RespuestaControlador<Boolean> respuesta = promoControlador.desactivarPromocion(new Date(), idPromocion);

            if (respuesta.exito()) {
                modeloActivas.removeRow(fila);
                idsPromoActiva.remove(fila);
                lblEstado.setText("Promoción \"" + nombrePromo + "\" desactivada.");
                lblEstado.setForeground(COLOR_PELIGRO);
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo desactivar la promoción: " + respuesta.mensaje(),
                        "Error al desactivar", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado al desactivar la promoción: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Puebla tablaActivas con las promociones vigentes leyendolas de la NUBE (la
     * misma fuente que ve
     * el Cliente). Por cada promocion muestra: nombre, producto, descuento, precio
     * original y precio
     * con descuento. Para resolver el nombre del producto y su precio original se
     * usa el catalogo de
     * la nube. La carga se hace en segundo plano (SwingWorker) para no congelar la
     * interfaz.
     */
    private void cargarPromocionesActivas() {
        new SwingWorker<ArrayList<Object[]>, Void>() {
            @Override
            protected ArrayList<Object[]> doInBackground() {
                ArrayList<Object[]> filas = new ArrayList<>();
                try {
                    RespuestaControlador<ArrayList<CapaLogicaNegocio.DTOS.Promociones>> resp = new PromocionClienteControlador()
                            .listarPromocionesActivas();

                    if (resp == null || !resp.exito() || resp.dato() == null)
                        return filas;

                    // Mapa idProducto -> Producto (nombre y precio original) desde el catalogo de
                    // la nube.
                    Map<String, Producto> productos = new HashMap<>();
                    RespuestaControlador<ArrayList<Producto>> catalogo = new CatalogoControlador().listarCatalogo();
                    if (catalogo != null && catalogo.exito() && catalogo.dato() != null) {
                        for (Producto p : catalogo.dato())
                            productos.put(p.getId(), p);
                    }

                    for (CapaLogicaNegocio.DTOS.Promociones promo : resp.dato()) {
                        double descuento = promo.getDescuento() != null ? promo.getDescuento() : 0.0;
                        String idProd = promo.getIdProducto();
                        String idCli = promo.getIdCliente();

                        String nombreProducto;
                        String precioOriginal;
                        String precioConDescuento;

                        if (idProd != null && !idProd.isEmpty() && productos.containsKey(idProd)) {
                            Producto p = productos.get(idProd);
                            nombreProducto = p.getNombre();
                            Double precio = p.getPrecioActual();
                            precioOriginal = precio != null ? formatoPrecio(precio) : "—";
                            precioConDescuento = precio != null
                                    ? formatoPrecio(precio * (1 - descuento / 100.0))
                                    : "—";
                        } else if (idCli != null && !idCli.isEmpty()) {
                            nombreProducto = "Cliente";
                            precioOriginal = "N/A";
                            precioConDescuento = "N/A";
                        } else {
                            nombreProducto = (idProd != null && !idProd.isEmpty()) ? idProd : "General";
                            precioOriginal = "—";
                            precioConDescuento = "—";
                        }

                        filas.add(new Object[] {
                                promo.getId(),
                                promo.getNombre() != null ? promo.getNombre() : "—",
                                nombreProducto,
                                ((int) descuento) + "%",
                                precioOriginal,
                                precioConDescuento
                        });
                    }
                } catch (Exception ex) {
                    System.out.println("Error al cargar promociones activas: " + ex.getMessage());
                }
                return filas;
            }

            @Override
            protected void done() {
                try {
                    ArrayList<Object[]> filas = get();
                    // Se limpia y repuebla de forma atomica AQUI (no antes de lanzar el worker)
                    // para que la tabla no quede vacia mientras se consulta la nube. Asi se
                    // elimina el parpadeo donde la promocion desaparecia ~300ms y reaparecia.
                    modeloActivas.setRowCount(0);
                    idsPromoActiva.clear();
                    for (Object[] fila : filas) {
                        modeloActivas.addRow(fila);
                        idsPromoActiva.add((String) fila[0]);
                    }
                } catch (Exception ignored) {
                }
            }
        }.execute();
    }

    // ══════════════════════════════════════════════════════════════
    // HELPERS DE ESTILO
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

        DefaultTableCellRenderer rendererCeldas = new DefaultTableCellRenderer();
        rendererCeldas.setHorizontalAlignment(JLabel.CENTER);
        rendererCeldas.setBackground(COLOR_TABLE_BG);
        rendererCeldas.setForeground(COLOR_TEXTO);
        for (int col = 0; col < tabla.getColumnCount(); col++) {
            tabla.getColumnModel().getColumn(col).setCellRenderer(rendererCeldas);
        }

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(COLOR_PANEL_SEC);
        header.setForeground(Color.BLACK); // título de columnas permanece negro
        header.setFont(new Font("Segoe UI Light", Font.BOLD, 15));
        header.setReorderingAllowed(false);
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);

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
                new EmptyBorder(6, 10, 6, 10)));
    }

    private void estilizarSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
        editor.getTextField().setOpaque(true);
        editor.getTextField().setBackground(Color.WHITE);
        editor.getTextField().setForeground(Color.BLACK);
        editor.getTextField().setCaretColor(Color.BLACK);
        editor.getTextField().setBorder(new EmptyBorder(4, 6, 4, 6));
    }

    /**
     * Crea un selector de fecha (JSpinner con SpinnerDateModel) que muestra y edita
     * la fecha con el
     * formato {@link #FORMATO_FECHA} (dd/MM/yyyy HH:mm), el mismo que espera el
     * controlador de
     * promociones. Incluye la hora porque una promocion puede durar solo unas
     * horas.
     * 
     * @param valorInicial fecha que muestra al inicio
     */
    private JSpinner crearSpinnerFecha(Date valorInicial) {
        JSpinner spinner = new JSpinner(
                new SpinnerDateModel(valorInicial, null, null, java.util.Calendar.DAY_OF_MONTH));
        spinner.setEditor(new JSpinner.DateEditor(spinner, FORMATO_FECHA));
        spinner.setPreferredSize(new Dimension(150, 32));
        estilizarSpinnerFecha(spinner);
        return spinner;
    }

    /**
     * Estilo especifico para los spinners de fecha: fondo claro y texto oscuro para
     * que la fecha SEA
     * VISIBLE. (El estilo oscuro generico dejaba el texto blanco sobre fondo blanco
     * y no se leia.)
     */
    private void estilizarSpinnerFecha(JSpinner spinner) {
        spinner.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
        javax.swing.JFormattedTextField campo = editor.getTextField();
        campo.setOpaque(true);
        campo.setBackground(Color.WHITE);
        campo.setForeground(Color.BLACK);
        campo.setCaretColor(Color.BLACK);
        campo.setBorder(new EmptyBorder(4, 6, 4, 6));
    }

    /**
     * Devuelve la fecha seleccionada en el spinner ya formateada como dd/MM/yyyy
     * HH:mm.
     */
    private String fechaDeSpinner(JSpinner spinner) {
        Date fecha = (Date) spinner.getValue();
        return new java.text.SimpleDateFormat(FORMATO_FECHA).format(fecha);
    }

    /**
     * Valida que la fecha de inicio no sea anterior al dia de hoy. Se compara solo
     * por dia (no por
     * hora) para no rechazar la fecha por defecto, pero la hora elegida si se
     * respeta para la vigencia.
     * 
     * @return true si la fecha de inicio es valida (hoy o futura)
     */
    private boolean fechaInicioEsValida(Date fechaInicio) {
        java.util.Calendar hoy = java.util.Calendar.getInstance();
        hoy.set(java.util.Calendar.HOUR_OF_DAY, 0);
        hoy.set(java.util.Calendar.MINUTE, 0);
        hoy.set(java.util.Calendar.SECOND, 0);
        hoy.set(java.util.Calendar.MILLISECOND, 0);
        return !fechaInicio.before(hoy.getTime());
    }

    /**
     * Convierte el valor de la celda "Precio Actual" (puede venir como Double,
     * número o texto) a Double.
     * 
     * @return el precio como Double, o null si no se pudo interpretar.
     */
    private Double parsearPrecio(Object valor) {
        if (valor == null)
            return null;
        if (valor instanceof Number)
            return ((Number) valor).doubleValue();
        try {
            return Double.valueOf(valor.toString().replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Formatea un precio para mostrarlo en la tabla (ej. $1.234). */
    private String formatoPrecio(double precio) {
        return "$" + String.format("%,.0f", precio);
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

    /**
     * Construye la sección de análisis de clientes. Llámala en el constructor de
     * Promociones, después de construirSeccionActivas():
     *
     * contenido.add(Box.createVerticalStrut(24));
     * contenido.add(construirSeccionClientes());
     */
    private JPanel construirSeccionClientes() {
        JPanel seccion = new JPanel(new BorderLayout(0, 10));
        seccion.setBackground(COLOR_BG);
        seccion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 420));

        // — Encabezado —
        JPanel encabezado = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        encabezado.setBackground(COLOR_BG);

        JLabel lbl = new JLabel("Promoción personalizada por cliente");
        lbl.setFont(new Font("Segoe UI Light", Font.BOLD, 18));
        lbl.setForeground(COLOR_TEXTO);

        JLabel badge = new JLabel("  Selecciona un cliente y analiza su comportamiento de compra  ");
        badge.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
        badge.setForeground(COLOR_BG);
        badge.setBackground(new Color(0x3B, 0x82, 0xF6));
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(3, 8, 3, 8));

        encabezado.add(lbl);
        encabezado.add(badge);
        seccion.add(encabezado, BorderLayout.NORTH);

        modeloClientes = new DefaultTableModel(
                new String[] { "ID", "Nombre", "Apellido", "Cédula",
                        "Total Compras", "Días últ. compra" },
                0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tablaClientes = estilizarTabla(new JTable(modeloClientes));
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaClientes.getColumnModel().getColumn(0).setMinWidth(0);
        tablaClientes.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaClientes.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane spClientes = new JScrollPane(tablaClientes);
        estilizarScroll(spClientes);
        spClientes.setPreferredSize(new Dimension(0, 180));
        seccion.add(spClientes, BorderLayout.CENTER);

        JPanel panelAccionCliente = new JPanel(new BorderLayout(0, 10));
        panelAccionCliente.setBackground(COLOR_PANEL_SEC);
        panelAccionCliente.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, new Color(0x3B, 0x82, 0xF6)),
                new EmptyBorder(14, 18, 14, 18)));

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

        JLabel lblIniC = new JLabel("Inicio:");
        lblIniC.setForeground(COLOR_TEXTO_MUTED);
        lblIniC.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
        spinnerInicioCliente = crearSpinnerFecha(new Date());

        JLabel lblFinC = new JLabel("Fin:");
        lblFinC.setForeground(COLOR_TEXTO_MUTED);
        lblFinC.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
        spinnerFinCliente = crearSpinnerFecha(new Date(System.currentTimeMillis() + TREINTA_DIAS_MS));

        JButton btnAnalizar = crearBoton("Analizar cliente", new Color(0x3B, 0x82, 0xF6));
        btnAnalizar.addActionListener(e -> analizarClienteSeleccionado());

        JButton btnAplicarCliente = crearBoton("Aplicar promoción", COLOR_ACENTO);
        btnAplicarCliente.addActionListener(e -> aplicarPromocionCliente());

        controlesCliente.add(lblDesc);
        controlesCliente.add(spinnerDescuentoCliente);
        controlesCliente.add(lblIniC);
        controlesCliente.add(spinnerInicioCliente);
        controlesCliente.add(lblFinC);
        controlesCliente.add(spinnerFinCliente);
        controlesCliente.add(Box.createHorizontalStrut(10));
        controlesCliente.add(btnAnalizar);
        controlesCliente.add(btnAplicarCliente);

        panelAccionCliente.add(controlesCliente, BorderLayout.CENTER);
        seccion.add(panelAccionCliente, BorderLayout.SOUTH);

        return seccion;
    }

    /**
     * Delega el análisis K-Means del cliente al controlador y actualiza el label de
     * perfil y el spinner con el descuento recomendado por el modelo.
     */
    private void analizarClienteSeleccionado() {
        int fila = tablaClientes.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un cliente de la tabla primero.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idCliente = modeloClientes.getValueAt(fila, 0).toString();
        String nombreCliente = modeloClientes.getValueAt(fila, 1) + " "
                + modeloClientes.getValueAt(fila, 2);

        try {
            RespuestaControlador<AnalisisCliente> respuesta = promoControlador.analizarCliente(idCliente);

            if (!respuesta.exito() || respuesta.dato() == null) {
                JOptionPane.showMessageDialog(this,
                        "No se pudo analizar el cliente: " + respuesta.mensaje(),
                        "Error de análisis", JOptionPane.ERROR_MESSAGE);
                return;
            }

            AnalisisCliente analisis = respuesta.dato();
            Color colorPerfil = switch (analisis.getEtiquetaNegocio()) {
                case "VIP" ->
                    COLOR_ACENTO;
                case "Regular" ->
                    COLOR_AMARILLO;
                default ->
                    COLOR_PELIGRO;
            };

            lblPerfilCliente.setText(
                    "<html><b style='color:white'>" + nombreCliente + "</b>"
                            + " → " + analisis.getEtiquetaNegocio()
                            + " &nbsp;|&nbsp; <i>Descuento sugerido: "
                            + analisis.getDescuentoRecomendado() + "%</i></html>");
            lblPerfilCliente.setForeground(colorPerfil);

            int descuentoSugerido = Integer.parseInt(analisis.getDescuentoRecomendado());
            spinnerDescuentoCliente.setValue(descuentoSugerido);

            clienteAnalizadoId = idCliente;
            clienteAnalizadoNombre = nombreCliente;
            clienteAnalizadoPerfil = analisis.getEtiquetaNegocio();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this,
                    "El descuento recomendado no tiene formato numérico válido.",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado al analizar el cliente: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String clienteAnalizadoId = null;
    private String clienteAnalizadoNombre = "";
    private String clienteAnalizadoPerfil = "";

    /**
     * Persiste en BD la promoción personalizada para el cliente que fue analizado
     * en el paso previo (analizarClienteSeleccionado).
     */
    private void aplicarPromocionCliente() {
        if (clienteAnalizadoId == null) {
            JOptionPane.showMessageDialog(this,
                    "Primero analiza un cliente con el botón \"Analizar cliente\".",
                    "Sin análisis previo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Solo una promocion por cliente en este momento: si ya se le aplico una en
        // esta sesion, no
        // se vuelve a aplicar (elegir y analizar otro cliente si esta permitido).
        if (clientesConPromoSesion.contains(clienteAnalizadoId)) {
            JOptionPane.showMessageDialog(this,
                    "Ya aplicaste una promoción a \"" + clienteAnalizadoNombre + "\" en este momento. "
                            + "Elige otro cliente si deseas aplicar otra.",
                    "Promoción ya aplicada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int porcentaje = (int) spinnerDescuentoCliente.getValue();

        Date fechaInicio = (Date) spinnerInicioCliente.getValue();
        Date fechaFin = (Date) spinnerFinCliente.getValue();
        if (fechaFin.before(fechaInicio)) {
            JOptionPane.showMessageDialog(this,
                    "La fecha de fin no puede ser anterior a la de inicio.",
                    "Fechas inválidas", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!fechaInicioEsValida(fechaInicio)) {
            JOptionPane.showMessageDialog(this,
                    "La fecha de inicio no puede ser anterior a la fecha actual.",
                    "Fechas inválidas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String resumen = String.format("""
                ╔══════════════════════════════════════╗
                  RESUMEN DE PROMOCIÓN PERSONALIZADA
                ╚══════════════════════════════════════╝

                  Cliente:   %s
                  Perfil:    %s
                  Descuento: %d%%

                  ¿Confirmar y registrar la promoción?""",
                clienteAnalizadoNombre,
                clienteAnalizadoPerfil,
                porcentaje);

        int confirmacion = JOptionPane.showConfirmDialog(this, resumen,
                "Vista previa — Promoción para " + clienteAnalizadoNombre,
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            PromocionesDTO.PromocionessDTO dto = new PromocionesDTO.PromocionessDTO(
                    UUID.randomUUID().toString(),
                    "Promo cliente — " + clienteAnalizadoPerfil,
                    String.valueOf(porcentaje),
                    fechaDeSpinner(spinnerInicioCliente),
                    fechaDeSpinner(spinnerFinCliente),
                    "GENERAL");

            RespuestaControlador<Boolean> respuesta = promoControlador.aplicarPromocionCliente(clienteAnalizadoId, dto);

            if (respuesta.exito()) {
                clientesConPromoSesion.add(clienteAnalizadoId);
                modeloActivas.addRow(new Object[] {
                        dto.id(),
                        "Promo cliente — " + porcentaje + "%",
                        clienteAnalizadoNombre + " (" + clienteAnalizadoPerfil + ")",
                        porcentaje + "%",
                        "N/A",
                        "N/A"
                });
                idsPromoActiva.add(dto.id());

                lblEstado.setText("Promoción de " + porcentaje + "% aplicada a " + clienteAnalizadoNombre);
                lblEstado.setForeground(COLOR_ACENTO);
                clienteAnalizadoId = null;
                clienteAnalizadoNombre = "";
                clienteAnalizadoPerfil = "";
                lblPerfilCliente.setText("Selecciona un cliente para analizar su perfil de compra.");
                lblPerfilCliente.setForeground(COLOR_TEXTO_MUTED);

            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo aplicar la promoción al cliente: " + respuesta.mensaje(),
                        "Error al aplicar", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado al aplicar la promoción al cliente: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
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
