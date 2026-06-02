package CapaPresentacion.GUI_Admin;

/**
 *
 * @author ASUS
 */
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Diálogo de carga personalizado con diseño de barra lineal luminosa ("Neon
 * Glow"). Dimensión de barra estricta de 100x25 píxeles.
 */
public class LinearLoadingDialog extends JDialog {

    private final int tiempoVisibilidad;
    private Timer timerCierre;
    private BarraNeon barraComponente;

    /**
     * Constructor principal.
     *
     * @param padre              Ventana JFrame que actúa como propietaria (puede
     *                           ser null).
     * @param tiempoMilisegundos Duración en milisegundos antes de cerrarse
     *                           automáticamente.
     */
    public LinearLoadingDialog(Frame padre, int tiempoMilisegundos) {
        super(padre, "Cargando...", true); // Modal por defecto
        this.tiempoVisibilidad = tiempoMilisegundos;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // 1. Configuración del JDialog sin bordes del sistema
        this.setUndecorated(true);

        // TRUCO 1: Hace que la ventana nativa sea completamente transparente
        // De este modo, las esquinas cuadradas del sistema operativo no se verán.
        this.setBackground(new Color(0, 0, 0, 0));

        // Creamos el panel principal que ahora tendrá el fondo negro sólido
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setBackground(Color.BLACK); // El color negro del fondo
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));
        this.setContentPane(panelPrincipal);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        // 2. Instanciar la barra de neón (Dimensión fija de 100x25)
        barraComponente = new BarraNeon();
        Dimension tamanoBarra = new Dimension(200, 25);
        barraComponente.setPreferredSize(tamanoBarra);
        barraComponente.setMinimumSize(tamanoBarra);
        barraComponente.setMaximumSize(tamanoBarra);

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        panelPrincipal.add(barraComponente, gbc);

        // 3. Texto inferior "Loading..." estilizado
        JLabel lblTexto = new JLabel();
        lblTexto.setFont(new Font("Times New Roman", Font.ITALIC | Font.BOLD, 14));
        lblTexto.setText(
                "<html><span style='color: #AADCFF; text-shadow: 0px 0px 5px #0088FF;'>Loading...</span></html>");

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        panelPrincipal.add(lblTexto, gbc);

        // 4. Empaquetar la ventana para calcular las dimensiones antes de redondear
        this.pack();

        // TRUCO 2: Aplicar esquinas redondeadas a la ventana completa
        // 'RoundRectangle2D.Float' recortará el JDialog con la dimensión exacta que
        // calculó pack()
        // El '20, 20' del final controla qué tan redondas quieres las esquinas (puedes
        // subirlo a 30 si quieres más arco)
        this.setShape(new RoundRectangle2D.Float(0, 0, this.getWidth(), this.getHeight(), 20, 20));

        // 5. Centrar en pantalla (Siempre después de pack() y setShape())
        this.setLocationRelativeTo(getOwner());

        // 6. Temporizador de autodestrucción
        timerCierre = new Timer(tiempoVisibilidad, e -> {
            barraComponente.detenerAnimacion();
            this.dispose();
        });
        timerCierre.setRepeats(false);
    }

    /**
     * Hace visible el diálogo e inicia automáticamente las animaciones y el
     * temporizador.
     */
    public void iniciar() {
        if (timerCierre != null && !timerCierre.isRunning()) {
            timerCierre.start();
        }
        // SwingUtilities asegura que la interfaz se dibuje correctamente en su propio
        // hilo de eventos
        SwingUtilities.invokeLater(() -> this.setVisible(true));
    }

    /**
     * Método utilitario estático para mostrar la barra rápidamente con una sola
     * línea de código.
     *
     * @param padre              Ventana JFrame actual desde donde se invoca.
     * @param tiempoMilisegundos Tiempo que durará en pantalla (ej: 5000 para 5
     *                           segundos).
     */
    public static void mostrar(Frame padre, int tiempoMilisegundos) {
        LinearLoadingDialog dialogo = new LinearLoadingDialog(padre, tiempoMilisegundos);
        dialogo.iniciar();
    }

    // =========================================================================
    // COMPONENTE INTERNO: LA BARRA DE PROGRESO DE NEÓN
    // =========================================================================
    private static class BarraNeon extends JComponent {

        private float progreso = 0.0f;
        private final Timer animacionTimer;

        public BarraNeon() {
            // Ciclo infinito para el efecto dinámico "Indeterminate"
            animacionTimer = new Timer(30, e -> {
                progreso += 0.015f;
                if (progreso > 1.0f) {
                    progreso = 0.0f;
                }
                repaint();
            });
            animacionTimer.start();
        }

        public void detenerAnimacion() {
            if (animacionTimer != null && animacionTimer.isRunning()) {
                animacionTimer.stop();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();

            // Suavizado de bordes píxel a píxel
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int radio = h; // Extremos completamente circulares (Cápsula)

            // Colores extraídos de la imagen muestra
            Color colorFondoBarra = new Color(30, 50, 70, 100);
            Color colorBordeNeon = new Color(0, 100, 200, 180);
            Color colorBrilloInterno = Color.WHITE;
            Color colorGlowFrente = new Color(0, 140, 255);

            // Fondo de la cápsula contenedora vacía
            g2.setColor(colorFondoBarra);
            g2.fill(new RoundRectangle2D.Float(2, 2, w - 4, h - 4, radio, radio));

            // Progreso dinámico activo
            int anchoProgreso = (int) ((w - 4) * progreso);
            if (anchoProgreso > radio) {
                // Aura externa de neón
                g2.setColor(colorGlowFrente);
                g2.setStroke(new BasicStroke(4f));
                g2.draw(new RoundRectangle2D.Float(2, 2, anchoProgreso, h - 4, radio, radio));

                // Núcleo blanco sólido
                g2.setColor(colorBrilloInterno);
                g2.fill(new RoundRectangle2D.Float(2, 2, anchoProgreso, h - 4, radio, radio));
            }

            // Contorno perimetral estático
            g2.setColor(colorBordeNeon);
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, radio, radio));

            g2.dispose();
        }

        @Override
        public void removeNotify() {
            detenerAnimacion();
            super.removeNotify();
        }
    }
}
