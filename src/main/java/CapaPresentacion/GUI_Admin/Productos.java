package CapaPresentacion.GUI_Admin;

import CapaLogicaNegocio.Controlador.CategoriaControlador;
import CapaLogicaNegocio.Controlador.ProductoControlador;
import CapaLogicaNegocio.Controlador.RespuestaControlador;
import CapaLogicaNegocio.DTOS.ProductoDTO;
import CapaLogicaNegocio.Logica_Negocio.Categoria;
import CapaLogicaNegocio.Logica_Negocio.Producto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import CapaDatos.Logica_Conexion.ProductoDAO;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Panel principal del módulo <b>Productos</b> dentro del JFrame
 * {@code Menu_Admin}.
 *
 * @author Marlon Vargas
 */
public class Productos extends JPanel {

    private static final Color COLOR_BG = new Color(0x1A, 0x1E, 0x29);
    private static final Color COLOR_PANEL_SEC = new Color(0x13, 0x2D, 0x46);
    private static final Color COLOR_ACENTO = new Color(1, 128, 95);
    private static final Color COLOR_HOVER = new Color(0x1A, 0x3D, 0x58);
    private static final Color COLOR_TEXTO = Color.WHITE;
    private static final Color COLOR_TEXTO_MUTED = Color.WHITE;
    private static final Color COLOR_TABLE_BG = new Color(0x10, 0x14, 0x1E);
    private static final Color COLOR_ROW_SEL = new Color(0x01, 0xC3, 0x8E, 80);
    private static final Color COLOR_INPUT_BG = new Color(0x0D, 0x12, 0x1E);

    /** Ancho fijo de la sidebar en píxeles. */
    private static final int FORM_PANEL_WIDTH = 340;

    private final ProductoControlador productoControlador = new ProductoControlador();
    private final CategoriaControlador categoriaControlador = new CategoriaControlador();

    private DefaultTableModel tableModel;

    private JTable tablaProductos;

    /**
     * Panel lateral derecho que aloja el formulario CRUD.
     * Oculto por defecto; se hace visible al presionar un botón de acción.
     */
    private JPanel formPanel;
    private JPanel camposContainer;

    private JTextField txtId;
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtMarca;
    private JTextField txtSerie;
    private JTextField txtStock;
    private JTextField txtPrecio;

    private JSpinner spinnerFecha;
    private JComboBox<Categoria> cmbCategoria;

    private JLabel lblTituloForm;
    private JButton btnConfirmar;
    private String modoActual = "";

    public String s;

    public Productos() {
        s = Paths.get("").toAbsolutePath().toString();
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);

