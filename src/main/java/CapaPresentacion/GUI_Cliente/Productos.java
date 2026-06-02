package CapaPresentacion.GUI_Cliente;

import CapaLogicaNegocio.Controlador.CatalogoControlador;
import CapaLogicaNegocio.Controlador.PromocionClienteControlador;
import CapaLogicaNegocio.Controlador.RespuestaControlador;
import CapaLogicaNegocio.Logica_Negocio.Carrito;
import CapaLogicaNegocio.Logica_Negocio.Producto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Panel del catalogo de productos del modulo Cliente.
 * Carga los productos UNICAMENTE desde la nube a traves de
 * {@link CatalogoControlador} y permite
 * agregar productos (con cantidad) al carrito compartido. La vista solo conoce
 * el controlador.
 *
 * @author Santiago Lopez
 */
public class Productos extends JPanel {

    private static final Color COLOR_BG = new Color(26, 30, 41);
    private static final Color COLOR_CARD = new Color(19, 45, 70);
    private static final Color COLOR_ACENTO = new Color(1, 195, 142);
    private static final Color COLOR_BOTON = new Color(1, 128, 95);
    private static final Color COLOR_NAV = new Color(0x13, 0x2D, 0x46);

    private final transient CatalogoControlador catalogoControlador = new CatalogoControlador();
    private final transient PromocionClienteControlador promocionControlador = new PromocionClienteControlador();
    private final transient Carrito carrito;

    private transient Runnable onVerPromociones;

    // Descuentos vigentes por producto (idProducto -> %), recargados junto con el
    // catalogo.
    private transient Map<String, Double> descuentos = new HashMap<>();

    private JPanel panelCatalogo;

    public Productos(Carrito carrito) {
        this.carrito = carrito;
        inicializarComponentes();
        cargarCatalogo();
    }

    /**
     * Permite a la ventana principal definir que pasa al pulsar el banner de
     * promociones
     * (por ejemplo, navegar a la pestania de Promociones).
     */
    public void setOnVerPromociones(Runnable onVerPromociones) {
        this.onVerPromociones = onVerPromociones;
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);
        setPreferredSize(new Dimension(1600, 1000));

        add(crearEncabezado(), BorderLayout.NORTH);

