package com.gimnasio.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

// Piezas visuales compartidas por todas las ventanas de formulario,
// para que se vean iguales (misma marca: negro y rojo).
public class EstiloFormulario {

    public static final Color FONDO = new Color(0x0D, 0x0D, 0x0D);
    public static final Color PANEL = new Color(0x1A, 0x1A, 0x1A);
    public static final Color ACENTO = new Color(0xE3, 0x1E, 0x24);
    public static final Color TEXTO = new Color(0xF5, 0xF5, 0xF5);
    public static final Color TEXTO_SUAVE = new Color(0xA0, 0xA0, 0xA0);

    private static final int ALTO_CAMPO = 42;

    // Ajustes generales de la ventana: tamaño, fondo, centrada, y Esc para cerrar
    public static void configurarVentana(JFrame ventana, String titulo, int ancho, int alto) {
        ventana.setTitle(titulo);
        ventana.setSize(ancho, alto);
        ventana.setResizable(false);
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventana.setLocationRelativeTo(null);
        ventana.getContentPane().setBackground(FONDO);
        ventana.setLayout(new BorderLayout());

        JRootPane raiz = ventana.getRootPane();
        raiz.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "cerrarVentana");
        raiz.getActionMap().put("cerrarVentana", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventana.dispose();
            }
        });
    }

    // Franja de arriba: título grande, subtítulo y una línea roja
    public static JPanel cabecera(String titulo, String subtitulo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, ACENTO),
                BorderFactory.createEmptyBorder(22, 28, 18, 28)));

        JLabel t = new JLabel(titulo);
        t.setForeground(TEXTO);
        t.setFont(new Font("SansSerif", Font.BOLD, 24));
        t.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel s = new JLabel(subtitulo);
        s.setForeground(TEXTO_SUAVE);
        s.setFont(new Font("SansSerif", Font.PLAIN, 13));
        s.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(t);
        panel.add(Box.createVerticalStrut(4));
        panel.add(s);
        return panel;
    }

    // Panel vertical donde se van apilando los campos
    public static JPanel cuerpo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(22, 28, 10, 28));
        return panel;
    }

    // Un campo con su etiqueta arriba
    public static JPanel bloque(String etiqueta, JComponent campo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(FONDO);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l = new JLabel(etiqueta);
        l.setForeground(TEXTO_SUAVE);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        campo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setPreferredSize(new Dimension(100, ALTO_CAMPO));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTO_CAMPO));

        panel.add(l);
        panel.add(Box.createVerticalStrut(5));
        panel.add(campo);
        return panel;
    }

    // Agrega un campo de ancho completo al cuerpo
    public static void agregarCampo(JPanel cuerpo, String etiqueta, JComponent campo) {
        cuerpo.add(bloque(etiqueta, campo));
        cuerpo.add(Box.createVerticalStrut(16));
    }

    // Agrega dos campos lado a lado
    public static void agregarFila(JPanel cuerpo, String etiqueta1, JComponent campo1,
                                   String etiqueta2, JComponent campo2) {
        JPanel fila = new JPanel(new GridLayout(1, 2, 16, 0));
        fila.setBackground(FONDO);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.add(bloque(etiqueta1, campo1));
        fila.add(bloque(etiqueta2, campo2));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTO_CAMPO + 24));

        cuerpo.add(fila);
        cuerpo.add(Box.createVerticalStrut(16));
    }

    // Texto gris dentro del campo cuando está vacío (lo dibuja FlatLaf)
    public static void placeholder(JTextField campo, String texto) {
        campo.putClientProperty("JTextField.placeholderText", texto);
    }

    // Barra de abajo con los botones Cancelar y Guardar
    public static JPanel pie(JFrame ventana, JButton botonPrincipal) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panel.setBackground(FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 28, 24, 28));

        JButton cancelar = new JButton("Cancelar");
        cancelar.setPreferredSize(new Dimension(120, 44));
        cancelar.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cancelar.setFocusPainted(false);
        cancelar.addActionListener(e -> ventana.dispose());

        botonPrincipal.setPreferredSize(new Dimension(160, 44));
        botonPrincipal.setBackground(ACENTO);
        botonPrincipal.setForeground(Color.WHITE);
        botonPrincipal.setFont(new Font("SansSerif", Font.BOLD, 14));
        botonPrincipal.setFocusPainted(false);

        panel.add(cancelar);
        panel.add(botonPrincipal);

        // Enter equivale a presionar el botón principal
        ventana.getRootPane().setDefaultButton(botonPrincipal);
        return panel;
    }
}