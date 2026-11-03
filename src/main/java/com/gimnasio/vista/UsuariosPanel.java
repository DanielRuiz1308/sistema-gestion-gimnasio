package com.gimnasio.vista;

import com.gimnasio.controlador.EntrenadorControlador;
import com.gimnasio.controlador.UsuarioControlador;
import com.gimnasio.modelo.Entrenador;
import com.gimnasio.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UsuariosPanel extends JPanel {

    private static final Color FONDO = new Color(0x0D, 0x0D, 0x0D);
    private static final Color TEXTO = new Color(0xF5, 0xF5, 0xF5);

    private UsuarioControlador controlador = new UsuarioControlador();
    private EntrenadorControlador entrenadorControlador = new EntrenadorControlador();
    private final Usuario usuarioActual;

    private JTextField campoNombre;
    private JTextField campoUsuario;
    private JPasswordField campoContrasena;
    private JComboBox<String> comboRol;
    private JComboBox<Entrenador> comboEntrenador;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private List<Usuario> usuariosEnTabla = new ArrayList<>();

    public UsuariosPanel(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(FONDO);

        JPanel arriba = new JPanel(new BorderLayout(5, 10));
        arriba.setBackground(FONDO);
        JLabel titulo = new JLabel("Usuarios del Sistema");
        titulo.setForeground(TEXTO);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        arriba.add(titulo, BorderLayout.NORTH);
        arriba.add(construirFormulario(), BorderLayout.CENTER);
        add(arriba, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Usuario", "Rol", "Entrenador vinculado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel abajo = new JPanel();
        abajo.setBackground(FONDO);
        JButton botonContrasena = new JButton("Cambiar contraseña");
        JButton botonEliminar = new JButton("Eliminar seleccionado");
        botonContrasena.addActionListener(e -> cambiarContrasena());
        botonEliminar.addActionListener(e -> eliminarSeleccionado());
        abajo.add(botonContrasena);
        abajo.add(botonEliminar);
        add(abajo, BorderLayout.SOUTH);

        // Al abrir la pantalla, se recargan la lista de entrenadores y la tabla
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                cargarEntrenadores();
                cargarUsuarios();
            }
        });

        cargarEntrenadores();
        cargarUsuarios();
    }

    private JPanel construirFormulario() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(FONDO);
        panel.setBorder(BorderFactory.createTitledBorder("Nuevo usuario"));

        campoNombre = new JTextField(14);
        campoUsuario = new JTextField(10);
        campoContrasena = new JPasswordField(10);
        comboRol = new JComboBox<>(new String[]{"Recepción", "Entrenador", "Administrador"});
        comboEntrenador = new JComboBox<>();
        comboEntrenador.setEnabled(false);

        // El combo de entrenador solo se activa si el rol elegido es "Entrenador"
        comboRol.addActionListener(e -> {
            boolean esEntrenador = comboRol.getSelectedIndex() == 1;
            comboEntrenador.setEnabled(esEntrenador);
            rellenarNombreDesdeEntrenador();
        });
        comboEntrenador.addActionListener(e -> rellenarNombreDesdeEntrenador());

        JButton botonCrear = new JButton("Crear usuario");
        botonCrear.addActionListener(e -> crearUsuario());

        panel.add(new JLabel("Rol:"));
        panel.add(comboRol);
        panel.add(new JLabel("Entrenador:"));
        panel.add(comboEntrenador);
        panel.add(new JLabel("Nombre:"));
        panel.add(campoNombre);
        panel.add(new JLabel("Usuario:"));
        panel.add(campoUsuario);
        panel.add(new JLabel("Contraseña:"));
        panel.add(campoContrasena);
        panel.add(botonCrear);

        return panel;
    }

    // Si el rol es Entrenador y el nombre está vacío, lo llena con el nombre del entrenador elegido
    private void rellenarNombreDesdeEntrenador() {
        if (comboRol.getSelectedIndex() == 1 && campoNombre.getText().isBlank()) {
            Entrenador e = (Entrenador) comboEntrenador.getSelectedItem();
            if (e != null) {
                campoNombre.setText(e.getNombre());
            }
        }
    }

    private void cargarEntrenadores() {
        comboEntrenador.removeAllItems();
        for (Entrenador e : entrenadorControlador.obtenerTodos()) {
            comboEntrenador.addItem(e);
        }
    }

    private void cargarUsuarios() {
        modeloTabla.setRowCount(0);
        usuariosEnTabla = controlador.obtenerTodos();

        Map<Integer, String> nombresEntrenadores = entrenadorControlador.obtenerTodos().stream()
                .collect(Collectors.toMap(Entrenador::getId, Entrenador::getNombre));

        for (Usuario u : usuariosEnTabla) {
            String vinculado = u.getEntrenadorId() == null
                    ? "-" : nombresEntrenadores.getOrDefault(u.getEntrenadorId(), "-");

            modeloTabla.addRow(new Object[]{
                    u.getId(), u.getNombreCompleto(), u.getNombreUsuario(), u.nombreRol(), vinculado
            });
        }
    }

    private void crearUsuario() {
        String nombre = campoNombre.getText().trim();
        String nombreUsuario = campoUsuario.getText().trim();
        String contrasena = new String(campoContrasena.getPassword());

        if (nombre.isEmpty() || nombreUsuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa nombre, usuario y contraseña.",
                    "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (contrasena.length() < 6) {
            JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 6 caracteres.",
                    "Contraseña muy corta", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String rol;
        Integer entrenadorId = null;

        switch (comboRol.getSelectedIndex()) {
            case 1:
                rol = Usuario.ROL_ENTRENADOR;
                Entrenador entrenador = (Entrenador) comboEntrenador.getSelectedItem();
                if (entrenador == null) {
                    JOptionPane.showMessageDialog(this,
                            "Para crear un usuario entrenador, primero registra al entrenador en el módulo Entrenadores.",
                            "Falta el entrenador", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                entrenadorId = entrenador.getId();
                break;
            case 2:
                rol = Usuario.ROL_ADMIN;
                break;
            default:
                rol = Usuario.ROL_RECEPCION;
        }

        try {
            controlador.crearUsuario(new Usuario(0, nombre, nombreUsuario, rol, entrenadorId), contrasena);
            campoNombre.setText("");
            campoUsuario.setText("");
            campoContrasena.setText("");
            cargarUsuarios();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo crear", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cambiarContrasena() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario primero");
            return;
        }
        Usuario seleccionado = usuariosEnTabla.get(fila);

        JPasswordField campo = new JPasswordField();
        int opcion = JOptionPane.showConfirmDialog(this, campo,
                "Nueva contraseña para " + seleccionado.getNombreUsuario(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        String nueva = new String(campo.getPassword());
        if (nueva.length() < 6) {
            JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 6 caracteres.",
                    "Contraseña muy corta", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            controlador.cambiarContrasena(seleccionado.getId(), nueva);
            JOptionPane.showMessageDialog(this, "Contraseña actualizada");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo cambiar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario primero");
            return;
        }
        Usuario seleccionado = usuariosEnTabla.get(fila);

        if (seleccionado.getId() == usuarioActual.getId()) {
            JOptionPane.showMessageDialog(this, "No puedes eliminar tu propio usuario mientras estás conectado.",
                    "Acción no permitida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Seguro que quieres eliminar al usuario " + seleccionado.getNombreUsuario() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                controlador.eliminarUsuario(seleccionado);
                cargarUsuarios();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "No se pudo eliminar", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}