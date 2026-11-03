package com.gimnasio.vista;

import com.formdev.flatlaf.FlatDarkLaf;
import com.gimnasio.modelo.Usuario;
import com.gimnasio.util.Sesion;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JFrame {

    private CardLayout cardLayout;
    private JPanel panelContenido;
    private final Usuario usuario;

    public MenuPrincipal(Usuario usuario) {
        this.usuario = usuario;
        Sesion.iniciar(usuario); // para que las demás pantallas sepan quién está conectado

        setTitle("Lifting Club - Sistema de Gimnasio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // pantalla completa
        setLayout(new BorderLayout());

        // El sidebar le avisa a este MenuPrincipal qué panel mostrar
        Sidebar sidebar = new Sidebar(this::mostrarPanel, usuario, this::cerrarSesion);
        add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setBackground(new Color(0x0D, 0x0D, 0x0D));

        // Cada panel se registra con una clave de texto,
        // pero solo si el rol del usuario tiene permiso para abrirlo
        registrar("clientes", ClienteListado::new);
        registrar("entrenadores", EntrenadorListado::new);
        registrar("membresias", MembresiaListado::new);
        registrar("rutinas", RutinaListado::new);
        registrar("asistencia", AsistenciaPanel::new);
        registrar("fisico", RegistroFisicoPanel::new);
        registrar("administradores", AdministradorListado::new);
        registrar("reportes", ReportesPanel::new);
        registrar("usuarios", () -> new UsuariosPanel(usuario));

        add(panelContenido, BorderLayout.CENTER);

        // Pantalla de bienvenida al entrar
        panelContenido.add(crearPanelBienvenida(), "bienvenida");
        cardLayout.show(panelContenido, "bienvenida");
    }

    // "Supplier" = una función que crea el panel solo cuando hace falta
    private void registrar(String clave, java.util.function.Supplier<JPanel> creador) {
        if (usuario.puedeAcceder(clave)) {
            panelContenido.add(creador.get(), clave);
        }
    }

    private void mostrarPanel(String clave) {
        cardLayout.show(panelContenido, clave);
    }

    private void cerrarSesion() {
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Quieres cerrar sesión?", "Cerrar sesión", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            Sesion.cerrar();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private JPanel crearPanelBienvenida() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(0x0D, 0x0D, 0x0D));

        JPanel caja = new JPanel();
        caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));
        caja.setBackground(new Color(0x0D, 0x0D, 0x0D));

        JLabel saludo = new JLabel("Bienvenido, " + usuario.getNombreCompleto());
        saludo.setForeground(new Color(0xF5, 0xF5, 0xF5));
        saludo.setFont(new Font("SansSerif", Font.BOLD, 28));
        saludo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel indicacion = new JLabel("Elige un módulo en el menú de la izquierda");
        indicacion.setForeground(new Color(0xA0, 0xA0, 0xA0));
        indicacion.setFont(new Font("SansSerif", Font.PLAIN, 15));
        indicacion.setAlignmentX(Component.CENTER_ALIGNMENT);

        caja.add(saludo);
        caja.add(Box.createVerticalStrut(10));
        caja.add(indicacion);
        panel.add(caja);
        return panel;
    }

    public static void main(String[] args) {
        FlatDarkLaf.setup();

        UIManager.put("@background", "#0D0D0D");
        UIManager.put("@foreground", "#F5F5F5");
        UIManager.put("@accentColor", "#E31E24");
        UIManager.put("Button.background", new Color(0x1A, 0x1A, 0x1A));
        UIManager.put("Button.foreground", new Color(0xF5, 0xF5, 0xF5));
        UIManager.put("Panel.background", new Color(0x0D, 0x0D, 0x0D));

        // El programa ahora arranca en el login
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}