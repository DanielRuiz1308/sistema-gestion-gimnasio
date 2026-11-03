package com.gimnasio.vista;

import com.gimnasio.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class Sidebar extends JPanel {

    private static final Color COLOR_FONDO = new Color(0x1A, 0x1A, 0x1A);
    private static final Color COLOR_ACENTO = new Color(0xE3, 0x1E, 0x24);
    private static final Color COLOR_TEXTO = new Color(0xF5, 0xF5, 0xF5);
    private static final Color COLOR_TEXTO_SUAVE = new Color(0xA0, 0xA0, 0xA0);

    private final Usuario usuario;

    public Sidebar(Consumer<String> alSeleccionar, Usuario usuario, Runnable alCerrarSesion) {
        this.usuario = usuario;

        setPreferredSize(new Dimension(220, 0));
        setBackground(COLOR_FONDO);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel logo = new JLabel("LIFTING CLUB", SwingConstants.CENTER);
        logo.setForeground(COLOR_ACENTO);
        logo.setFont(new Font("SansSerif", Font.BOLD, 20));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        add(logo);

        // Solo se agregan los botones de los módulos que el rol del usuario puede abrir
        agregarBoton("Clientes", "clientes", alSeleccionar);
        agregarBoton("Entrenadores", "entrenadores", alSeleccionar);
        agregarBoton("Membresías", "membresias", alSeleccionar);
        agregarBoton("Rutinas", "rutinas", alSeleccionar);
        agregarBoton("Asistencia", "asistencia", alSeleccionar);
        agregarBoton("Registro Físico", "fisico", alSeleccionar);
        agregarBoton("Administradores", "administradores", alSeleccionar);
        agregarBoton("Reportes", "reportes", alSeleccionar);
        agregarBoton("Usuarios", "usuarios", alSeleccionar);

        add(Box.createVerticalGlue()); // empuja lo siguiente hacia abajo

        // Pie del menú: quién está conectado + cerrar sesión
        JLabel etiquetaUsuario = new JLabel(usuario.getNombreCompleto(), SwingConstants.CENTER);
        etiquetaUsuario.setForeground(COLOR_TEXTO);
        etiquetaUsuario.setFont(new Font("SansSerif", Font.BOLD, 13));
        etiquetaUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(etiquetaUsuario);

        JLabel etiquetaRol = new JLabel(usuario.nombreRol(), SwingConstants.CENTER);
        etiquetaRol.setForeground(COLOR_TEXTO_SUAVE);
        etiquetaRol.setFont(new Font("SansSerif", Font.PLAIN, 12));
        etiquetaRol.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(etiquetaRol);

        add(Box.createVerticalStrut(10));
        JButton botonSalir = crearBoton("Cerrar sesión");
        botonSalir.addActionListener(e -> alCerrarSesion.run());
        add(botonSalir);
    }

    private void agregarBoton(String texto, String clave, Consumer<String> alSeleccionar) {
        if (!usuario.puedeAcceder(clave)) {
            return;
        }

        JButton boton = crearBoton(texto);
        boton.addActionListener(e -> alSeleccionar.accept(clave));

        add(Box.createVerticalStrut(5));
        add(boton);
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(200, 45));
        boton.setBackground(COLOR_FONDO);
        boton.setForeground(COLOR_TEXTO);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Al pasar el mouse por encima, se ilumina en rojo
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                boton.setBackground(COLOR_ACENTO);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                boton.setBackground(COLOR_FONDO);
            }
        });

        return boton;
    }
}