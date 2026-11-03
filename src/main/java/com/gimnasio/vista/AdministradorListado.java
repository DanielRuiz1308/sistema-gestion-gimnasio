package com.gimnasio.vista;

import com.gimnasio.controlador.AdministradorControlador;
import com.gimnasio.modelo.Administrador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class AdministradorListado extends JPanel {

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private AdministradorControlador controlador = new AdministradorControlador();

    public AdministradorListado() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(0x0D, 0x0D, 0x0D));

        // Tabla no editable; la columna ID es numérica para que ordene bien (2 antes que 10)
        String[] columnas = {"ID", "Nombre", "Documento", "Teléfono", "Email", "Permisos"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columna) {
                return columna == 0 ? Integer.class : String.class;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(28);

        // Arriba: título + barra de búsqueda
        JLabel titulo = new JLabel("Gestión de Administradores");
        titulo.setForeground(new Color(0xF5, 0xF5, 0xF5));
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(new Color(0x0D, 0x0D, 0x0D));
        encabezado.add(titulo, BorderLayout.NORTH);
        encabezado.add(BuscadorTabla.crear(tabla, "Buscar por nombre, documento, permisos..."),
                BorderLayout.CENTER);
        add(encabezado, BorderLayout.NORTH);

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(new Color(0x0D, 0x0D, 0x0D));
        JButton botonNuevo = new JButton("Nuevo Administrador");
        JButton botonActualizar = new JButton("Actualizar lista");
        JButton botonEditar = new JButton("Editar seleccionado");
        JButton botonEliminar = new JButton("Eliminar seleccionado");

        panelBotones.add(botonNuevo);
        panelBotones.add(botonActualizar);
        panelBotones.add(botonEditar);
        panelBotones.add(botonEliminar);
        add(panelBotones, BorderLayout.SOUTH);

        botonNuevo.addActionListener(e -> nuevoAdministrador());
        botonActualizar.addActionListener(e -> cargarAdministradores());
        botonEditar.addActionListener(e -> editarSeleccionado());
        botonEliminar.addActionListener(e -> eliminarSeleccionado());

        cargarAdministradores();
    }

    private void cargarAdministradores() {
        modeloTabla.setRowCount(0);

        List<Administrador> administradores = controlador.obtenerTodos();
        for (Administrador a : administradores) {
            modeloTabla.addRow(new Object[]{
                    a.getId(), a.getNombre(), a.getDocumento(), a.getTelefono(), a.getEmail(), a.getPermisos()
            });
        }
    }

    // Devuelve el id del administrador seleccionado, o -1 si no hay ninguno.
    // Convierte la fila de la pantalla (filtrada/ordenada) a la fila real del modelo.
    private int idSeleccionado() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista == -1) {
            return -1;
        }
        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        return (int) modeloTabla.getValueAt(filaModelo, 0);
    }

    private void nuevoAdministrador() {
        AdministradorFormulario formulario = new AdministradorFormulario();

        formulario.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                cargarAdministradores();
            }
        });

        formulario.setVisible(true);
    }

    private void editarSeleccionado() {
        int id = idSeleccionado();

        if (id == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un administrador primero");
            return;
        }

        Administrador administrador = controlador.buscarAdministrador(id);

        AdministradorFormulario formulario = new AdministradorFormulario(administrador);

        formulario.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                cargarAdministradores();
            }
        });

        formulario.setVisible(true);
    }

    private void eliminarSeleccionado() {
        int id = idSeleccionado();

        if (id == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un administrador primero");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Seguro que quieres eliminar este administrador?",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                controlador.eliminarAdministrador(id);
                cargarAdministradores();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "No se pudo eliminar", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}