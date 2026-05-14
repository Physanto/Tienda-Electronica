/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package GUI_Admin;
 
import GUI_Cliente.MenuCliente;
import Helpers.HelperCifrado;
import Helpers.OSHelper;
import Logica_Negocio.Administrador;
import Logica_Negocio.Persona;
import Logica_Negocio.Usuario;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
 
/**
 * Formulario de inicio de sesión.
 * Rediseñado visualmente para coincidir con el estilo de la imagen de referencia:
 * fondo degradado azul, ícono circular oscuro, título espaciado, campos con
 * solo línea inferior y botón ancho de color azul oscuro.
 *
 * @author Santiago Lopez
 */
public class InicioSesionAdministrador extends javax.swing.JFrame {
 
    public String pathc;
    public String s;
    Usuario usuAdmin;
    Usuario usuCliente;
 
    // Paleta de colores centralizada para mantener coherencia visual
    private static final Color COLOR_BOTON       = new Color(0, 0, 255);
    private static final Color COLOR_LINEA       = Color.WHITE;
    private static final Color COLOR_ERROR       = Color.RED;
 
    public InicioSesionAdministrador() {
        initComponents();
        centerFrame();
        aplicarEstilosCampos();   // Estilos visuales de los campos (bordes, colores, transparencia)
        aplicarEstiloBoton();     // Estilo del botón "Iniciar Sesión"
        addPlaceholders();        // Textos de ayuda en los campos vacíos
 
        s = Paths.get("").toAbsolutePath().toString();
        establecerFondo();
        establecerIconoUsuario();
        establecerLogo();
      
        OSHelper.setImage(fondo, "Fondo.png");
        OSHelper.setImage(icono_user, "usuario.png");
        OSHelper.setImage(logo, "LogoF.png");
 
        // Forzar el foco al frame para evitar que un campo quede activo al abrir
        javax.swing.Timer timer = new javax.swing.Timer(100, e -> requestFocusInWindow());
        timer.setRepeats(false);
        timer.start();
    }
 
