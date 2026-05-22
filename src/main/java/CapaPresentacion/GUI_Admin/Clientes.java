package CapaPresentacion.GUI_Admin;

import CapaDatos.Logica_Conexion.ClienteDAO;
import CapaLogicaNegocio.Logica_Negocio.Cliente;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author Marlon Vargas
 */
public class Clientes extends javax.swing.JPanel {

    // ──────────────────────────────────────────────────────────────
    // Paleta de colores (coherente con Menu_Admin)
    // ──────────────────────────────────────────────────────────────
 
    /** Fondo general del panel (igual que COLOR_CONTENIDO_BG en Menu_Admin). */
    private static final Color COLOR_BG          = new Color(0x1A, 0x1E, 0x29);
 
    /** Fondo de la toolbar y del panel lateral. */
    private static final Color COLOR_PANEL_SEC   = new Color(0x13, 0x2D, 0x46);
 
    /** Color de acento / botón activo — verde Sborg. */
    private static final Color COLOR_ACENTO      = new Color(0x01, 0xC3, 0x8E);
 
    /** Color hover de botones. */
    private static final Color COLOR_HOVER       = new Color(0x1A, 0x3D, 0x58);
 
    /** Color de texto principal — blanco. */
    private static final Color COLOR_TEXTO       = new Color(0x1C, 0x1C, 0x1C);
 
    /** Color de texto secundario / placeholder. */
    private static final Color COLOR_TEXTO_MUTED = new Color(0x8A, 0xA5, 0xBE);
 
    /** Color de fondo de la tabla. */
    private static final Color COLOR_TABLE_BG= new Color(0x10, 0x14, 0x1E);
 
    /** Color de fila seleccionada. */
    private static final Color COLOR_ROW_SEL     = new Color(0x01, 0xC3, 0x8E, 80);
 
    /** Ancho fijo del panel lateral de formulario en píxeles. */
    private static final int FORM_PANEL_WIDTH    = 320;
 
    // ──────────────────────────────────────────────────────────────
    // Componentes principales
    // ──────────────────────────────────────────────────────────────
 
    /** DAO para operaciones CRUD sobre la entidad {@link Cliente}. */
    private final ClienteDAO clienteDAO = new ClienteDAO();
 
    /** Modelo de datos de la tabla de clientes. */
    private DefaultTableModel tableModel;
 
    /** Tabla que muestra el listado de clientes. */
    private JTable tablaClientes;
 
    /**
     * Panel lateral derecho que contiene el formulario de la operación activa.
     * Se muestra u oculta según el botón presionado.
     */
    private JPanel formPanel;
 
    // ── Campos del formulario ──
    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtCedula;
    private JTextField txtDireccion;
 
    /** Etiqueta que indica la operación activa en el panel lateral. */
    private JLabel lblTituloForm;
 
    /** Botón de confirmación dentro del formulario (cambia según la operación). */
    private JButton btnConfirmar;
 
    /** Modo de operación activo: REGISTRAR | BUSCAR | ACTUALIZAR | ELIMINAR. */
    private String modoActual = "";
 
    /** Ruta base del proyecto (para cargar recursos). */
    public String s;
 
    // ──────────────────────────────────────────────────────────────
    // Constructor
    // ──────────────────────────────────────────────────────────────
 
    /**
     * Construye el panel de gestión de clientes e inicializa todos
     * sus sub-componentes: toolbar, tabla y panel lateral.
     */
    public Clientes() {
        s = Paths.get("").toAbsolutePath().toString();
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);
 
