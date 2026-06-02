package CapaPresentacion.GUI_Cliente;

import CapaLogicaNegocio.Controlador.CompraControlador;
import CapaLogicaNegocio.Controlador.PromocionClienteControlador;
import CapaLogicaNegocio.Controlador.RespuestaControlador;
import CapaLogicaNegocio.Logica_Negocio.Carrito;
import CapaLogicaNegocio.Logica_Negocio.ItemCarrito;
import CapaLogicaNegocio.Logica_Negocio.SesionCliente;
import CapaLogicaNegocio.Logica_Negocio.Venta;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Panel del carrito de compras del modulo Cliente.
 * Muestra los items del carrito compartido, permite quitarlos o vaciarlo y
 * procesa la compra
 * UNICAMENTE contra la nube a traves de {@link CompraControlador}. La vista
 * solo conoce el controlador.
 *
 * @author ASUS
 */
public class CarritoCompras extends javax.swing.JPanel {

    private static final Color COLOR_BG = new Color(26, 30, 41);
    private static final Color COLOR_CARD = new Color(19, 45, 70);
    private static final Color COLOR_ACENTO = new Color(1, 195, 142);
    private static final Color COLOR_BOTON = new Color(1, 128, 95);
    private static final Color COLOR_TABLE_BG = new Color(0x10, 0x14, 0x1E);

    private final transient Carrito carrito;
    private final transient CompraControlador compraControlador = new CompraControlador();
    private final transient PromocionClienteControlador promocionControlador = new PromocionClienteControlador();

    private transient Runnable onCompraExitosa;

    private DefaultTableModel modeloTabla;
    private JTable tablaProductos;
    private JLabel lblTotal;
    private JComboBox<Venta.MetodoPago> comboMetodoPago;
    private JButton btnAceptarCompra;

    public CarritoCompras(Carrito carrito) {
        this.carrito = carrito;
        inicializarComponentes();
        refrescar();
    }

    /**
     * Permite a la ventana principal reaccionar tras una compra exitosa (ej.
     * refrescar el catalogo
     * porque cambio el stock).
     */
    public void setOnCompraExitosa(Runnable onCompraExitosa) {
        this.onCompraExitosa = onCompraExitosa;
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);
        setPreferredSize(new Dimension(1600, 1000));

        JLabel titulo = new JLabel("CARRITO DE COMPRAS");
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titulo, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
                new Object[] { "Producto", "Precio Unit.", "Cantidad", "Subtotal" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setRowHeight(34);
        tablaProductos.setBackground(COLOR_TABLE_BG);
        tablaProductos.setForeground(Color.WHITE);
        tablaProductos.setGridColor(COLOR_CARD);
        tablaProductos.setSelectionBackground(COLOR_ACENTO);
        tablaProductos.setSelectionForeground(Color.WHITE);
        tablaProductos.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tablaProductos.setShowGrid(true);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);
        cellRenderer.setForeground(Color.WHITE);
        cellRenderer.setBackground(COLOR_TABLE_BG);
        for (int col = 0; col < tablaProductos.getColumnCount(); col++) {
            tablaProductos.getColumnModel().getColumn(col).setCellRenderer(cellRenderer);
        }

        JTableHeader header = tablaProductos.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setReorderingAllowed(false);
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scroll = new JScrollPane(tablaProductos);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        scroll.getViewport().setBackground(COLOR_TABLE_BG);
        add(scroll, BorderLayout.CENTER);

