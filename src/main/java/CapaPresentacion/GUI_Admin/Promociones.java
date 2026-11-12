package CapaPresentacion.GUI_Admin;

import CapaDatos.Logica_Conexion.Conexion;
import CapaLogicaNegocio.Controlador.PromocionControlador;
import CapaLogicaNegocio.Controlador.RespuestaControlador;
import CapaLogicaNegocio.DTOS.AnalisisCliente;
import CapaLogicaNegocio.DTOS.PromocionesDTO;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/** Administración de descuentos con consultas fuera del hilo de la interfaz. */
public class Promociones extends JPanel {
    private static final Color FONDO = new Color(0x1A1E29);
    private static final Color PANEL = new Color(0x132D46);
    private static final Color TABLA = new Color(0x10141E);
    private static final Color ACENTO = new Color(0x01805F);
    private static final Color TEXTO = new Color(0xE8EEF5);
    private static final Color SECUNDARIO = new Color(0xA6B8CA);
    private static final String FECHA = "dd/MM/yyyy HH:mm";
    private final PromocionControlador controlador;
    private final JTabbedPane pestanas = new JTabbedPane();
    private final JLabel estado = etiqueta("Preparando promociones…", 13, SECUNDARIO);
    private final JProgressBar progreso = new JProgressBar();
    private final List<JButton> acciones = new ArrayList<>();
    private final List<JComponent> entradas = new ArrayList<>();
    private final DefaultTableModel modeloProductos = modelo("Producto", "Marca", "Stock", "Precio (COP)", "Días sin venta", "Unidades vendidas");
    private final DefaultTableModel modeloClientes = modelo("Nombre", "Apellido", "Cédula", "Compras (COP)", "Días sin compra");
    private final DefaultTableModel modeloPromociones = modelo("Promoción", "Destino", "Estado", "Descuento (%)", "Precio (COP)", "Con descuento (COP)", "Inicio", "Fin");
    private final JTable tablaProductos = tabla(modeloProductos, new int[]{210,130,70,135,115,140});
    private final JTable tablaClientes = tabla(modeloClientes, new int[]{145,145,140,150,145});
    private final JTable tablaPromociones = tabla(modeloPromociones, new int[]{190,250,110,120,140,160,160,160});
    private final JTextField nombre = new JTextField();
    private final JSpinner descuento = new JSpinner(new SpinnerNumberModel(10, 1, 90, 1));
    private final JSpinner inicio = fecha(new Date());
    private final JSpinner fin = fecha(new Date(System.currentTimeMillis() + 30L * 86400000));
    private final JSpinner descuentoCliente = new JSpinner(new SpinnerNumberModel(5, 0, 90, 1));
    private final JSpinner inicioCliente = fecha(new Date());
    private final JSpinner finCliente = fecha(new Date(System.currentTimeMillis() + 30L * 86400000));
    private final JLabel perfil = etiqueta("Selecciona un cliente para analizar su historial.", 14, SECUNDARIO);
    private final JLabel seleccion = etiqueta("Selecciona uno o más productos de la tabla.", 13, SECUNDARIO);
    private final JLabel cantidadProductos = etiqueta("Calculando sugerencias…", 14, SECUNDARIO);
    private final JLabel cantidadClientes = etiqueta("Cargando clientes…", 14, SECUNDARIO);
    private final JLabel cantidadPromociones = etiqueta("Cargando promociones…", 14, SECUNDARIO);
    private List<PromocionesDTO.PromocionAplicadaDTO> productos = List.of();
    private List<PromocionesDTO.PromocioPersonalizadaCliente> clientes = List.of();
    private List<PromocionControlador.PromocionVista> promociones = List.of();
    private String clienteAnalizado;
    private boolean ocupado;

    public Promociones() { this(new PromocionControlador(), true); }