        construirToolbar();
        construirTabla();
        construirFormPanel();
        cargarProductos();
    }

    /**
     * Construye la barra superior con el título del módulo y los cuatro
     * botones de acción CRUD. Cada botón invoca
     * {@link #mostrarFormulario(String)} con el modo correspondiente.
     */
    private void construirToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        toolbar.setBackground(COLOR_PANEL_SEC);
        toolbar.setBorder(new EmptyBorder(8, 16, 8, 16));

        JLabel lblModulo = new JLabel("Gestión de Productos / Inventario");
        lblModulo.setFont(new Font("Segoe UI Light", Font.BOLD, 26));
        lblModulo.setForeground(COLOR_TEXTO);
        toolbar.add(lblModulo);

        toolbar.add(Box.createHorizontalStrut(24));

        toolbar.add(crearBotonAccion("Registrar Producto", "REGISTRAR"));
        toolbar.add(crearBotonAccion("Buscar Producto", "BUSCAR"));
        toolbar.add(crearBotonAccion("Actualizar Producto", "ACTUALIZAR"));
        toolbar.add(crearBotonAccion("Eliminar Producto", "ELIMINAR"));

        add(toolbar, BorderLayout.NORTH);
    }

    /**
     * Crea un botón de acción estilizado para la toolbar con efecto hover.
     *
     * @param etiqueta texto visible del botón
     * @param modo     modo CRUD que activa este botón
     * @return {@link JButton} completamente configurado
     */
    private JButton crearBotonAccion(String etiqueta, String modo) {
        JButton btn = new JButton(etiqueta);
        btn.setFont(new Font("Segoe UI Light", Font.PLAIN, 15));
        btn.setForeground(COLOR_TEXTO);
        btn.setBackground(COLOR_HOVER);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(COLOR_ACENTO);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(COLOR_HOVER);
            }
        });

        btn.addActionListener(e -> mostrarFormulario(modo));
        return btn;
    }

    /**
     * Construye el panel de inventario con un {@link JTable} dentro de un
     * {@link JScrollPane}. Las columnas reflejan los campos de la entidad
     * {@link Producto} tal como los devuelve {@link ProductoDAO#obteners()}.
     *
     * <p>
     * Al seleccionar una fila, sus datos se precargán automáticamente
     * en el formulario lateral mediante {@link #cargarFilaEnFormulario(int)}.
     */
    private void construirTabla() {
        String[] columnas = {
                "ID", "Código", "Nombre", "Marca", "Serie",
                "Stock", "Precio", "Vencimiento", "Categoría"
        };

        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaProductos = new JTable(tableModel);
        tablaProductos.setBackground(COLOR_TABLE_BG);
        tablaProductos.setForeground(COLOR_TEXTO); // 🔹 texto blanco en filas
        tablaProductos.setFont(new Font("Segoe UI Light", Font.PLAIN, 14));
        tablaProductos.setRowHeight(36);
        tablaProductos.setShowHorizontalLines(true);
        tablaProductos.setGridColor(new Color(0x2A, 0x2E, 0x3D));
        tablaProductos.setSelectionBackground(COLOR_ROW_SEL);
        tablaProductos.setSelectionForeground(COLOR_TEXTO);
        tablaProductos.setIntercellSpacing(new Dimension(0, 1));

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);
        cellRenderer.setForeground(COLOR_TEXTO); // texto blanco
        cellRenderer.setBackground(COLOR_TABLE_BG); // fondo oscuro
        for (int col = 0; col < tablaProductos.getColumnCount(); col++) {
            tablaProductos.getColumnModel().getColumn(col).setCellRenderer(cellRenderer);
        }

        JTableHeader header = tablaProductos.getTableHeader();
        header.setBackground(COLOR_PANEL_SEC);
        header.setForeground(Color.BLACK); // títulos en negro
        header.setFont(new Font("Segoe UI Light", Font.BOLD, 14));
        header.setReorderingAllowed(false);

        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaProductos.getSelectedRow() >= 0) {
                cargarFilaEnFormulario(tablaProductos.getSelectedRow());
            }
        });

        int[] anchos = { 70, 90, 140, 110, 100, 60, 80, 110, 160 };
        for (int i = 0; i < anchos.length; i++) {
            tablaProductos.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        JScrollPane scroll = new JScrollPane(tablaProductos);
        scroll.getViewport().setBackground(COLOR_TABLE_BG);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(COLOR_BG);
        wrapper.setBorder(new EmptyBorder(16, 16, 16, 16));
        wrapper.add(scroll, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
    }

    /**
     * Construye el panel lateral derecho con todos los controles del formulario.
     *
     * <p>
     * Se crea inicialmente <b>oculto</b> ({@code setVisible(false)}).
     */
    private void construirFormPanel() {
        formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(COLOR_PANEL_SEC);
        formPanel.setPreferredSize(new Dimension(FORM_PANEL_WIDTH, 0));
        formPanel.setVisible(false);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PANEL_SEC);
        header.setBorder(new EmptyBorder(20, 16, 8, 16));

        lblTituloForm = new JLabel("Registrar Producto");
        lblTituloForm.setFont(new Font("Segoe UI Light", Font.BOLD, 18));
        lblTituloForm.setForeground(COLOR_TEXTO);
        header.add(lblTituloForm, BorderLayout.CENTER);
        formPanel.add(header, BorderLayout.NORTH);

        camposContainer = new JPanel();
        camposContainer.setLayout(new BoxLayout(camposContainer, BoxLayout.Y_AXIS));
        camposContainer.setBackground(COLOR_PANEL_SEC);
        camposContainer.setBorder(new EmptyBorder(4, 16, 4, 16));

        txtId = crearCampoTexto();
        txtCodigo = crearCampoTexto();
        txtNombre = crearCampoTexto();
        txtMarca = crearCampoTexto();
        txtSerie = crearCampoTexto();
        txtStock = crearCampoTexto();
        txtPrecio = crearCampoTexto();

        /*
         * spinnerFecha = new JSpinner(new SpinnerDateModel());
         * JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerFecha,
         * "dd/MM/yyyy");
         * spinnerFecha.setEditor(dateEditor);
         * spinnerFecha.setBackground(COLOR_INPUT_BG);
         * spinnerFecha.setForeground(COLOR_TEXTO);
         * spinnerFecha.setFont(new Font("Segoe UI Light", Font.PLAIN, 14));
         * spinnerFecha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
         * spinnerFecha.setAlignmentX(Component.LEFT_ALIGNMENT);
         * dateEditor.getTextField().setBackground(COLOR_INPUT_BG);
         * dateEditor.getTextField().setForeground(COLOR_TEXTO);
         * dateEditor.getTextField().setCaretColor(COLOR_ACENTO);
         * dateEditor.getTextField().setBorder(BorderFactory.createLineBorder(new
         * Color(0x2A, 0x3D, 0x55), 1));
         */

        cmbCategoria = new JComboBox<>();
        cmbCategoria.setBackground(COLOR_INPUT_BG);
        cmbCategoria.setForeground(COLOR_TEXTO);
        cmbCategoria.setFont(new Font("Segoe UI Light", Font.PLAIN, 14));
        cmbCategoria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cmbCategoria.setAlignmentX(Component.LEFT_ALIGNMENT);
        cargarCategorias();

        camposContainer.add(crearFilaCampo("ID", txtId));
        camposContainer.add(Box.createVerticalStrut(8));
        camposContainer.add(crearFilaCampo("Código", txtCodigo));
        camposContainer.add(Box.createVerticalStrut(8));
        camposContainer.add(crearFilaCampo("Nombre", txtNombre));
        camposContainer.add(Box.createVerticalStrut(8));
        camposContainer.add(crearFilaCampo("Marca", txtMarca));
        camposContainer.add(Box.createVerticalStrut(8));
        camposContainer.add(crearFilaCampo("Serie", txtSerie));
        camposContainer.add(Box.createVerticalStrut(8));
        camposContainer.add(crearFilaCampo("Stock (unidades)", txtStock));
        camposContainer.add(Box.createVerticalStrut(8));
        camposContainer.add(crearFilaCampo("Precio actual ($)", txtPrecio));
        camposContainer.add(Box.createVerticalStrut(8));
        /*
         * camposContainer.add(crearFilaControl("Fecha de vencimiento", spinnerFecha));
         * camposContainer.add(Box.createVerticalStrut(8));
         */
        camposContainer.add(crearFilaControl("Categoría", cmbCategoria));

        JScrollPane scrollForm = new JScrollPane(camposContainer);
        scrollForm.setBorder(BorderFactory.createEmptyBorder());
        scrollForm.getViewport().setBackground(COLOR_PANEL_SEC);
        scrollForm.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formPanel.add(scrollForm, BorderLayout.CENTER);

        JPanel botonesPanel = new JPanel();
        botonesPanel.setLayout(new BoxLayout(botonesPanel, BoxLayout.Y_AXIS));
        botonesPanel.setBackground(COLOR_PANEL_SEC);
        botonesPanel.setBorder(new EmptyBorder(12, 16, 20, 16));

        btnConfirmar = new JButton("Guardar");
        estilizarBotonForm(btnConfirmar, COLOR_ACENTO);
        btnConfirmar.addActionListener(e -> ejecutarOperacion());

        JButton btnCancelar = new JButton("Cancelar");
        estilizarBotonForm(btnCancelar, COLOR_HOVER);
        btnCancelar.addActionListener(e -> ocultarFormulario());

        botonesPanel.add(btnConfirmar);
        botonesPanel.add(Box.createVerticalStrut(8));
        botonesPanel.add(btnCancelar);
        formPanel.add(botonesPanel, BorderLayout.SOUTH);

        add(formPanel, BorderLayout.EAST);
    }

    /**
     * Crea un {@link JTextField} con el estilo visual del tema oscuro.
     *
     * @return campo de texto configurado listo para añadir al formulario
     */
    private JTextField crearCampoTexto() {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI Light", Font.PLAIN, 14));
        campo.setForeground(COLOR_TEXTO);
        campo.setBackground(COLOR_INPUT_BG);
        campo.setCaretColor(COLOR_ACENTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x2A, 0x3D, 0x55), 1),
                new EmptyBorder(6, 10, 6, 10)));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return campo;
    }

    /**
     * Crea una fila de formulario compuesta por una etiqueta descriptiva
     * y un {@link JTextField} apilados verticalmente.
     *
     * @param etiqueta nombre del campo visible al usuario
     * @param campo    {@link JTextField} asociado al campo
     * @return {@link JPanel} contenedor listo para agregar al formulario
     */
    private JPanel crearFilaCampo(String etiqueta, JTextField campo) {
        JPanel fila = new JPanel();
        fila.setLayout(new BoxLayout(fila, BoxLayout.Y_AXIS));
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI Light", Font.PLAIN, 12));
        lbl.setForeground(COLOR_TEXTO_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        fila.add(lbl);
        fila.add(Box.createVerticalStrut(4));
        fila.add(campo);
        return fila;
    }

    /**
     * Crea una fila de formulario genérica para controles distintos a
     * {@link JTextField} (como {@link JSpinner} o {@link JComboBox}),
     * compuesta por una etiqueta y el control apilados verticalmente.
     *
     * @param etiqueta nombre descriptivo del campo
     * @param control  componente Swing del control de entrada
     * @return {@link JPanel} contenedor listo para agregar al formulario
     */
    private JPanel crearFilaControl(String etiqueta, JComponent control) {
        JPanel fila = new JPanel();
        fila.setLayout(new BoxLayout(fila, BoxLayout.Y_AXIS));
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI Light", Font.PLAIN, 12));
        lbl.setForeground(COLOR_TEXTO_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        fila.add(lbl);
        fila.add(Box.createVerticalStrut(4));
        fila.add(control);
        return fila;
    }

    /**
     * Aplica el estilo visual unificado del tema oscuro a un botón del formulario.
     *
     * @param btn   botón a estilizar
     * @param color color de fondo del botón
     */
    private void estilizarBotonForm(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(COLOR_TEXTO);
        btn.setFont(new Font("Segoe UI Light", Font.BOLD, 14));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setBorder(new EmptyBorder(8, 0, 8, 0));
    }

    /**
     * Muestra el panel lateral configurado para el modo de operación indicado,
     * o lo oculta si se vuelve a presionar el mismo botón activo (toggle).
     * 
     * @param modo operación CRUD a activar
     */
    private void mostrarFormulario(String modo) {
        if (modoActual.equals(modo) && formPanel.isVisible()) {
            ocultarFormulario();
            return;
        }

        modoActual = modo;
        limpiarFormulario();

        switch (modo) {
            case "REGISTRAR":
                lblTituloForm.setText("Registrar Producto");
                btnConfirmar.setText("Guardar");
                txtId.setText(UUID.randomUUID().toString());
                txtId.setEnabled(false);
                habilitarCampos(true);
                break;

            case "BUSCAR":
                lblTituloForm.setText("Buscar Producto");
                btnConfirmar.setText("Buscar");
                habilitarCampos(false);
                txtId.setEnabled(true);
                break;

            case "ACTUALIZAR":
                lblTituloForm.setText("Actualizar Producto");
                btnConfirmar.setText("Actualizar");
                habilitarCampos(true);
                if (tablaProductos.getSelectedRow() >= 0) {
                    cargarFilaEnFormulario(tablaProductos.getSelectedRow());
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Selecciona un producto de la tabla para actualizar.",
                            "Sin selección", JOptionPane.WARNING_MESSAGE);
                }
                break;

            case "ELIMINAR":
                lblTituloForm.setText("Eliminar Producto");
                btnConfirmar.setText("Confirmar Eliminación");
                habilitarCampos(false);
                if (tablaProductos.getSelectedRow() >= 0) {
                    cargarFilaEnFormulario(tablaProductos.getSelectedRow());
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Selecciona un producto de la tabla para eliminar.",
                            "Sin selección", JOptionPane.WARNING_MESSAGE);
                }
                break;
        }

        formPanel.setVisible(true);
        revalidate();
        repaint();
    }

    /**
     * Oculta el panel lateral, limpia el formulario y resetea el modo activo.
     */
    private void ocultarFormulario() {
        modoActual = "";
        formPanel.setVisible(false);
        limpiarFormulario();
        revalidate();
        repaint();
        cargarProductos();
    }

    /**
     * Despacha la operación CRUD al método especializado según {@code modoActual}.
     */
    private void ejecutarOperacion() {
        switch (modoActual) {
            case "REGISTRAR" -> {
                registrarProducto();
                cargarProductos();
            }
            case "BUSCAR" -> {
                buscarProducto();
                cargarProductos();
            }
            case "ACTUALIZAR" -> {
                actualizarProducto();
                cargarProductos();
            }
            case "ELIMINAR" -> {
                eliminarProducto();
                cargarProductos();
            }
            default -> {
            }
        }
    }

    private ProductoDTO construirProductoDTODesdeFormulario() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaFormateada = new Date();
        try {
            fechaFormateada = simpleDateFormat.parse("12/04/2025");
        } catch (Exception e) {
            System.out.println("fecha mal formateada en linea 512 de Productos");
        }

        Categoria categoriaSeleccionada = (Categoria) cmbCategoria.getSelectedItem();
        String idCategoria = (categoriaSeleccionada != null) ? categoriaSeleccionada.getId() : "";

        return new ProductoDTO(
                txtId.getText().trim(),
                txtCodigo.getText().trim(),
                txtNombre.getText().trim(),
                txtMarca.getText().trim(),
                txtSerie.getText().trim(),
                txtStock.getText().trim(),
                txtPrecio.getText().trim(),
                simpleDateFormat.format(fechaFormateada),
                idCategoria);
    }

    /**
     * Lee los campos del formulario, construye un objeto {@link Producto}
     * y lo persiste mediante {@link ProductoDAO#agregar(Producto)}.
     *
     * <p>
     * Valida campos obligatorios antes de ejecutar la operación.
     * Refresca la tabla con {@link #cargarProductos()} si la operación
     * es exitosa y oculta el formulario.
     */
    private void registrarProducto() {
        try {
            RespuestaControlador<Producto> respuesta = productoControlador
                    .agregarProducto(construirProductoDTODesdeFormulario());

            if (respuesta.exito()) {
                JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarProductos();
                ocultarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Error de validación",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado al registrar producto: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Busca un producto por su ID en la base de datos y muestra el resultado
     * en la tabla. Si el campo ID está vacío, recarga todos los registros.
     */
    private void buscarProducto() {
        try {
            String id = txtId.getText().trim();

            if (id.isEmpty()) {
                cargarProductos();
                return;
            }

            RespuestaControlador<Producto> respuesta = productoControlador.buscarProductoId(id);

            tableModel.setRowCount(0);

            if (respuesta.exito() && respuesta.dato() != null) {
                agregarFilaTabla(respuesta.dato());
            } else {
                JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Sin resultados",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado al buscar producto: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lee el formulario, construye el objeto {@link Producto} actualizado
     * y lo persiste mediante {@link ProductoDAO#actualizar(Producto)}.
     *
     * <p>
     * Requiere que {@code txtId} tenga el ID del producto a modificar.
     */
    private void actualizarProducto() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecciona un producto de la tabla para actualizar.",
                        "Sin selección", JOptionPane.WARNING_MESSAGE);
                return;
            }

            RespuestaControlador<Producto> respuesta = productoControlador
                    .actualizarProducto(construirProductoDTODesdeFormulario());

            if (respuesta.exito()) {
                JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarProductos();
                ocultarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Error de validación",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado al actualizar producto: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Elimina el producto cuyo ID está cargado en el formulario,
     * previa confirmación explícita del usuario mediante un diálogo.
     */
    private void eliminarProducto() {
        try {
            String id = txtId.getText().trim();

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecciona un producto de la tabla para eliminar.",
                        "Sin selección", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Confirma la eliminación del producto con ID: " + id + "?\n" + "Esta acción no se puede deshacer.",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }

            RespuestaControlador<Producto> respuesta = productoControlador.eliminarProducto(id);

            if (respuesta.exito()) {
                JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarProductos();
                ocultarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Error al eliminar",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado al eliminar producto: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Carga todos los productos desde la base de datos y repuebla la tabla.
     * Limpia el modelo previo antes de insertar las nuevas filas.
     */
    public void cargarProductos() {
        try {
            RespuestaControlador<ArrayList<Producto>> respuesta = productoControlador.obtenerTodos();

            tableModel.setRowCount(0);

            ArrayList<Categoria> lista = categoriaControlador.buscarTodos().dato();

            if (respuesta.exito() && respuesta.dato() != null) {
                for (Producto p : respuesta.dato()) {
                    agregarFilaTabla(p);
                }
            } else {
                JOptionPane.showMessageDialog(this, respuesta.mensaje(), "Información",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado al cargar productos: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Agrega una fila al modelo de la tabla con los datos de un {@link Producto}.
     * Formatea {@code fechaVencimiento} con el patrón {@code dd/MM/yyyy}.
     *
     * @param p producto cuyos datos se agregarán como fila
     */
    private void agregarFilaTabla(Producto p) {

        String fechaStr = "";
        if (p.getFechaVencimiento() != null) {
            fechaStr = new SimpleDateFormat("dd/MM/yyyy").format(p.getFechaVencimiento());
        }

        String nombreCategoria = "";
        for (int i = 0; i < cmbCategoria.getItemCount(); i++) {

            if (cmbCategoria.getItemAt(i).getId().equals(p.getIdCategoria())) {
                nombreCategoria = cmbCategoria.getItemAt(i).getNombre();
                break;
            }
        }

        tableModel.addRow(new Object[] {
                p.getId(),
                p.getCodigo(),
                p.getNombre(),
                p.getMarca(),
                p.getSerie(),
                p.getStock(),
                p.getPrecioActual(),
                fechaStr,
                nombreCategoria
        });
    }

    /**
     * Carga los datos de una fila seleccionada de la tabla en los controles
     * del formulario lateral. Maneja la conversión de tipos para
     * {@link JSpinner} (fecha) y {@link JComboBox} (categoría).
     *
     * @param rowIndex índice de la fila seleccionada en {@code tablaProductos}
     */
    private void cargarFilaEnFormulario(int rowIndex) {
        txtId.setText((String) tableModel.getValueAt(rowIndex, 0));
        txtCodigo.setText((String) tableModel.getValueAt(rowIndex, 1));
        txtNombre.setText((String) tableModel.getValueAt(rowIndex, 2));
        txtMarca.setText((String) tableModel.getValueAt(rowIndex, 3));
        txtSerie.setText((String) tableModel.getValueAt(rowIndex, 4));

        Object stock = tableModel.getValueAt(rowIndex, 5);
        Object precio = tableModel.getValueAt(rowIndex, 6);
        txtStock.setText(stock != null ? stock.toString() : "");
        txtPrecio.setText(precio != null ? precio.toString() : "");

        /*
         * String fechaStr = (String) tableModel.getValueAt(rowIndex, 7);
         * if (fechaStr != null && !fechaStr.isEmpty()) {
         * try {
         * Date fecha = new SimpleDateFormat("dd/MM/yyyy").parse(fechaStr);
         * spinnerFecha.setValue(fecha);
         * } catch (Exception ex) {
         * spinnerFecha.setValue(new Date());
         * }
         * }
         */

        String nombreCategoria = (String) tableModel.getValueAt(rowIndex, 8);
        for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
            if (cmbCategoria.getItemAt(i).toString().equals(nombreCategoria)) {
                cmbCategoria.setSelectedIndex(i);
                break;
            }
        }
    }

    /**
     * Construye un objeto {@link Producto} a partir de los valores actuales
     * de los controles del formulario lateral.
     *
     * <p>
     * Realiza las conversiones de tipo necesarias:
     * {@code txtStock} → {@code Long}, {@code txtPrecio} → {@code Double},
     * {@code spinnerFecha} → {@code Date}, {@code cmbCategoria} →
     * {@code String id}.
     *
     * @return nuevo objeto {@link Producto} con los datos del formulario,
     *         o {@code null} si ocurre un error de conversión numérica
     */
    private Producto construirProductoDesdeFormulario() {
        try {
            String id = txtId.getText().trim();
            String codigo = txtCodigo.getText().trim();
            String nombre = txtNombre.getText().trim();
            String marca = txtMarca.getText().trim();
            String serie = txtSerie.getText().trim();
            long stock = Long.parseLong(txtStock.getText().trim());
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            Date fecha = (Date) spinnerFecha.getValue();
            String Categoria = ((CategoriaItem) cmbCategoria.getSelectedItem()).getId();

            return new Producto(id, codigo, nombre, marca, serie, stock, precio, fecha, Categoria);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Stock debe ser un número entero y Precio un número decimal (ej. 99.99).",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Puebla el {@link JComboBox} {@code cmbCategoria} con todas las categorías
     * disponibles en la base de datos. Cada ítem se representa mediante
     * la clase interna {@link CategoriaItem}, que encapsula id y nombre.
     */
    private void cargarCategorias() {
        cmbCategoria.removeAllItems();

        for (Categoria categoria : categoriaControlador.buscarTodos().dato()) {
            cmbCategoria.addItem(categoria);
        }
    }

    /**
     * Limpia el contenido de todos los controles del formulario lateral
     * y restablece el {@link JSpinner} a la fecha actual.
     */
    private void limpiarFormulario() {
        txtId.setText("");
        txtCodigo.setText("");
        txtNombre.setText("");
        txtMarca.setText("");
        txtSerie.setText("");
        txtStock.setText("");
        txtPrecio.setText("");
        // spinnerFecha.setValue(new Date());
        if (cmbCategoria.getItemCount() > 0)
            cmbCategoria.setSelectedIndex(0);
    }

    /**
     * Habilita o deshabilita la edición de todos los controles del formulario.
     * Siempre deja {@code txtId} deshabilitado en modo REGISTRAR
     * (se gestiona en {@link #mostrarFormulario(String)}).
     *
     * @param habilitar {@code true} para permitir edición, {@code false} para solo
     *                  lectura
     */
    private void habilitarCampos(boolean habilitar) {
        txtId.setEnabled(habilitar);
        txtCodigo.setEnabled(habilitar);
        txtNombre.setEnabled(habilitar);
        txtMarca.setEnabled(habilitar);
        txtSerie.setEnabled(habilitar);
        txtStock.setEnabled(habilitar);
        txtPrecio.setEnabled(habilitar);
        // spinnerFecha.setEnabled(habilitar);
        cmbCategoria.setEnabled(habilitar);
    }

    /**
     * Valida que los campos obligatorios del formulario no estén vacíos
     * ni con formato incorrecto. Muestra un {@link JOptionPane} de advertencia
     * si falla la validación.
     *
     * @return {@code true} si todos los campos obligatorios son válidos,
     *         {@code false} en caso contrario
     */
    private boolean validarCamposObligatorios() {
        if (txtCodigo.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Los campos Código y Nombre son obligatorios.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            Long.valueOf(txtStock.getText().trim());
            Double.valueOf(txtPrecio.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Stock debe ser un número entero (ej. 10) y Precio un decimal (ej. 99.99).",
                    "Formato inválido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Clase interna que encapsula el {@code id} y el {@code nombre} de una
     * {@link Categoria} para ser usada como ítem en el {@link JComboBox}.
     */
    private static class CategoriaItem {
        private final String id;
        private final String nombre;

        /**
         * Construye un ítem de categoría para el ComboBox.
         * 
         * @param id     identificador único de la categoría
         * @param nombre nombre legible de la categoría
         */
        public CategoriaItem(String id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return nombre;
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
