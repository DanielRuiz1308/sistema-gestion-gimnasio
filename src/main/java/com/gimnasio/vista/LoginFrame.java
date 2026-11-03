package com.gimnasio.vista;

import com.gimnasio.controlador.UsuarioControlador;
import com.gimnasio.modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private static final Color FONDO = new Color(0x0D, 0x0D, 0x0D);
    private static final Color PANEL = new Color(0x1A, 0x1A, 0x1A);
    private static final Color ACENTO = new Color(0xE3, 0x1E, 0x24);
    private static final Color TEXTO = new Color(0xF5, 0xF5, 0xF5);
    private static final Color TEXTO_SUAVE = new Color(0xA0, 0xA0, 0xA0);

    private UsuarioControlador controlador = new UsuarioControlador();

    private JTextField campoUsuario;
    private JPasswordField campoContrasena;
    private JLabel mensajeError;

    public LoginFrame() {
        setTitle("Lifting Club - Iniciar sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // pantalla completa, igual que el resto del sistema
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        // Pantalla dividida: marca a la izquierda (60%), formulario a la derecha (40%)
        JPanel raiz = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        gbc.gridx = 0;
        gbc.weightx = 0.6;
        raiz.add(new PanelMarca(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.4;
        raiz.add(construirFormulario(), gbc);

        setContentPane(raiz);
    }

    private JPanel construirFormulario() {
        JPanel lado = new JPanel(new GridBagLayout());
        lado.setBackground(PANEL);

        JPanel caja = new JPanel();
        caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));
        caja.setBackground(PANEL);
        caja.setPreferredSize(new Dimension(380, 430));

        JLabel bienvenida = new JLabel("Iniciar sesión");
        bienvenida.setForeground(TEXTO);
        bienvenida.setFont(new Font("SansSerif", Font.BOLD, 32));
        bienvenida.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel indicacion = new JLabel("Ingresa con tu usuario del gimnasio");
        indicacion.setForeground(TEXTO_SUAVE);
        indicacion.setFont(new Font("SansSerif", Font.PLAIN, 15));
        indicacion.setAlignmentX(Component.LEFT_ALIGNMENT);

        campoUsuario = new JTextField();
        campoUsuario.putClientProperty("JTextField.placeholderText", "Usuario");
        campoContrasena = new JPasswordField();
        campoContrasena.putClientProperty("JTextField.placeholderText", "Contraseña");
        campoContrasena.putClientProperty("JPasswordField.showRevealButton", true);
        for (JComponent c : new JComponent[]{campoUsuario, campoContrasena}) {
            c.setFont(new Font("SansSerif", Font.PLAIN, 16));
            c.setPreferredSize(new Dimension(380, 52));
            c.setMaximumSize(new Dimension(380, 52));
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        mensajeError = new JLabel(" ");
        mensajeError.setForeground(ACENTO);
        mensajeError.setFont(new Font("SansSerif", Font.PLAIN, 13));
        mensajeError.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton botonIngresar = new JButton("INGRESAR");
        botonIngresar.setBackground(ACENTO);
        botonIngresar.setForeground(Color.WHITE);
        botonIngresar.setFont(new Font("SansSerif", Font.BOLD, 16));
        botonIngresar.setFocusPainted(false);
        botonIngresar.setPreferredSize(new Dimension(380, 54));
        botonIngresar.setMaximumSize(new Dimension(380, 54));
        botonIngresar.setAlignmentX(Component.LEFT_ALIGNMENT);
        botonIngresar.addActionListener(e -> iniciarSesion());

        // Enter en cualquier campo equivale a presionar el botón
        getRootPane().setDefaultButton(botonIngresar);

        caja.add(Box.createVerticalGlue());
        caja.add(bienvenida);
        caja.add(Box.createVerticalStrut(8));
        caja.add(indicacion);
        caja.add(Box.createVerticalStrut(40));
        caja.add(campoUsuario);
        caja.add(Box.createVerticalStrut(16));
        caja.add(campoContrasena);
        caja.add(Box.createVerticalStrut(12));
        caja.add(mensajeError);
        caja.add(Box.createVerticalStrut(12));
        caja.add(botonIngresar);
        caja.add(Box.createVerticalGlue());

        lado.add(caja);
        return lado;
    }

    private void iniciarSesion() {
        String usuarioTexto = campoUsuario.getText().trim();
        String contrasena = new String(campoContrasena.getPassword());

        if (usuarioTexto.isEmpty() || contrasena.isEmpty()) {
            mensajeError.setText("Escribe tu usuario y contraseña");
            return;
        }

        try {
            Usuario usuario = controlador.iniciarSesion(usuarioTexto, contrasena);

            if (usuario == null) {
                mensajeError.setText("Usuario o contraseña incorrectos");
                campoContrasena.setText("");
                return;
            }

            dispose();
            new MenuPrincipal(usuario).setVisible(true);

        } catch (RuntimeException ex) {
            mensajeError.setText(ex.getMessage());
        }
    }

    // Panel izquierdo: la marca, dibujada a mano. El tamaño del texto se adapta a la ventana.
    private static class PanelMarca extends JPanel {

        PanelMarca() {
            setBackground(FONDO);
            setPreferredSize(new Dimension(600, 600));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Fondo: degradado muy sutil de negro a rojo oscuro, hacia la esquina inferior
            g2.setPaint(new GradientPaint(0, 0, FONDO, w, h, new Color(0x2B, 0x07, 0x09)));
            g2.fillRect(0, 0, w, h);

            // Franja roja diagonal decorativa
            int[] xs = {(int) (w * 0.62), (int) (w * 0.78), (int) (w * 0.70), (int) (w * 0.54)};
            int[] ys = {h, h, 0, 0};
            g2.setColor(new Color(0xE3, 0x1E, 0x24, 38));
            g2.fillPolygon(xs, ys, 4);
            int[] xs2 = {(int) (w * 0.80), (int) (w * 0.84), (int) (w * 0.76), (int) (w * 0.72)};
            g2.setColor(new Color(0xE3, 0x1E, 0x24, 70));
            g2.fillPolygon(xs2, ys, 4);

            // Texto de marca
            int tamano = Math.max(48, Math.min(w / 6, 120));
            int x = (int) (w * 0.10);
            int base = h / 2;

            g2.setFont(new Font("SansSerif", Font.BOLD, tamano));
            g2.setColor(TEXTO);
            g2.drawString("LIFTING", x, base);

            g2.setColor(ACENTO);
            g2.drawString("CLUB", x, base + (int) (tamano * 1.0));

            // Línea roja y subtítulo
            int yLinea = base + (int) (tamano * 1.0) + 28;
            g2.setColor(ACENTO);
            g2.fillRect(x, yLinea, 90, 5);

            g2.setFont(new Font("SansSerif", Font.PLAIN, Math.max(14, tamano / 5)));
            g2.setColor(TEXTO_SUAVE);
            g2.drawString("SISTEMA DE GESTIÓN DEL GIMNASIO", x, yLinea + Math.max(34, tamano / 3));

            g2.dispose();
        }
    }
}