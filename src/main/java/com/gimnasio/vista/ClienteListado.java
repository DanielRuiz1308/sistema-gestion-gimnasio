package com.gimnasio.vista;

import com.gimnasio.controlador.ClienteControlador;
import com.gimnasio.modelo.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class ClienteListado extends JPanel {

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private ClienteControlador controlador = new ClienteControlador();

    public ClienteListado() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(0x0D, 0x0D, 0x0D));

        // Columnas de la tabla (no editable: se modifica con el botón Editar)
        String[] columnas = {"ID", "Nombre", "Documento", "Teléfono", "Email", "Género", "Fecha de nacimiento"};
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
        JLabel titulo = new JLabel("Gestión de Clientes");
        titulo.setForeground(new Color(0xF5, 0xF5, 0xF5));
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(new Color(0x0D, 0x0D, 0x0D));
        encabezado.add(titulo, BorderLayout.NORTH);
        encabezado.add(BuscadorTabla.crear(tabla, "Buscar por nombre, documento, teléfono, email..."),
                BorderLayout.CENTER);
        add(encabezado, BorderLayout.NORTH);

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Panel de botones abajo
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(new Color(0x0D, 0x0D, 0x0D));
        JButton botonNuevo = new JButton("Nuevo Cliente");
        JButton botonActualizar = new JButton("Actualizar lista");
        JButton botonEditar = new JButton("Editar seleccionado");
        JButton botonEliminar = new JButton("Eliminar seleccionado");

        panelBotones.add(botonNuevo);
        panelBotones.add(botonActualizar);
        panelBotones.add(botonEditar);
        panelBotones.add(botonEliminar);
        add(panelBotones, BorderLayout.SOUTH);

        botonNuevo.addActionListener(e -> nuevoCliente());
        botonActualizar.addActionListener(e -> cargarClientes());
        botonEditar.addActionListener(e -> editarSeleccionado());
        botonEliminar.addActionListener(e -> eliminarSeleccionado());

        cargarClientes(); // cargar datos al crear el panel
    }

    private void cargarClientes() {
        modeloTabla.setRowCount(0);

        List<Cliente> clientes = controlador.obtenerTodos();
        for (Cliente c : clientes) {
            modeloTabla.addRow(new Object[]{
                    c.getId(), c.getNombre(), c.getDocumento(), c.getTelefono(), c.getEmail(),
                    c.getGenero(), c.getFechaNacimiento()
            });
        }
    }

    // Devuelve el id del cliente seleccionado, o -1 si no hay ninguno.
    // La tabla puede estar filtrada u ordenada, por eso se convierte la fila
    // de la pantalla a la fila real del modelo antes de leer el id.
    private int idSeleccionado() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista == -1) {
            return -1;
        }
        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        return (int) modeloTabla.getValueAt(filaModelo, 0);
    }

    private void nuevoCliente() {
        ClienteFormulario formulario = new ClienteFormulario();

        formulario.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                cargarClientes();
            }
        });

        formulario.setVisible(true);
    }

    private void editarSeleccionado() {
        int id = idSeleccionado();

        if (id == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente primero");
            return;
        }

        Cliente cliente = controlador.buscarCliente(id);

        ClienteFormulario formulario = new ClienteFormulario(cliente);

        formulario.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                cargarClientes();
            }
        });

        formulario.setVisible(true);
    }

    private void eliminarSeleccionado() {
        int id = idSeleccionado();

        if (id == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente primero");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Seguro que quieres eliminar este cliente?",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                controlador.eliminarCliente(id);
                cargarClientes();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "No se pudo eliminar", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}