        construirToolbar();
        construirTabla();
        construirFormPanel();
        cargarClientes(); 
    }
 
    /**
     * Construye la barra superior con los cuatro botones de acción del módulo.
     *
     * <p>Los botones son: <b>Registrar Cliente</b>, <b>Buscar</b>,
     * <b>Actualizar</b> y <b>Eliminar</b>. Cada uno, al ser presionado,
     * invoca {@link #mostrarFormulario(String)} con el modo correspondiente.
     */
    private void construirToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        toolbar.setBackground(COLOR_PANEL_SEC);
        toolbar.setBorder(new EmptyBorder(8, 16, 8, 16));
 
        // Título del módulo
        JLabel lblModulo = new JLabel("Gestión de Clientes");
        lblModulo.setFont(new Font("Segoe UI Light", Font.BOLD, 24));
        lblModulo.setForeground(Color.WHITE);
        toolbar.add(lblModulo);
 
        // Separador visual
        toolbar.add(Box.createHorizontalStrut(24));
 
        // Botones de acción
        toolbar.add(crearBotonAccion("Buscar Cliente",            "BUSCAR"));
        toolbar.add(crearBotonAccion("Actualizar Cliente",        "ACTUALIZAR"));
        toolbar.add(crearBotonAccion("Eliminar Cliente",          "ELIMINAR"));
 
        add(toolbar, BorderLayout.NORTH);
    }
 
    /**
     * Crea un botón de acción para la toolbar con estilos hover y activo.
     *
     * @param etiqueta texto visible del botón
     * @param modo     modo de operación que activa este botón
     *                 ({@code "REGISTRAR"}, {@code "BUSCAR"}, {@code "ACTUALIZAR"}, {@code "ELIMINAR"})
     * @return {@link JButton} completamente configurado
     */
    private JButton crearBotonAccion(String etiqueta, String modo) {
        JButton btn = new JButton(etiqueta);
        btn.setFont(new Font("Segoe UI Light", Font.PLAIN, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(0x1A, 0x3D, 0x58));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
 
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(COLOR_ACENTO); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(COLOR_HOVER);  }
        });
 
        btn.addActionListener(e -> mostrarFormulario(modo));
        return btn;
    }
 
    // ──────────────────────────────────────────────────────────────
    // Construcción de la Tabla (zona CENTER)
    // ──────────────────────────────────────────────────────────────
 
    /**
     * Construye el panel de la tabla de clientes con un {@link JScrollPane}.
     *
     * <p>Las columnas corresponden exactamente a los campos de la entidad
     * {@link Cliente} en la base de datos:
     * <ul>
     *   <li>ID, Nombre, Apellido, Cédula, Dirección, URL Imagen</li>
     * </ul>
     *
     * <p>Al seleccionar una fila, los datos se cargan automáticamente
     * en el formulario lateral mediante {@link #cargarFilaEnFormulario(int)}.
     */
    private void construirTabla() {
        // Columnas del modelo (NO editables directamente en la tabla)
        String[] columnas = {"ID", "Nombre", "Apellido", "Cédula", "Dirección"};
        tableModel = new DefaultTableModel(columnas, 0) {
            /**
             * Deshabilita la edición directa de celdas en la tabla.
             *
             * @param row    fila de la celda
             * @param column columna de la celda
             * @return siempre {@code false}
             */
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
 
        tablaClientes = new JTable(tableModel);
        tablaClientes.setBackground(COLOR_TABLE_BG);
        tablaClientes.setForeground(COLOR_TEXTO);
        tablaClientes.setFont(new Font("Segoe UI Light", Font.PLAIN, 14));
        tablaClientes.setRowHeight(36);
        tablaClientes.setShowHorizontalLines(true);
        tablaClientes.setGridColor(new Color(0x2A, 0x2E, 0x3D));
        tablaClientes.setSelectionBackground(COLOR_ROW_SEL);
        tablaClientes.setSelectionForeground(COLOR_TEXTO);
        tablaClientes.setIntercellSpacing(new Dimension(0, 1));
 
        // Encabezado de la tabla
        JTableHeader header = tablaClientes.getTableHeader();
        header.setBackground(COLOR_PANEL_SEC);
        header.setForeground(COLOR_TEXTO);
        header.setFont(new Font("Segoe UI Light", Font.BOLD, 14));
        header.setReorderingAllowed(false);
 
        // Al seleccionar una fila → cargar datos en el formulario
        tablaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaClientes.getSelectedRow() >= 0) {
                cargarFilaEnFormulario(tablaClientes.getSelectedRow());
            }
        });
 
        // Ajuste de anchos de columna
        tablaClientes.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaClientes.getColumnModel().getColumn(1).setPreferredWidth(130);
        tablaClientes.getColumnModel().getColumn(2).setPreferredWidth(130);
        tablaClientes.getColumnModel().getColumn(3).setPreferredWidth(110);
        tablaClientes.getColumnModel().getColumn(4).setPreferredWidth(200);
 
        JScrollPane scroll = new JScrollPane(tablaClientes);
        scroll.setBackground(COLOR_TABLE_BG);
        scroll.getViewport().setBackground(COLOR_TABLE_BG);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
 
        // Panel contenedor con padding
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(COLOR_BG);
        tableWrapper.setBorder(new EmptyBorder(16, 16, 16, 16));
        tableWrapper.add(scroll, BorderLayout.CENTER);
 
        add(tableWrapper, BorderLayout.CENTER);
    }
 
    // ──────────────────────────────────────────────────────────────
    // Construcción del Panel Lateral de Formulario (zona EAST)
    // ──────────────────────────────────────────────────────────────
 
    /**
     * Construye el panel lateral derecho que contiene el formulario CRUD.
     *
     * <p>Inicialmente se crea <b>oculto</b> ({@code setVisible(false)}).
     * Se hace visible al invocar {@link #mostrarFormulario(String)}.
     *
     * <p>El panel contiene:
     * <ul>
     *   <li>Título dinámico según la operación</li>
     *   <li>Campos: ID, Nombre, Apellido, Cédula, Dirección, URL Imagen</li>
     *   <li>Botón de confirmación (Guardar / Buscar / Actualizar / Eliminar)</li>
     *   <li>Botón Cancelar para ocultar el panel</li>
     * </ul>
     */
    private void construirFormPanel() {
        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(COLOR_PANEL_SEC);
        formPanel.setPreferredSize(new Dimension(FORM_PANEL_WIDTH, 0));
        formPanel.setBorder(new EmptyBorder(24, 16, 16, 16));
        formPanel.setVisible(false); // Oculto por defecto
 
        // ── Título dinámico del formulario ──
        lblTituloForm = new JLabel("Buscar Cliente");
        lblTituloForm.setFont(new Font("Segoe UI Light", Font.BOLD, 18));
        lblTituloForm.setForeground(Color.WHITE);
        lblTituloForm.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(lblTituloForm);
        formPanel.add(Box.createVerticalStrut(20));
 
        // ── Campos del formulario ──
        txtId        = crearCampoForm("ID");
        txtNombre    = crearCampoForm("Nombre");
        txtApellido  = crearCampoForm("Apellido");
        txtCedula    = crearCampoForm("Cédula");
        txtDireccion = crearCampoForm("Dirección");
 
        formPanel.add(crearFilaCampo("ID",         txtId));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(crearFilaCampo("Nombre",     txtNombre));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(crearFilaCampo("Apellido",   txtApellido));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(crearFilaCampo("Cédula",     txtCedula));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(crearFilaCampo("Dirección",  txtDireccion));
        formPanel.add(Box.createVerticalStrut(10));
 
        // ── Botón confirmar ──
        btnConfirmar = new JButton("Guardar");
        estilizarBotonForm(btnConfirmar, COLOR_ACENTO);
        btnConfirmar.addActionListener(e -> ejecutarOperacion());
        formPanel.add(btnConfirmar);
        formPanel.add(Box.createVerticalStrut(10));
 
        // ── Botón cancelar ──
        JButton btnCancelar = new JButton("Cancelar");
        estilizarBotonForm(btnCancelar, COLOR_HOVER);
        btnCancelar.addActionListener(e -> ocultarFormulario());
        formPanel.add(btnCancelar);
 
        add(formPanel, BorderLayout.EAST);
    }
 
    /**
     * Crea un campo de texto ({@link JTextField}) estilizado para el formulario lateral.
     *
     * @param placeholder texto de ayuda visible cuando el campo está vacío
     * @return {@link JTextField} configurado con estilos del tema oscuro
     */
    private JTextField crearCampoForm(String placeholder) {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI Light", Font.PLAIN, 14));
        campo.setForeground(COLOR_TEXTO_MUTED);
        campo.setBackground(new Color(0x0D, 0x12, 0x1E));
        campo.setCaretColor(COLOR_ACENTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x2A, 0x3D, 0x55), 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.putClientProperty("placeholder", placeholder);
        return campo;
    }
 
    /**
     * Crea un panel que contiene una etiqueta y un campo de texto
     * apilados verticalmente, para componer cada fila del formulario.
     *
     * @param etiqueta nombre del campo visible al usuario
     * @param campo    el {@link JTextField} asociado
     * @return {@link JPanel} contenedor listo para añadir al formulario
     */
    private JPanel crearFilaCampo(String etiqueta, JTextField campo) {
        JPanel fila = new JPanel();
        fila.setLayout(new BoxLayout(fila, BoxLayout.Y_AXIS));
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
 
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI Light", Font.PLAIN, 12));
        lbl.setForeground(Color.WHITE);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        fila.add(lbl);
        fila.add(Box.createVerticalStrut(4));
        fila.add(campo);
        return fila;
    }
 
    /**
     * Aplica el estilo visual unificado a un botón del formulario lateral.
     *
     * @param btn   botón a estilizar
     * @param color color de fondo del botón
     */
    private void estilizarBotonForm(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI Light", Font.BOLD, 14));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setBorder(new EmptyBorder(8, 0, 8, 0));
    }
 
    // ──────────────────────────────────────────────────────────────
    // Lógica de navegación y visibilidad del formulario
    // ──────────────────────────────────────────────────────────────
 
    /**
     * Muestra el panel lateral con el formulario configurado según el modo
     * de operación solicitado, o lo oculta si se presiona el mismo botón
     * que está activo actualmente (comportamiento toggle).
     *
     * <p>Modos soportados:
     * <ul>
     *   <li>{@code "REGISTRAR"} — habilita todos los campos, genera ID automático</li>
     *   <li>{@code "BUSCAR"}    — habilita solo el campo ID, deshabilita los demás</li>
     *   <li>{@code "ACTUALIZAR"} — requiere selección en tabla, todos los campos editables</li>
     *   <li>{@code "ELIMINAR"} — requiere selección en tabla, solo muestra datos</li>
     * </ul>
     *
     * @param modo operación a activar ({@code "REGISTRAR"}, {@code "BUSCAR"},
     *             {@code "ACTUALIZAR"}, {@code "ELIMINAR"})
     */
    private void mostrarFormulario(String modo) {
        // Toggle: si se presiona el mismo modo activo, ocultar
        if (modoActual.equals(modo) && formPanel.isVisible()) {
            ocultarFormulario();
            return;
        }
 
        modoActual = modo;
        limpiarFormulario();
 
        switch (modo) {
 
            case "BUSCAR" -> {
                lblTituloForm.setText("Buscar Cliente");
                btnConfirmar.setText("Buscar");
                habilitarCampos(false);
                txtId.setEnabled(true);
            }
 
            case "ACTUALIZAR" -> {
                lblTituloForm.setText("Actualizar Cliente");
                btnConfirmar.setText("Actualizar");
                habilitarCampos(true);
                if (tablaClientes.getSelectedRow() >= 0) {
                    cargarFilaEnFormulario(tablaClientes.getSelectedRow());
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Selecciona un cliente de la tabla para actualizar.",
                            "Sin selección", JOptionPane.WARNING_MESSAGE);
                }
            }
 
            case "ELIMINAR" -> {
                lblTituloForm.setText("Eliminar Cliente");
                btnConfirmar.setText("Confirmar Eliminación");
                habilitarCampos(false); // Solo lectura en modo eliminar
                if (tablaClientes.getSelectedRow() >= 0) {
                    cargarFilaEnFormulario(tablaClientes.getSelectedRow());
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Selecciona un cliente de la tabla para eliminar.",
                            "Sin selección", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
 
        formPanel.setVisible(true);
        revalidate();
        repaint();
    }
 
    /**
     * Oculta el panel lateral y resetea el modo activo.
     */
    private void ocultarFormulario() {
        modoActual = "";
        formPanel.setVisible(false);
        limpiarFormulario();
        revalidate();
        repaint();
    }
 
    // ──────────────────────────────────────────────────────────────
    // Lógica CRUD
    // ──────────────────────────────────────────────────────────────
 
    /**
     * Ejecuta la operación CRUD correspondiente al modo activo.
     * Delega a los métodos especializados según {@code modoActual}.
     */
    private void ejecutarOperacion() {
        switch (modoActual) {
            case "BUSCAR" -> buscarCliente();
            case "ACTUALIZAR" -> actualizarCliente();
            case "ELIMINAR" -> eliminarCliente();
        }
    }
 
    /**
     * Busca un cliente por su ID y muestra los resultados en la tabla.
     * Si el ID está vacío, recarga todos los registros.
     */
    private void buscarCliente() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            cargarClientes();
            return;
        }
 
        Cliente cliente = clienteDAO.obtener(id);
        tableModel.setRowCount(0); // Limpiar tabla
 
        if (cliente != null) {
            tableModel.addRow(new Object[]{
                cliente.getId(), cliente.getNombre(), cliente.getApellido(),
                cliente.getCedula(), cliente.getDireccion()
            });
        } else {
            JOptionPane.showMessageDialog(this,
                "No se encontró un cliente con el ID: " + id,
                "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        }
    }
 
    /**
     * Actualiza el registro del cliente actualmente cargado en el formulario.
     *
     * <p>Requiere que el campo ID contenga el identificador del cliente a modificar.
     * Valida campos obligatorios antes de ejecutar la actualización.
     */
    private void actualizarCliente() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un cliente de la tabla para actualizar.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validarCamposObligatorios()) return;
 
        Cliente cliente = new Cliente(
            txtId.getText().trim(),
            txtNombre.getText().trim(),
            txtApellido.getText().trim(),
            txtCedula.getText().trim(),
            txtDireccion.getText().trim()
        );
 
        boolean exito = clienteDAO.actualizar(cliente);
        if (exito) {
            JOptionPane.showMessageDialog(this,
                "Cliente actualizado correctamente.", "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            cargarClientes();
            ocultarFormulario();
        } else {
            JOptionPane.showMessageDialog(this,
                "No se pudo actualizar el cliente.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    /**
     * Elimina el cliente cuyo ID está cargado en el formulario,
     * previa confirmación del usuario mediante un diálogo.
     */
    private void eliminarCliente() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un cliente de la tabla para eliminar.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar al cliente con ID: " + id + "?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
 
        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean exito = clienteDAO.eliminar(id);
            if (exito) {
                JOptionPane.showMessageDialog(this,
                    "Cliente eliminado correctamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                cargarClientes();
                ocultarFormulario();
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se pudo eliminar el cliente.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
 
    // ──────────────────────────────────────────────────────────────
    // Métodos utilitarios
    // ──────────────────────────────────────────────────────────────
 
    /**
     * Carga todos los clientes desde la base de datos y los muestra en la tabla.
     * Limpia el modelo previo antes de insertar las nuevas filas.
     */
    public void cargarClientes() {
        tableModel.setRowCount(0);
        ArrayList<Cliente> clientes = clienteDAO.obteners();
        for (Cliente c : clientes) {
            tableModel.addRow(new Object[]{
                c.getId(), c.getNombre(), c.getApellido(),
                c.getCedula(), c.getDireccion()
            });
        }
    }
 
    /**
     * Carga los datos de la fila seleccionada en la tabla hacia
     * los campos del formulario lateral para su edición o visualización.
     *
     * @param rowIndex índice de la fila seleccionada en {@code tablaClientes}
     */
    private void cargarFilaEnFormulario(int rowIndex) {
        txtId.setText((String) tableModel.getValueAt(rowIndex, 0));
        txtNombre.setText((String) tableModel.getValueAt(rowIndex, 1));
        txtApellido.setText((String) tableModel.getValueAt(rowIndex, 2));
        txtCedula.setText((String) tableModel.getValueAt(rowIndex, 3));
        txtDireccion.setText((String) tableModel.getValueAt(rowIndex, 4));
    }
 
    /**
     * Limpia el contenido de todos los campos del formulario lateral.
     */
    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtCedula.setText("");
        txtDireccion.setText("");
    }
 
    /**
     * Habilita o deshabilita la edición de todos los campos del formulario.
     *
     * @param habilitar {@code true} para habilitar, {@code false} para deshabilitar
     */
    private void habilitarCampos(boolean habilitar) {
        txtId.setEnabled(habilitar);
        txtNombre.setEnabled(habilitar);
        txtApellido.setEnabled(habilitar);
        txtCedula.setEnabled(habilitar);
        txtDireccion.setEnabled(habilitar);
    }
 
    /**
     * Valida que los campos obligatorios del formulario no estén vacíos.
     * Muestra un mensaje de advertencia si alguno falta.
     *
     * @return {@code true} si todos los campos obligatorios tienen valor,
     *         {@code false} en caso contrario
     */
    private boolean validarCamposObligatorios() {
        if (txtNombre.getText().trim().isEmpty() ||
            txtApellido.getText().trim().isEmpty() ||
            txtCedula.getText().trim().isEmpty()) {
 
            JOptionPane.showMessageDialog(this,
                "Los campos Nombre, Apellido y Cédula son obligatorios.",
                "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
