package com.gimnasio.vista;

import com.gimnasio.controlador.AsistenciaControlador;
import com.gimnasio.controlador.ClienteControlador;
import com.gimnasio.controlador.EntrenadorControlador;
import com.gimnasio.modelo.Asistencia;
import com.gimnasio.modelo.Cliente;
import com.gimnasio.modelo.Entrenador;
import com.gimnasio.util.Sesion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AsistenciaPanel extends JPanel {

    private AsistenciaControlador asistenciaControlador = new AsistenciaControlador();
    private ClienteControlador clienteControlador = new ClienteControlador();
    private EntrenadorControlador entrenadorControlador = new EntrenadorControlador();

    private ComboBuscable<Cliente> comboCliente;
    private JComboBox<Entrenador> comboEntrenador;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    // Para mostrar nombres en la tabla en vez de solo ids
    private Map<Integer, String> nombresClientes;
    private Map<Integer, String> nombresEntrenadores;

    public AsistenciaPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(0x0D, 0x0D, 0x0D));

        JPanel arriba = new JPanel(new BorderLayout(5, 10));
        arriba.setBackground(new Color(0x0D, 0x0D, 0x0D));

        JLabel titulo = new JLabel("Registro de Asistencia");
        titulo.setForeground(new Color(0xF5, 0xF5, 0xF5));
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        arriba.add(titulo, BorderLayout.NORTH);
        arriba.add(construirPanelRegistro(), BorderLayout.CENTER);
        add(arriba, BorderLayout.NORTH);

        // Historial: tabla no editable, con buscador encima
        String[] columnas = {"Cliente", "Entrenador", "Fecha", "Hora", "Turno"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(28);

        JPanel historial = new JPanel(new BorderLayout());
        historial.setBackground(new Color(0x0D, 0x0D, 0x0D));
        historial.add(BuscadorTabla.crear(tabla, "Buscar en el historial por cliente, entrenador, fecha o turno..."),
                BorderLayout.NORTH);
        historial.add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(historial, BorderLayout.CENTER);

        // Cada vez que se abre esta pantalla, se recargan clientes, entrenadores e historial
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                cargarCombos();
                cargarAsistencias();
            }
        });

        cargarCombos();
        cargarAsistencias();
    }

    private JPanel construirPanelRegistro() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panel.setBackground(new Color(0x0D, 0x0D, 0x0D));
        panel.setBorder(BorderFactory.createTitledBorder("Registrar nueva asistencia"));

        comboCliente = new ComboBuscable<>(new Cliente[0]);
        comboCliente.setPreferredSize(new Dimension(260, 36));
        comboEntrenador = new JComboBox<>();
        comboEntrenador.setPreferredSize(new Dimension(200, 36));

        JButton botonRegistrar = new JButton("Registrar asistencia");
        botonRegistrar.addActionListener(e -> registrarAsistencia());

        panel.add(new JLabel("Cliente (escribe para buscar):"));
        panel.add(comboCliente);
        panel.add(new JLabel("Entrenador:"));
        panel.add(comboEntrenador);
        panel.add(botonRegistrar);

        return panel;
    }

    private void cargarCombos() {
        comboCliente.cargar(clienteControlador.obtenerTodos());

        comboEntrenador.removeAllItems();
        for (Entrenador e : entrenadorControlador.obtenerTodos()) {
            comboEntrenador.addItem(e);
        }

        // Si quien está conectado es un entrenador, queda seleccionado él mismo y no puede cambiarlo
        if (Sesion.hayEntrenadorConectado()) {
            int miId = Sesion.getUsuario().getEntrenadorId();
            for (int i = 0; i < comboEntrenador.getItemCount(); i++) {
                if (comboEntrenador.getItemAt(i).getId() == miId) {
                    comboEntrenador.setSelectedIndex(i);
                    break;
                }
            }
            comboEntrenador.setEnabled(false);
        } else {
            comboEntrenador.setEnabled(true);
        }
    }

    private void registrarAsistencia() {
        Cliente cliente = (Cliente) comboCliente.getSelectedItem();
        Entrenador entrenador = (Entrenador) comboEntrenador.getSelectedItem();

        if (cliente == null || entrenador == null) {
            JOptionPane.showMessageDialog(this,
                    "Elige un cliente y un entrenador de la lista (escribe para buscarlos)",
                    "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            asistenciaControlador.registrarAsistenciaAhora(cliente.getId(), entrenador.getId());
            JOptionPane.showMessageDialog(this, "Asistencia registrada para " + cliente.getNombre());
            cargarAsistencias();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo registrar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarAsistencias() {
        // Armamos mapas id → nombre, para no repetir consultas por cada fila
        nombresClientes = clienteControlador.obtenerTodos().stream()
                .collect(Collectors.toMap(Cliente::getId, Cliente::getNombre));
        nombresEntrenadores = entrenadorControlador.obtenerTodos().stream()
                .collect(Collectors.toMap(Entrenador::getId, Entrenador::getNombre));

        modeloTabla.setRowCount(0);

        List<Asistencia> asistencias = asistenciaControlador.obtenerTodas();
        for (Asistencia a : asistencias) {
            modeloTabla.addRow(new Object[]{
                    nombresClientes.getOrDefault(a.getClienteId(), "Desconocido"),
                    nombresEntrenadores.getOrDefault(a.getEntrenadorId(), "Desconocido"),
                    a.getFecha(), a.getHora(), a.getTurno()
            });
        }
    }
}