package CapaPresentacion.GUI_Cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Productos extends JPanel {

    private JPanel panelCatalogo;

    public Productos() {
        inicializarComponentes();
        mostrarPromocionDelDia();
    }

    private void inicializarComponentes() {

        setLayout(new BorderLayout());
        setBackground(new Color(26, 30, 41));
        setPreferredSize(new Dimension(1600, 1000));

        JLabel titulo = new JLabel("CATÁLOGO DE PRODUCTOS");
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        add(titulo, BorderLayout.NORTH);

        panelCatalogo = new JPanel(new GridLayout(2, 3, 20, 20));
        panelCatalogo.setBackground(new Color(26, 30, 41));
        panelCatalogo.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        panelCatalogo.add(crearProducto(
                "Laptop Gamer",
                "$4.500.000"
        ));

        panelCatalogo.add(crearProducto(
                "Mouse RGB",
                "$120.000"
        ));

        panelCatalogo.add(crearProducto(
                "Teclado Mecánico",
                "$280.000"
        ));

        panelCatalogo.add(crearProducto(
                "Monitor 27\"",
                "$1.200.000"
        ));

        panelCatalogo.add(crearProducto(
                "Audífonos",
                "$350.000"
        ));

        panelCatalogo.add(crearProducto(
                "WebCam HD",
                "$180.000"
        ));

        JScrollPane scroll = new JScrollPane(panelCatalogo);
        scroll.setBorder(null);

        add(scroll, BorderLayout.CENTER);
    }

    private JPanel crearProducto(String nombre, String precio) {

        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(new Color(19, 45, 70));
        card.setBorder(BorderFactory.createLineBorder(
                new Color(1, 195, 142), 2));

        JLabel lblNombre = new JLabel(nombre);
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setHorizontalAlignment(SwingConstants.CENTER);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel lblPrecio = new JLabel(precio);
        lblPrecio.setForeground(new Color(1, 195, 142));
        lblPrecio.setHorizontalAlignment(SwingConstants.CENTER);
        lblPrecio.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JButton btnAgregar = new JButton("Agregar al Carrito");
        btnAgregar.setBackground(new Color(1, 128, 95));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFocusPainted(false);

        btnAgregar.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    nombre + " agregado al carrito.",
                    "Carrito",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        info.add(lblNombre);
        info.add(lblPrecio);

        card.add(info, BorderLayout.CENTER);
        card.add(btnAgregar, BorderLayout.SOUTH);

        return card;
    }

    private void mostrarPromocionDelDia() {

        SwingUtilities.invokeLater(() -> {

            JDialog promo = new JDialog();
            promo.setUndecorated(true);
            promo.setSize(350, 120);
            promo.setLocationRelativeTo(this);

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(new Color(1, 128, 95));
            panel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

            JLabel mensaje = new JLabel(
                    "<html><center><b>PROMOCIÓN DEL DÍA</b><br>"
                    + "Haz clic aquí para ver todas las ofertas.</center></html>"
            );

            mensaje.setForeground(Color.WHITE);
            mensaje.setHorizontalAlignment(SwingConstants.CENTER);

            panel.add(mensaje, BorderLayout.CENTER);

            panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {

                    promo.dispose();

                    Promociones promociones = new Promociones();

                    JFrame ventana = new JFrame("Promociones");
                    ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    ventana.setSize(1200, 700);
                    ventana.setLocationRelativeTo(null);
                    ventana.add(promociones);
                    ventana.setVisible(true);
                }
            });

            promo.add(panel);
            promo.setVisible(true);
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setPreferredSize(new java.awt.Dimension(1600, 1000));
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