        panelCatalogo = new JPanel(new GridLayout(0, 3, 20, 20));
        panelCatalogo.setBackground(COLOR_BG);
        panelCatalogo.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(COLOR_BG);
        wrapper.add(panelCatalogo, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COLOR_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);
    }

    private JPanel crearEncabezado() {
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(COLOR_BG);
        cabecera.setBorder(BorderFactory.createEmptyBorder(20, 40, 0, 40));

        JLabel titulo = new JLabel("CATÁLOGO DE PRODUCTOS");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        cabecera.add(titulo, BorderLayout.WEST);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnActualizar.setBackground(COLOR_BOTON);
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.addActionListener(e -> cargarCatalogo());
        cabecera.add(btnActualizar, BorderLayout.EAST);

        // Banner clickeable de promociones (reemplaza el popup intrusivo anterior).
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(COLOR_BOTON);
        banner.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        banner.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblBanner = new JLabel(
                "<html><b>PROMOCIONES DEL DÍA</b> &nbsp;·&nbsp; Haz clic aquí para ver todas las ofertas vigentes.</html>");
        lblBanner.setForeground(Color.WHITE);
        lblBanner.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        banner.add(lblBanner, BorderLayout.CENTER);

        banner.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onVerPromociones != null)
                    onVerPromociones.run();
            }
        });

        JPanel contenedorBanner = new JPanel(new BorderLayout());
        contenedorBanner.setBackground(COLOR_BG);
        contenedorBanner.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        contenedorBanner.add(banner, BorderLayout.CENTER);

        JPanel norte = new JPanel(new BorderLayout());
        norte.setBackground(COLOR_BG);
        norte.add(cabecera, BorderLayout.NORTH);
        norte.add(contenedorBanner, BorderLayout.SOUTH);
        return norte;
    }

    /**
     * Carga el catalogo desde la nube en segundo plano (SwingWorker) para no
     * congelar la interfaz
     * mientras se consulta Firebase, y luego pinta las tarjetas en el hilo de la
     * UI.
     */
    public void cargarCatalogo() {
        panelCatalogo.removeAll();
        panelCatalogo.add(crearMensaje("Cargando catálogo..."));
        panelCatalogo.revalidate();
        panelCatalogo.repaint();

        new SwingWorker<RespuestaControlador<ArrayList<Producto>>, Void>() {
            @Override
            protected RespuestaControlador<ArrayList<Producto>> doInBackground() {
                // Se traen los descuentos vigentes por producto junto con el catalogo (ambos
                // desde la nube).
                RespuestaControlador<HashMap<String, Double>> resp = promocionControlador
                        .obtenerDescuentosPorProducto();
                descuentos = (resp != null && resp.dato() != null) ? resp.dato() : new HashMap<>();
                return catalogoControlador.listarCatalogo();
            }

            @Override
            protected void done() {
                try {
                    mostrarProductos(get());
                } catch (Exception e) {
                    panelCatalogo.removeAll();
                    panelCatalogo.add(crearMensaje("No se pudo cargar el catálogo."));
                    panelCatalogo.revalidate();
                    panelCatalogo.repaint();
                }
            }
        }.execute();
    }

    private void mostrarProductos(RespuestaControlador<ArrayList<Producto>> respuesta) {
        panelCatalogo.removeAll();

        if (respuesta == null || !respuesta.exito() || respuesta.dato() == null || respuesta.dato().isEmpty()) {
            String mensaje = (respuesta != null && respuesta.mensaje() != null)
                    ? respuesta.mensaje()
                    : "No hay productos disponibles.";
            panelCatalogo.add(crearMensaje(mensaje));
        } else {
            for (Producto producto : respuesta.dato()) {
                panelCatalogo.add(crearTarjetaProducto(producto));
            }
        }

        panelCatalogo.revalidate();
        panelCatalogo.repaint();
    }

    private JPanel crearTarjetaProducto(Producto producto) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createLineBorder(COLOR_ACENTO, 2));
        card.setPreferredSize(new Dimension(300, 210));

        JPanel info = new JPanel(new GridLayout(0, 1, 0, 4));
        info.setOpaque(false);
        info.setBorder(BorderFactory.createEmptyBorder(12, 12, 6, 12));

        JLabel lblNombre = new JLabel(textoSeguro(producto.getNombre()));
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel lblMarca = new JLabel(textoSeguro(producto.getMarca()));
        lblMarca.setForeground(Color.LIGHT_GRAY);
        lblMarca.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Si el producto tiene una oferta vigente, se cobra el precio con descuento: se
        // aplica al
        // propio objeto Producto para que ese precio fluya al carrito y a la venta sin
        // tocar el
        // CompraControlador. Se conserva el precio original solo para mostrarlo
        // tachado.
        Double descuento = descuentos.get(producto.getId());
        boolean tienePromo = descuento != null && descuento > 0 && producto.getPrecioActual() != null;
        Double precioOriginal = producto.getPrecioActual();
        if (tienePromo) {
            double factor = Math.max(0.0, 1.0 - (descuento / 100.0));
            producto.setPrecioActual(precioOriginal * factor);
        }

        info.add(lblNombre);
        info.add(lblMarca);

        if (tienePromo) {
            JLabel lblAntes = new JLabel("<html><strike>" + formatoPrecio(precioOriginal) + "</strike>  "
                    + "<font color='#F59E0B'>-" + String.format("%,.0f", descuento) + "%</font></html>");
            lblAntes.setForeground(Color.LIGHT_GRAY);
            lblAntes.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            JLabel lblPrecio = new JLabel(formatoPrecio(producto.getPrecioActual()));
            lblPrecio.setForeground(COLOR_ACENTO);
            lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 20));

            info.add(lblAntes);
            info.add(lblPrecio);
        } else {
            JLabel lblPrecio = new JLabel(formatoPrecio(producto.getPrecioActual()));
            lblPrecio.setForeground(COLOR_ACENTO);
            lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 20));
            info.add(lblPrecio);
        }

        long stock = producto.getStock() != null ? producto.getStock() : 0L;
        JLabel lblStock = new JLabel(stock > 0 ? "Disponibles: " + stock : "Agotado");
        lblStock.setForeground(stock > 0 ? Color.LIGHT_GRAY : new Color(0xE7, 0x4C, 0x3C));
        lblStock.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        info.add(lblStock);

        JPanel acciones = new JPanel(new BorderLayout(8, 0));
        acciones.setOpaque(false);
        acciones.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));

        int maxCantidad = (int) Math.max(1L, Math.min(stock, 9999L));
        JSpinner spinnerCantidad = new JSpinner(
                new SpinnerNumberModel(1, 1, maxCantidad, 1));
        spinnerCantidad.setPreferredSize(new Dimension(70, 32));

        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setBackground(COLOR_BOTON);
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFocusPainted(false);
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAgregar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (stock <= 0) {
            btnAgregar.setEnabled(false);
            spinnerCantidad.setEnabled(false);
        }

        btnAgregar.addActionListener(e -> {
            long cantidad = ((Number) spinnerCantidad.getValue()).longValue();
            carrito.agregar(producto, cantidad);
            JOptionPane.showMessageDialog(this,
                    cantidad + " x " + producto.getNombre() + " agregado al carrito.",
                    "Carrito", JOptionPane.INFORMATION_MESSAGE);
        });

        acciones.add(spinnerCantidad, BorderLayout.WEST);
        acciones.add(btnAgregar, BorderLayout.CENTER);

        card.add(info, BorderLayout.CENTER);
        card.add(acciones, BorderLayout.SOUTH);
        return card;
    }

    private JLabel crearMensaje(String texto) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        return lbl;
    }

    private String formatoPrecio(Double precio) {
        if (precio == null)
            return "$0";
        return "$" + String.format("%,.0f", precio);
    }

    private String textoSeguro(String texto) {
        return texto != null ? texto : "";
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

        setPreferredSize(new java.awt.Dimension(1600, 1000));
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