    /**
     * Centra el JFrame en la pantalla al momento de mostrarse.
     */
    private void centerFrame() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - getWidth()) / 2, (screen.height - getHeight()) / 2);
    }
 
    /**
     * Aplica estilos visuales a los campos de texto y contraseña:
     * fondo transparente, sin borde exterior y línea inferior blanca,
     * alineados con el estilo minimalista de la imagen de referencia.
     */
    private void aplicarEstilosCampos() {
        for (javax.swing.JComponent campo : new javax.swing.JComponent[]{tx_user, tx_passwd}) {
            campo.setOpaque(false);
            campo.setBackground(new Color(0, 0, 0, 0)); // Alpha 0 = completamente transparente
            campo.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_LINEA));
            campo.setForeground(COLOR_LINEA);
            // Fuerza transparencia cuando Nimbus sobreescribe el fondo del campo
            javax.swing.UIManager.put("TextField.background", new Color(0, 0, 0, 0));
            javax.swing.UIManager.put("PasswordField.background", new Color(0, 0, 0, 0));
        }
    }
 
    /**
     * Configura el botón "INICIAR SESIÓN" con el estilo de referencia:
     * color azul oscuro, texto en mayúsculas, sin borde pintado y cursor de mano.
     */
    private void aplicarEstiloBoton() {
        btn_login.setBackground(COLOR_BOTON);
        btn_login.setForeground(COLOR_LINEA);
        btn_login.setFont(new java.awt.Font("Segoe UI Light", java.awt.Font.BOLD, 13));
        btn_login.setText("INICIAR SESIÓN");
        btn_login.setBorderPainted(false);
        btn_login.setFocusPainted(false);
        btn_login.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }
 
    /**
     * Registra los placeholders (texto gris de ayuda) en los campos.
     * El campo de contraseña muestra el texto sin máscara hasta que el usuario escribe.
     */
    private void addPlaceholders() {
        setPlaceholder(tx_user, "Ingresa tu Usuario");
        setPasswordPlaceholder(tx_passwd, "Ingresa tu Contraseña");
    }
 
    /**
     * Placeholder genérico para JTextField: muestra texto blanco al perder foco
     * y lo limpia al ganar foco si el contenido sigue siendo el placeholder.
     */
    private void setPlaceholder(javax.swing.JTextField field, String ph) {
        field.setText(ph);
        field.setForeground(java.awt.Color.WHITE);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(ph)) { field.setText(""); field.setForeground(COLOR_LINEA); }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) { field.setText(ph); field.setForeground(java.awt.Color.WHITE); }
            }
        });
    }
 
    /**
     * Placeholder para JPasswordField: sin máscara al mostrar el texto de ayuda,
     * activa el carácter '•' al ganar foco y lo desactiva si queda vacío.
     */
    private void setPasswordPlaceholder(javax.swing.JPasswordField pass, String ph) {
        pass.setText(ph);
        pass.setForeground(java.awt.Color.WHITE);
        pass.setEchoChar('\0');
        pass.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (String.valueOf(pass.getPassword()).equals(ph)) {
                    pass.setText(""); pass.setEchoChar('•'); pass.setForeground(COLOR_LINEA);
                }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (pass.getPassword().length == 0) {
                    pass.setText(ph); pass.setEchoChar('\0'); pass.setForeground(java.awt.Color.WHITE);
                }
            }
        });
    }
 
    /**
     * Lógica principal de autenticación. Valida entradas, cifra con SHA-256,
     * determina el tipo de usuario (Admin / Persona) y abre el menú correspondiente.
     * Marca los campos con borde rojo si la validación falla.
     */
    public void InicioSesion() {
        String usuario   = tx_user.getText();
        String contrasena = String.valueOf(tx_passwd.getPassword());
 
        if (Helpers.HelperValidacion.ValidarTodo(usuario) != 0
                || Helpers.HelperValidacion.ValidarTodoContrasenha(contrasena) != 0) {
            mostrarErrorCampos();
            return;
        }
 
        String cifrarusu    = HelperCifrado.CifrarSHA256(usuario);
        String cifrarcontra = HelperCifrado.CifrarSHA256(contrasena);
 
        System.out.println("usu ci inter\t"  + cifrarusu);
        System.out.println("usu con inter\t" + cifrarcontra);
 
        if (usuario.equals("Admin")) {
            usuAdmin = new Administrador("1", "Admin", "12345", true);
        } else if (usuario.equals("Persona")) {
            usuCliente = new Persona("2", "Persona", "12345", true);
        } else {
            mostrarErrorCampos();
            return;
        }
 
        if (usuCliente instanceof Persona) {
            if (usuCliente.LogOn(cifrarusu, cifrarcontra)) {
                JOptionPane.showMessageDialog(null, "Bienvenido Persona");
                new MenuCliente().setVisible(true);
                dispose();
            } else {
                mostrarErrorCampos();
            }
        } else if (usuAdmin instanceof Administrador) {
            if (usuAdmin.LogOn(cifrarusu, cifrarcontra)) {
                JOptionPane.showMessageDialog(null, "Bienvenido Administrador");
                new MenuAdministrador().setVisible(true);
                dispose();
            } else {
                mostrarErrorCampos();
            }
        }
    }
 
    /**
     * Muestra el mensaje de error y resalta los campos con borde rojo.
     * Centraliza la lógica repetida de error en un único punto.
     */
    private void mostrarErrorCampos() {
        tx_user.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_ERROR));
        tx_passwd.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_ERROR));
        JOptionPane.showMessageDialog(null, "Usuario o contraseña inválida");      
    }
 
    /**
     * Carga la imagen de fondo desde el sistema de archivos y la asigna al JLabel fondo.
     */
    public void establecerFondo() {
        Image img = null;
        try {
            String pathC = s + "\\Images\\" + "Fondo" + ".png";
            img = ImageIO.read(new File(pathC));
            fondo.setIcon(new ImageIcon(img));
        } catch (IOException ioexception) {
            System.err.println(ioexception);
        }
    }
 
    /**
     * Carga el ícono de usuario desde el sistema de archivos y la asigna al JLabel icono_user.
     */
    public void establecerIconoUsuario() {
        Image img = null;
        try {
            String pathIcono = s + "\\Images\\" + "usuario" + ".png";
            img = ImageIO.read(new File(pathIcono));
            icono_user.setIcon(new ImageIcon(img));
        } catch (IOException ioexception) {
            System.err.println(ioexception);
        }
    }
    
    /**
     * Carga el la imagen del logo desde el sistema de archivos y la asigna al JLabel logo.
     */
    public void establecerLogo() {
        Image img = null;
        try {
            String pathLogo = s + "\\Images\\" + "LogoF" + ".png";
            img = ImageIO.read(new File(pathLogo));
            logo.setIcon(new ImageIcon(img));
        } catch (IOException ioexception) {
            System.err.println(ioexception);
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        icono_user = new javax.swing.JLabel();
        tx_user = new javax.swing.JTextField();
        tx_passwd = new javax.swing.JPasswordField();
        btn_login = new javax.swing.JButton();
        fondo = new javax.swing.JLabel();
        logo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Iniciar Sesion Administrador");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(icono_user, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 50, 120, 110));

        tx_user.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        tx_user.setForeground(new java.awt.Color(0, 0, 0));
        getContentPane().add(tx_user, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 210, 180, -1));

        tx_passwd.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        tx_passwd.setForeground(new java.awt.Color(0, 0, 0));
        tx_passwd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tx_passwdActionPerformed(evt);
            }
        });
        getContentPane().add(tx_passwd, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 260, 180, -1));

        btn_login.setBackground(new java.awt.Color(0, 51, 204));
        btn_login.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        btn_login.setForeground(new java.awt.Color(255, 255, 255));
        btn_login.setText("Iniciar Sesion");
        btn_login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_loginActionPerformed(evt);
            }
        });
        getContentPane().add(btn_login, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 320, 130, 40));
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 370, 440));
        getContentPane().add(logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 0, 370, 430));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tx_passwdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tx_passwdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tx_passwdActionPerformed

    private void btn_loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_loginActionPerformed
        InicioSesion();
    }//GEN-LAST:event_btn_loginActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(InicioSesionAdministrador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InicioSesionAdministrador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InicioSesionAdministrador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InicioSesionAdministrador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InicioSesionAdministrador().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_login;
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel icono_user;
    private javax.swing.JLabel logo;
    private javax.swing.JPasswordField tx_passwd;
    private javax.swing.JTextField tx_user;
    // End of variables declaration//GEN-END:variables
}