    Promociones(PromocionControlador controlador, boolean cargar) {
        this.controlador = controlador;
        setLayout(new BorderLayout(0, 16));
        setBackground(FONDO);
        setBorder(new EmptyBorder(20, 20, 16, 20));
        setMinimumSize(new Dimension(0, 0));
        JPanel cabecera = panel(new BorderLayout(0, 5), FONDO);
        cabecera.add(etiqueta("Promociones", 26, TEXTO), BorderLayout.NORTH);
        cabecera.add(etiqueta("Descuentos por producto y cliente · importes en pesos colombianos", 13, SECUNDARIO), BorderLayout.CENTER);
        add(cabecera, BorderLayout.NORTH);
        pestanas.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pestanas.addTab("Productos", productos());
        pestanas.addTab("Clientes", clientes());
        pestanas.addTab("Vigentes y programadas", vigentes());
        add(pestanas, BorderLayout.CENTER);
        JPanel pie = panel(new BorderLayout(12, 0), FONDO);
        progreso.setPreferredSize(new Dimension(100, 6));
        progreso.setVisible(false);
        pie.add(estado, BorderLayout.CENTER);
        pie.add(progreso, BorderLayout.EAST);
        add(pie, BorderLayout.SOUTH);
        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) seleccion.setText(tablaProductos.getSelectedRowCount()
                    + " producto(s) seleccionado(s). El precio base se conserva al aplicar el descuento.");
        });
        tablaClientes.getSelectionModel().addListSelectionListener(e -> {
            clienteAnalizado = null;
            perfil.setText("Analiza el cliente seleccionado antes de aplicar un descuento.");
        });
        if (cargar) {
            SwingUtilities.invokeLater(this::actualizar);
            addComponentListener(new ComponentAdapter() {
                @Override public void componentShown(ComponentEvent e) { if (!ocupado) actualizar(); }
            });
        } else estado.setText("Selecciona una sección para administrar descuentos.");
    }

    private JPanel productos() {
        JPanel contenido = panel(new BorderLayout(0, 12), FONDO);
        contenido.add(cabecera(cantidadProductos, boton("Actualizar sugerencias", this::actualizar)), BorderLayout.NORTH);
        contenido.add(scroll(tablaProductos), BorderLayout.CENTER);
        JPanel formulario = formulario("Promoción para los productos seleccionados");
        JPanel campos = campos(campo("Nombre (máximo 50 caracteres)", nombre), campo("Descuento (%)", descuento),
                campo("Inicio", inicio), campo("Fin", fin));
        formulario.add(campos, BorderLayout.CENTER);
        JPanel pie = panel(new BorderLayout(0, 8), PANEL);
        pie.add(seleccion, BorderLayout.NORTH);
        pie.add(fila(boton("Aplicar a seleccionados", this::aplicarProductos)), BorderLayout.SOUTH);
        formulario.add(pie, BorderLayout.SOUTH);
        contenido.add(formulario, BorderLayout.SOUTH);
        return contenido;
    }

    private JPanel clientes() {
        JPanel contenido = panel(new BorderLayout(0, 12), FONDO);
        contenido.add(cabecera(cantidadClientes, boton("Actualizar historial", this::actualizar)), BorderLayout.NORTH);
        contenido.add(scroll(tablaClientes), BorderLayout.CENTER);
        JPanel formulario = formulario("Descuento personalizado");
        JPanel campos = campos(campo("Descuento sugerido (%) · editable", descuentoCliente), campo("Inicio", inicioCliente),
                campo("Fin", finCliente));
        formulario.add(campos, BorderLayout.CENTER);
        JPanel pie = panel(new BorderLayout(0, 8), PANEL);
        pie.add(perfil, BorderLayout.NORTH);
        pie.add(fila(boton("Analizar cliente", this::analizar), boton("Aplicar al cliente", this::aplicarCliente)), BorderLayout.SOUTH);
        formulario.add(pie, BorderLayout.SOUTH);
        contenido.add(formulario, BorderLayout.SOUTH);
        return contenido;
    }

    private JPanel vigentes() {
        JPanel contenido = panel(new BorderLayout(0, 12), FONDO);
        contenido.add(cabecera(cantidadPromociones, boton("Actualizar promociones", this::actualizar)), BorderLayout.NORTH);
        contenido.add(scroll(tablaPromociones), BorderLayout.CENTER);
        JPanel pie = panel(new BorderLayout(0, 8), FONDO);
        pie.add(etiqueta("Las promociones finalizadas no aparecen. Desactivar también funciona sin internet.", 13, SECUNDARIO), BorderLayout.NORTH);
        pie.add(fila(boton("Desactivar seleccionada", this::desactivar)), BorderLayout.SOUTH);
        contenido.add(pie, BorderLayout.SOUTH);
        return contenido;
    }

    private record Datos(List<PromocionesDTO.PromocionAplicadaDTO> productos,
                         List<PromocionesDTO.PromocioPersonalizadaCliente> clientes,
                         List<PromocionControlador.PromocionVista> promociones) { }

    private Datos consultar() {
        var sugerencias = controlador.obtenerProductosPromocion();
        var historial = controlador.obtenerResumeComprasClientes();
        if (!sugerencias.exito() || !historial.exito()) throw new IllegalStateException("No se pudieron cargar los datos de promociones");
        return new Datos(sugerencias.dato(), historial.dato(), controlador.listarParaAdministrar());
    }

    private void actualizar() { tarea("Actualizando inventario, historial y promociones…", this::consultar, this::mostrar); }

    private void mostrar(Datos datos) {
        productos = List.copyOf(datos.productos());
        clientes = List.copyOf(datos.clientes());
        promociones = List.copyOf(datos.promociones());
        modeloProductos.setRowCount(0);
        for (var p : productos) modeloProductos.addRow(new Object[]{p.nombre(), p.marca(), Long.parseLong(p.Stock()),
                Double.parseDouble(p.PrecioActual()), Long.parseLong(p.diasSinVenta()), Double.parseDouble(p.totalVendido())});
        modeloClientes.setRowCount(0);
        for (var c : clientes) modeloClientes.addRow(new Object[]{c.nombre(), c.apellido(), c.cedula(),
                Double.parseDouble(c.totalCompras()), c.ultCompra() == null ? "Sin compras" : c.ultCompra()});
        modeloPromociones.setRowCount(0);
        var formato = new SimpleDateFormat(FECHA);
        Date ahora = new Date();
        for (var vista : promociones) {
            var p = vista.promocion();
            Double precio = vista.precioOriginal();
            modeloPromociones.addRow(new Object[]{p.getNombre(), vista.destino(), p.getFechaInicio().after(ahora) ? "Programada" : "Vigente",
                    p.getDescuento(), precio, precio == null ? null : precio * (1 - p.getDescuento() / 100),
                    formato.format(p.getFechaInicio()), formato.format(p.getFechaFin())});
        }
        cantidadProductos.setText(productos.isEmpty() ? "No hay productos candidatos con existencias." : productos.size() + " productos sugeridos según inventario y ventas");
        cantidadClientes.setText(clientes.size() + " clientes · selecciona uno para revisar sus compras");
        cantidadPromociones.setText(promociones.isEmpty() ? "No hay promociones vigentes ni programadas." : promociones.size() + " promociones vigentes o programadas");
        clienteAnalizado = null;
        estado.setText("Datos actualizados. Los cambios se guardan localmente y se sincronizan con Firebase.");
    }

    private PromocionesDTO.PromocionessDTO dto(String titulo, JSpinner porcentaje, JSpinner desde, JSpinner hasta, String tipo) throws Exception {
        porcentaje.commitEdit(); desde.commitEdit(); hasta.commitEdit();
        SimpleDateFormat formato = new SimpleDateFormat(FECHA);
        return new PromocionesDTO.PromocionessDTO(UUID.randomUUID().toString(), titulo,
                porcentaje.getValue().toString(), formato.format(desde.getValue()), formato.format(hasta.getValue()), tipo);
    }

    private void aplicarProductos() {
        int[] seleccionadas = tablaProductos.getSelectedRows();
        if (seleccionadas.length == 0) { aviso("Selecciona al menos un producto."); return; }
        try {
            var base = dto(nombre.getText().trim(), descuento, inicio, fin, "ESPECIFICA");
            List<String> ids = new ArrayList<>();
            for (int fila : seleccionadas) ids.add(productos.get(tablaProductos.convertRowIndexToModel(fila)).id());
            tarea("Guardando promociones…", () -> {
                int guardadas = 0;
                List<String> errores = new ArrayList<>();
                for (String id : ids) {
                    var individual = new PromocionesDTO.PromocionessDTO(UUID.randomUUID().toString(), base.nombre(), base.descuento(), base.fechaInicio(), base.fechaFin(), base.tipo());
                    var respuesta = controlador.aplicarPromocionProducto(id, individual);
                    if (respuesta.exito()) guardadas++; else errores.add(respuesta.mensaje());
                }
                return new Resultado(consultar(), guardadas + " de " + ids.size() + " promociones guardadas."
                        + (errores.isEmpty() ? "" : " " + errores.get(0)));
            }, resultado -> { mostrar(resultado.datos()); estado.setText(resultado.mensaje()); });
        } catch (Exception e) { aviso("Revisa el descuento y las fechas antes de guardar."); }
    }

    private record Resultado(Datos datos, String mensaje) { }

    private void analizar() {
        int fila = tablaClientes.getSelectedRow();
        if (fila < 0) { aviso("Selecciona un cliente."); return; }
        var cliente = clientes.get(tablaClientes.convertRowIndexToModel(fila));
        tarea("Analizando historial de compra…", () -> controlador.analizarCliente(cliente.id()), respuesta -> {
            if (!respuesta.exito()) { estado.setText(respuesta.mensaje()); return; }
            AnalisisCliente dato = respuesta.dato();
            clienteAnalizado = cliente.id();
            descuentoCliente.setValue(Integer.parseInt(dato.getDescuentoRecomendado()));
            perfil.setText(cliente.nombre() + " " + cliente.apellido() + " · " + dato.getEtiquetaNegocio()
                    + " · " + dato.getCantCompras().intValue() + " compra(s)");
            estado.setText("Perfil calculado. Puedes ajustar el descuento antes de aplicarlo.");
        });
    }

    private void aplicarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila < 0 || clienteAnalizado == null) { aviso("Selecciona y analiza un cliente antes de aplicar el descuento."); return; }
        var cliente = clientes.get(tablaClientes.convertRowIndexToModel(fila));
        if (!cliente.id().equals(clienteAnalizado)) { aviso("Vuelve a analizar el cliente seleccionado."); return; }
        try {
            var datos = dto("Promoción para " + cliente.nombre(), descuentoCliente, inicioCliente, finCliente, "GENERAL");
            tarea("Guardando promoción del cliente…", () -> {
                var respuesta = controlador.aplicarPromocionCliente(cliente.id(), datos);
                return new Resultado(consultar(), respuesta.mensaje());
            }, resultado -> { mostrar(resultado.datos()); estado.setText(resultado.mensaje()); });
        } catch (Exception e) { aviso("Revisa el descuento y las fechas antes de guardar."); }
    }

    private void desactivar() {
        int fila = tablaPromociones.getSelectedRow();
        if (fila < 0) { aviso("Selecciona una promoción para desactivarla."); return; }
        var elegida = promociones.get(tablaPromociones.convertRowIndexToModel(fila)).promocion();
        tarea("Desactivando promoción…", () -> {
            var respuesta = controlador.desactivarPromocion(new Date(), elegida.getId());
            return new Resultado(consultar(), respuesta.mensaje());
        }, resultado -> { mostrar(resultado.datos()); estado.setText(resultado.mensaje()); });
    }

    private <T> void tarea(String mensaje, Callable<T> trabajo, Consumer<T> terminado) {
        if (ocupado) return;
        ocupado = true;
        acciones.forEach(b -> b.setEnabled(false));
        entradas.forEach(c -> c.setEnabled(false));
        tablaProductos.setEnabled(false); tablaClientes.setEnabled(false); tablaPromociones.setEnabled(false);
        estado.setText(mensaje);
        progreso.setVisible(true); progreso.setIndeterminate(true);
        new SwingWorker<T, Void>() {
            @Override protected T doInBackground() throws Exception {
                try { return trabajo.call(); } finally { Conexion.cerrarConexionLocal(); }
            }
            @Override protected void done() {
                try { terminado.accept(get()); }
                catch (Exception e) {
                    Throwable causa = e.getCause() == null ? e : e.getCause();
                    estado.setText("No se completó la operación: " + causa.getMessage());
                    estado.setToolTipText(estado.getText());
                } finally {
                    ocupado = false;
                    acciones.forEach(b -> b.setEnabled(true)); entradas.forEach(c -> c.setEnabled(true));
                    tablaProductos.setEnabled(true); tablaClientes.setEnabled(true); tablaPromociones.setEnabled(true);
                    progreso.setIndeterminate(false); progreso.setVisible(false);
                }
            }
        }.execute();
    }

    private void aviso(String mensaje) { estado.setText(mensaje); }
    private static JPanel panel(LayoutManager layout, Color fondo) { JPanel p = new JPanel(layout); p.setBackground(fondo); return p; }
    private static JLabel etiqueta(String texto, int tamano, Color color) {
        JLabel l = new JLabel(texto); l.setFont(new Font("Segoe UI", tamano >= 20 ? Font.BOLD : Font.PLAIN, tamano)); l.setForeground(color); return l;
    }
    private JPanel cabecera(JLabel titulo, JButton accion) {
        JPanel p = panel(new BorderLayout(10, 0), FONDO); p.add(titulo, BorderLayout.CENTER); p.add(accion, BorderLayout.EAST); return p;
    }
    private static JPanel fila(JComponent... componentes) {
        JPanel p = panel(new FlowLayout(FlowLayout.RIGHT, 8, 0), PANEL); for (var c : componentes) p.add(c); return p;
    }
    private static JPanel formulario(String titulo) {
        JPanel p = panel(new BorderLayout(0, 12), PANEL); p.setBorder(new EmptyBorder(14, 16, 14, 16));
        p.add(etiqueta(titulo, 16, TEXTO), BorderLayout.NORTH); return p;
    }
    private static JPanel campos(JPanel... campos) {
        JPanel p = panel(new GridLayout(0, 2, 16, 10), PANEL); for (JPanel c : campos) p.add(c); return p;
    }
    private JPanel campo(String titulo, JComponent entrada) {
        entrada.setFont(new Font("Segoe UI", Font.PLAIN, 14)); entrada.setPreferredSize(new Dimension(100, 32));
        entrada.setMinimumSize(new Dimension(50, 30)); entradas.add(entrada);
        if (entrada instanceof JTextField texto) { texto.setBackground(TABLA); texto.setForeground(TEXTO); texto.setCaretColor(TEXTO); }
        JPanel p = panel(new BorderLayout(0, 5), PANEL); p.add(etiqueta(titulo, 13, SECUNDARIO), BorderLayout.NORTH); p.add(entrada, BorderLayout.CENTER); return p;
    }
    private JButton boton(String texto, Runnable accion) {
        JButton b = new JButton(texto); b.setFont(new Font("Segoe UI", Font.BOLD, 13)); b.setBackground(ACENTO); b.setForeground(Color.WHITE);
        b.setFocusPainted(true); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); b.addActionListener(e -> accion.run()); acciones.add(b); return b;
    }
    private static JSpinner fecha(Date valor) {
        JSpinner s = new JSpinner(new SpinnerDateModel(valor, null, null, java.util.Calendar.MINUTE)); s.setEditor(new JSpinner.DateEditor(s, FECHA)); return s;
    }
    private static DefaultTableModel modelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
    }
    private static JTable tabla(DefaultTableModel modelo, int[] anchos) {
        JTable t = new JTable(modelo); t.setBackground(TABLA); t.setForeground(TEXTO); t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setRowHeight(32); t.setGridColor(new Color(0x2A3045)); t.setSelectionBackground(new Color(0x22645C)); t.setSelectionForeground(Color.WHITE);
        t.setFillsViewportHeight(true); t.setAutoCreateRowSorter(true); t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        if (anchos.length == 6) t.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13)); t.getTableHeader().setPreferredSize(new Dimension(0, 36));
        for (int i = 0; i < anchos.length; i++) { t.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]); t.getColumnModel().getColumn(i).setMinWidth(65); }
        DefaultTableCellRenderer render = new DefaultTableCellRenderer() {
            @Override protected void setValue(Object valor) {
                if (valor instanceof Double importe) setText(NumberFormat.getNumberInstance(Locale.forLanguageTag("es-CO")).format(importe));
                else super.setValue(valor == null ? "—" : valor);
            }
        };
        render.setBorder(new EmptyBorder(0, 8, 0, 8)); t.setDefaultRenderer(Object.class, render);
        return t;
    }
    private static JScrollPane scroll(JTable tabla) {
        JScrollPane sp = new JScrollPane(tabla); sp.getViewport().setBackground(TABLA); sp.setMinimumSize(new Dimension(0, 100));
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); sp.getVerticalScrollBar().setUnitIncrement(24); return sp;
    }
}
