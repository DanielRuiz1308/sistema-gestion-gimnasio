package com.gimnasio.vista;

import com.gimnasio.controlador.ClienteControlador;
import com.gimnasio.controlador.MembresiaControlador;
import com.gimnasio.modelo.Cliente;
import com.gimnasio.modelo.Membresia;
import com.gimnasio.modelo.TipoMembresia;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class MembresiaListado extends JPanel {

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private MembresiaControlador membresiaControlador = new MembresiaControlador();
    private ClienteControlador clienteControlador = new ClienteControlador();

    public MembresiaListado() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(0x0D, 0x0D, 0x0D));

        // Tabla no editable; la columna ID es numérica para que ordene bien (2 antes que 10)
        String[] columnas = {"ID Cliente", "Nombre", "Tipo", "Fecha pago", "Fecha vencimiento", "Estado"};
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

        // Arriba: título + barra de búsqueda (sirve también para escribir "vencida", "vigente", "sin")
        JLabel titulo = new JLabel("Gestión de Membresías");
        titulo.setForeground(new Color(0xF5, 0xF5, 0xF5));
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(new Color(0x0D, 0x0D, 0x0D));
        encabezado.add(titulo, BorderLayout.NORTH);
        encabezado.add(BuscadorTabla.crear(tabla, "Buscar por cliente, tipo o estado (vigente, vencida...)"),
                BorderLayout.CENTER);
        add(encabezado, BorderLayout.NORTH);

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(new Color(0x0D, 0x0D, 0x0D));
        JButton botonActualizar = new JButton("Actualizar lista");
        JButton botonRenovar = new JButton("Renovar membresía");

        panelBotones.add(botonActualizar);
        panelBotones.add(botonRenovar);
        add(panelBotones, BorderLayout.SOUTH);

        botonActualizar.addActionListener(e -> cargarMembresias());
        botonRenovar.addActionListener(e -> renovarSeleccionado());

        cargarMembresias();
    }

    private void cargarMembresias() {
        modeloTabla.setRowCount(0);

        List<Cliente> clientes = clienteControlador.obtenerTodos();
        List<TipoMembresia> tipos = membresiaControlador.obtenerTiposMembresia();

        for (Cliente cliente : clientes) {
            Membresia membresia = membresiaControlador.buscarPorCliente(cliente.getId());

            if (membresia == null) {
                modeloTabla.addRow(new Object[]{
                        cliente.getId(), cliente.getNombre(), "-", "-", "-", "Sin membresía"
                });
            } else {
                String nombreTipo = tipos.stream()
                        .filter(t -> t.getId() == membresia.getTipoMembresiaId())
                        .findFirst()
                        .map(TipoMembresia::getNombre)
                        .orElse("Desconocido");

                String estado = membresia.estaVigente() ? "Vigente" : "Vencida";

                modeloTabla.addRow(new Object[]{
                        cliente.getId(), cliente.getNombre(), nombreTipo,
                        String.valueOf(membresia.getFechaPago()),
                        String.valueOf(membresia.getFechaVencimiento()),
                        estado
                });
            }
        }
    }

    private void renovarSeleccionado() {
        int filaVista = tabla.getSelectedRow();

        if (filaVista == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente primero");
            return;
        }

        // La tabla puede estar filtrada u ordenada: se convierte a la fila real del modelo
        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        int clienteId = (int) modeloTabla.getValueAt(filaModelo, 0);
        String nombreCliente = (String) modeloTabla.getValueAt(filaModelo, 1);

        RenovarMembresiaFormulario formulario = new RenovarMembresiaFormulario(clienteId, nombreCliente);

        formulario.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                cargarMembresias();
            }
        });

        formulario.setVisible(true);
    }
}