        add(crearPanelInferior(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelInferior() {
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(COLOR_BG);
        panelInferior.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        lblTotal = new JLabel("Total: $0");
        lblTotal.setForeground(Color.WHITE);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panelInferior.add(lblTotal, BorderLayout.WEST);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        acciones.setOpaque(false);

        JButton btnQuitar = crearBoton("Quitar seleccionado", COLOR_CARD);
        btnQuitar.addActionListener(e -> quitarSeleccionado());

        JButton btnVaciar = crearBoton("Vaciar", COLOR_CARD);
        btnVaciar.addActionListener(e -> vaciarCarrito());

        JLabel lblPago = new JLabel("Pago:");
        lblPago.setForeground(Color.WHITE);
        lblPago.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        comboMetodoPago = new JComboBox<>(Venta.MetodoPago.values());
        comboMetodoPago.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        btnAceptarCompra = crearBoton("Aceptar Compra", COLOR_BOTON);
        btnAceptarCompra.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAceptarCompra.addActionListener(e -> confirmarCompra());

        acciones.add(btnQuitar);
        acciones.add(btnVaciar);
        acciones.add(lblPago);
        acciones.add(comboMetodoPago);
        acciones.add(btnAceptarCompra);

        panelInferior.add(acciones, BorderLayout.EAST);
        return panelInferior;
    }

    private JButton crearBoton(String texto, Color fondo) {
        JButton btn = new JButton(texto);
        btn.setBackground(fondo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Reconstruye la tabla a partir del carrito compartido. La fila i corresponde
     * al item i del
     * carrito, asi la seleccion para quitar se mapea directo.
     */
    public void refrescar() {
        modeloTabla.setRowCount(0);
        for (ItemCarrito item : carrito.getItems()) {
            modeloTabla.addRow(new Object[] {
                    item.getProducto().getNombre(),
                    formatoPrecio(item.getProducto().getPrecioActual()),
                    item.getCantidad(),
                    formatoPrecio(item.getSubtotal())
            });
        }
        actualizarTotal();
    }

    private void actualizarTotal() {
        lblTotal.setText("Total: " + formatoPrecio(carrito.calcularTotal()));
    }

    private void quitarSeleccionado() {
        int fila = tablaProductos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto del carrito para quitarlo.",
                    "Carrito", JOptionPane.WARNING_MESSAGE);
            return;
        }
        carrito.quitar(carrito.getItems().get(fila).getProducto().getId());
        refrescar();
    }

    private void vaciarCarrito() {
        if (carrito.estaVacio())
            return;
        int confirm = JOptionPane.showConfirmDialog(this, "¿Vaciar el carrito?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            carrito.vaciar();
            refrescar();
        }
    }

    private void confirmarCompra() {
        if (carrito.estaVacio()) {
            JOptionPane.showMessageDialog(this, "No hay productos en el carrito.",
                    "Carrito vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Venta.MetodoPago metodoPago = (Venta.MetodoPago) comboMetodoPago.getSelectedItem();
        String idCliente = SesionCliente.getClienteActual().getId();

        // Obtener el mejor descuento personal del cliente desde la nube
        final double descuentoCliente = obtenerMejorDescuentoCliente(idCliente);
        final double totalOriginal = carrito.calcularTotal();
        final double totalFinal = descuentoCliente > 0
                ? totalOriginal * (1.0 - descuentoCliente / 100.0)
                : totalOriginal;

        String msgConfirm;
        if (descuentoCliente > 0) {
            msgConfirm = "Tu descuento personalizado: " + (int) descuentoCliente + "%\n"
                    + "Total con descuento: " + formatoPrecio(totalFinal) + "\n¿Confirmar la compra?";
        } else {
            msgConfirm = "Total a pagar: " + formatoPrecio(totalOriginal) + "\n¿Confirmar la compra?";
        }

        int confirm = JOptionPane.showConfirmDialog(this, msgConfirm,
                "Confirmar compra", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        // PSE: informar redireccion antes de generar la compra (integración futura).
        if (metodoPago == Venta.MetodoPago.PSE) {
            JOptionPane.showMessageDialog(this,
                    "Serás redirigido a la pasarela de pagos PSE.\n"
                            + "(Integración futura) Por ahora se generará tu compra directamente.",
                    "Pago con PSE", JOptionPane.INFORMATION_MESSAGE);
        }

        btnAceptarCompra.setEnabled(false);

        new SwingWorker<RespuestaControlador<Venta>, Void>() {
            @Override
            protected RespuestaControlador<Venta> doInBackground() {
                return descuentoCliente > 0
                        ? compraControlador.registrarCompra(carrito, idCliente, metodoPago, descuentoCliente)
                        : compraControlador.registrarCompra(carrito, idCliente, metodoPago);
            }

            @Override
            protected void done() {
                btnAceptarCompra.setEnabled(true);
                RespuestaControlador<Venta> respuesta;
                try {
                    respuesta = get();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CarritoCompras.this,
                            "Ocurrió un error al procesar la compra.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (respuesta != null && respuesta.exito()) {
                    mostrarMensajeExito(metodoPago, respuesta);
                    carrito.vaciar();
                    refrescar();
                    if (onCompraExitosa != null)
                        onCompraExitosa.run();
                } else {
                    String mensaje = respuesta != null ? respuesta.mensaje() : "No se pudo procesar la compra.";
                    JOptionPane.showMessageDialog(CarritoCompras.this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Consulta la nube y devuelve el mayor porcentaje de descuento personal activo
     * para el cliente.
     * Retorna 0 si no hay promo activa o si la consulta falla.
     */
    private double obtenerMejorDescuentoCliente(String idCliente) {
        try {
            RespuestaControlador<ArrayList<CapaLogicaNegocio.DTOS.Promociones>> resp = promocionControlador
                    .listarPromocionesCliente(idCliente);
            if (resp == null || !resp.exito() || resp.dato() == null)
                return 0;
            double mejor = 0;
            for (CapaLogicaNegocio.DTOS.Promociones p : resp.dato()) {
                if (p.getDescuento() != null && p.getDescuento() > mejor) {
                    mejor = p.getDescuento();
                }
            }
            return mejor;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Muestra el mensaje de exito segun el metodo de pago. Para EFECTY genera una
     * referencia de pago
     * que el cliente debe llevar al punto EFECTY; para los demas metodos muestra la
     * confirmacion normal.
     */
    private void mostrarMensajeExito(Venta.MetodoPago metodoPago, RespuestaControlador<Venta> respuesta) {
        if (metodoPago == Venta.MetodoPago.EFECTY) {
            String referencia = "EFY-" + (100000000L + (long) (Math.random() * 899999999L));
            JOptionPane.showMessageDialog(this,
                    "¡Compra generada!\n\nEsta es tu referencia de pago: " + referencia + "\n"
                            + "Acércate al punto EFECTY más cercano y paga con esta referencia.",
                    "Pago con EFECTY", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, respuesta.mensaje(),
                    "Compra Confirmada", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String formatoPrecio(Double precio) {
        if (precio == null)
            return "$0";
        return "$" + String.format("%,.0f", precio);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setMinimumSize(new java.awt.Dimension(1600, 1000));
        setPreferredSize(new java.awt.Dimension(1600, 1000));
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
