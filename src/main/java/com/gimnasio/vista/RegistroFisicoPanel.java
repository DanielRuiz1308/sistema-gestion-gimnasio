package com.gimnasio.vista;

import com.gimnasio.controlador.ClienteControlador;
import com.gimnasio.controlador.RegistroFisicoControlador;
import com.gimnasio.modelo.Cliente;
import com.gimnasio.modelo.RegistroFisico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class RegistroFisicoPanel extends JPanel {

    private RegistroFisicoControlador controlador = new RegistroFisicoControlador();
    private ClienteControlador clienteControlador = new ClienteControlador();

    private ComboBuscable<Cliente> comboCliente;
    private JTextField campoFecha;
    private JTextField campoPeso;
    private JTextField campoAltura;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private List<Integer> idsFilas = new ArrayList<>(); // id real de cada fila de la tabla

    public RegistroFisicoPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(0x0D, 0x0D, 0x0D));

        JPanel arriba = new JPanel(new BorderLayout(5, 10));
        arriba.setBackground(new Color(0x0D, 0x0D, 0x0D));

        JLabel titulo = new JLabel("Registro Físico");
        titulo.setForeground(new Color(0xF5, 0xF5, 0xF5));
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        arriba.add(titulo, BorderLayout.NORTH);
        arriba.add(construirPanelRegistro(), BorderLayout.CENTER);
        add(arriba, BorderLayout.NORTH);

        String[] columnas = {"Fecha", "Peso (kg)", "Altura (m)", "IMC", "Clasificación"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(28);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel abajo = new JPanel();
        abajo.setBackground(new Color(0x0D, 0x0D, 0x0D));
        JButton botonEliminar = new JButton("Eliminar seleccionado");
        botonEliminar.addActionListener(e -> eliminarSeleccionado());
        abajo.add(botonEliminar);
        add(abajo, BorderLayout.SOUTH);

        // Cada vez que se muestra esta pantalla, recargamos la lista de clientes
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                cargarClientes();
            }
        });

        cargarClientes();
    }

    private JPanel construirPanelRegistro() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(new Color(0x0D, 0x0D, 0x0D));
        panel.setBorder(BorderFactory.createTitledBorder("Nueva medición"));

        // Al elegir un cliente (clic en la lista o Enter) se cargan sus mediciones
        comboCliente = new ComboBuscable<>(new Cliente[0]);
        comboCliente.setPreferredSize(new Dimension(260, 36));
        comboCliente.addActionListener(e -> cargarRegistros());

        campoFecha = new JTextField(LocalDate.now().toString(), 9);
        campoPeso = new JTextField(5);
        campoAltura = new JTextField(5);

        JButton botonRegistrar = new JButton("Registrar medición");
        botonRegistrar.addActionListener(e -> registrar());

        panel.add(new JLabel("Cliente (escribe para buscar):"));
        panel.add(comboCliente);
        panel.add(new JLabel("Fecha (AAAA-MM-DD):"));
        panel.add(campoFecha);
        panel.add(new JLabel("Peso (kg):"));
        panel.add(campoPeso);
        panel.add(new JLabel("Altura (m):"));
        panel.add(campoAltura);
        panel.add(botonRegistrar);

        return panel;
    }

    private void cargarClientes() {
        Cliente seleccionado = (Cliente) comboCliente.getSelectedItem();

        comboCliente.cargar(clienteControlador.obtenerTodos());

        // Si había uno elegido, lo dejamos elegido
        if (seleccionado != null) {
            for (int i = 0; i < comboCliente.getItemCount(); i++) {
                if (comboCliente.getItemAt(i).getId() == seleccionado.getId()) {
                    comboCliente.setSelectedIndex(i);
                    break;
                }
            }
        }
        cargarRegistros();
    }

    private void cargarRegistros() {
        modeloTabla.setRowCount(0);
        idsFilas.clear();

        Cliente cliente = (Cliente) comboCliente.getSelectedItem();
        if (cliente == null) {
            return;
        }

        for (RegistroFisico r : controlador.obtenerPorCliente(cliente.getId())) {
            modeloTabla.addRow(new Object[]{
                    r.getFecha(),
                    String.format("%.1f", r.getPeso()),
                    String.format("%.2f", r.getAltura()),
                    String.format("%.1f", r.calcularImc()),
                    r.clasificarImc()
            });
            idsFilas.add(r.getId());
        }
    }

    private void registrar() {
        Cliente cliente = (Cliente) comboCliente.getSelectedItem();
        if (cliente == null) {
            JOptionPane.showMessageDialog(this,
                    "Elige un cliente de la lista (escribe para buscarlo)",
                    "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(campoFecha.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "La fecha debe tener el formato AAAA-MM-DD (ej: 2026-10-15)",
                    "Fecha inválida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double peso;
        double altura;
        try {
            peso = Double.parseDouble(campoPeso.getText().trim().replace(',', '.'));
            altura = Double.parseDouble(campoAltura.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "El peso y la altura deben ser números (ej: 72.5 y 1.75)",
                    "Datos inválidos", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (peso < 20 || peso > 300) {
            JOptionPane.showMessageDialog(this, "El peso debe estar entre 20 y 300 kg",
                    "Datos inválidos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (altura < 0.5 || altura > 2.5) {
            JOptionPane.showMessageDialog(this, "La altura debe estar entre 0.5 y 2.5 metros (ej: 1.75)",
                    "Datos inválidos", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            controlador.registrar(cliente.getId(), fecha, peso, altura);
            campoPeso.setText("");
            campoAltura.setText("");
            cargarRegistros();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo guardar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una medición primero");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Seguro que quieres eliminar esta medición?",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                controlador.eliminar(idsFilas.get(fila));
                cargarRegistros();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "No se pudo eliminar", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}