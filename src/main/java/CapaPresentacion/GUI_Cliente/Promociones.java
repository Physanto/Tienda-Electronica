/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package CapaPresentacion.GUI_Cliente;

import CapaLogicaNegocio.Controlador.PromocionClienteControlador;
import CapaLogicaNegocio.Controlador.RespuestaControlador;
import CapaLogicaNegocio.Logica_Negocio.SesionCliente;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Panel de promociones del modulo Cliente.
 * Lista las promociones vigentes leyendolas UNICAMENTE desde la nube a traves de
 * {@link PromocionClienteControlador}. La vista solo conoce el controlador.
 *
 * <p>Nota: se referencia el DTO de promociones con su nombre completamente calificado
 * ({@code CapaLogicaNegocio.DTOS.Promociones}) porque esta clase de la vista tambien se llama
 * {@code Promociones} y un import provocaria un choque de nombres.</p>
 *
 * @author ASUS
 */
public class Promociones extends javax.swing.JPanel {

    private static final Color COLOR_BG = new Color(26, 30, 41);
    private static final Color COLOR_CARD = new Color(19, 45, 70);
    private static final Color COLOR_ACENTO = new Color(1, 195, 142);
    private static final Color COLOR_BOTON = new Color(1, 128, 95);

    private final transient PromocionClienteControlador promocionControlador = new PromocionClienteControlador();
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

    private JPanel panelPromos;

    /**
     * Creates new form Promociones
     */
    public Promociones() {
        inicializarComponentes();
        cargarPromociones();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);
        setPreferredSize(new Dimension(1600, 1000));

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(COLOR_BG);
        cabecera.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));

        JLabel titulo = new JLabel("PROMOCIONES ACTIVAS");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        cabecera.add(titulo, BorderLayout.WEST);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnActualizar.setBackground(COLOR_BOTON);
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.addActionListener(e -> cargarPromociones());
        cabecera.add(btnActualizar, BorderLayout.EAST);

        add(cabecera, BorderLayout.NORTH);

        panelPromos = new JPanel(new GridLayout(0, 3, 20, 20));
        panelPromos.setBackground(COLOR_BG);
        panelPromos.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(COLOR_BG);
        wrapper.add(panelPromos, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COLOR_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Carga las promociones vigentes desde la nube en segundo plano (SwingWorker) para no congelar
     * la interfaz, y luego pinta las tarjetas en el hilo de la UI.
     */
    public void cargarPromociones() {
        panelPromos.removeAll();
        panelPromos.add(crearMensaje("Cargando promociones..."));
        panelPromos.revalidate();
        panelPromos.repaint();

        new SwingWorker<RespuestaControlador<ArrayList<CapaLogicaNegocio.DTOS.Promociones>>, Void>() {
            @Override
            protected RespuestaControlador<ArrayList<CapaLogicaNegocio.DTOS.Promociones>> doInBackground() {
                return promocionControlador.listarPromocionesActivas();
            }

            @Override
            protected void done() {
                try {
                    mostrarPromociones(get());
                } catch (Exception e) {
                    panelPromos.removeAll();
                    panelPromos.add(crearMensaje("No se pudieron cargar las promociones."));
                    panelPromos.revalidate();
                    panelPromos.repaint();
                }
            }
        }.execute();
    }

    private void mostrarPromociones(RespuestaControlador<ArrayList<CapaLogicaNegocio.DTOS.Promociones>> respuesta) {
        panelPromos.removeAll();

        String idClienteSesion = SesionCliente.getClienteActual().getId();
        int mostradas = 0;

        if (respuesta != null && respuesta.exito() && respuesta.dato() != null) {
            for (CapaLogicaNegocio.DTOS.Promociones promocion : respuesta.dato()) {
                String idProducto = promocion.getIdProducto();
                String idCliente = promocion.getIdCliente();

                // Solo se muestran: ofertas por producto (para todos), las promociones personales de
                // ESTE cliente, y promos generales sin vinculo. Las personales de otros clientes se ocultan.
                boolean esOfertaProducto = idProducto != null && !idProducto.isEmpty();
                boolean esMiPromocion = idCliente != null && idCliente.equals(idClienteSesion);
                boolean esGeneral = (idProducto == null || idProducto.isEmpty())
                        && (idCliente == null || idCliente.isEmpty());

                if (!esOfertaProducto && !esMiPromocion && !esGeneral) continue;

                String etiqueta = esOfertaProducto ? "Oferta de producto"
                        : (esMiPromocion ? "Tu promoción" : "Promoción general");
                panelPromos.add(crearTarjetaPromocion(promocion, etiqueta));
                mostradas++;
            }
        }

        if (mostradas == 0) {
            String mensaje = (respuesta != null && respuesta.mensaje() != null && !respuesta.exito())
                    ? respuesta.mensaje() : "No hay promociones activas en este momento.";
            panelPromos.add(crearMensaje(mensaje));
        }

        panelPromos.revalidate();
        panelPromos.repaint();
    }

    private JPanel crearTarjetaPromocion(CapaLogicaNegocio.DTOS.Promociones promocion, String etiqueta) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createLineBorder(COLOR_ACENTO, 2));
        card.setPreferredSize(new Dimension(300, 200));

        JLabel lblDescuento = new JLabel("-" + formatoDescuento(promocion.getDescuento()) + "%", SwingConstants.CENTER);
        lblDescuento.setForeground(COLOR_ACENTO);
        lblDescuento.setFont(new Font("Segoe UI", Font.BOLD, 40));
        lblDescuento.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        card.add(lblDescuento, BorderLayout.NORTH);

        JPanel info = new JPanel(new GridLayout(0, 1, 0, 6));
        info.setOpaque(false);
        info.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));

        JLabel lblEtiqueta = new JLabel(etiqueta.toUpperCase(), SwingConstants.CENTER);
        lblEtiqueta.setForeground(COLOR_BG);
        lblEtiqueta.setBackground("Tu promoción".equals(etiqueta) ? new Color(0x3B, 0x82, 0xF6) : COLOR_ACENTO);
        lblEtiqueta.setOpaque(true);
        lblEtiqueta.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEtiqueta.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));

        JLabel lblNombre = new JLabel(textoSeguro(promocion.getNombre()), SwingConstants.CENTER);
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel lblTipo = new JLabel("Tipo: " + (promocion.getTipo() != null ? promocion.getTipo().name() : "—"),
                SwingConstants.CENTER);
        lblTipo.setForeground(Color.LIGHT_GRAY);
        lblTipo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblVigencia = new JLabel(textoVigencia(promocion), SwingConstants.CENTER);
        lblVigencia.setForeground(Color.LIGHT_GRAY);
        lblVigencia.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        info.add(lblEtiqueta);
        info.add(lblNombre);
        info.add(lblTipo);
        info.add(lblVigencia);

        card.add(info, BorderLayout.CENTER);
        return card;
    }

    private JLabel crearMensaje(String texto) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        return lbl;
    }

    private String textoVigencia(CapaLogicaNegocio.DTOS.Promociones promocion) {
        String inicio = promocion.getFechaInicio() != null ? formatoFecha.format(promocion.getFechaInicio()) : "—";
        String fin = promocion.getFechaFin() != null ? formatoFecha.format(promocion.getFechaFin()) : "—";
        return "Válida: " + inicio + " a " + fin;
    }

    private String formatoDescuento(Double descuento) {
        if (descuento == null) return "0";
        return String.format("%,.0f", descuento);
    }

    private String textoSeguro(String texto) {
        return texto != null ? texto : "";
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
