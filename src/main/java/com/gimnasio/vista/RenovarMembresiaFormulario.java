package com.gimnasio.vista;

import com.gimnasio.controlador.MembresiaControlador;
import com.gimnasio.modelo.TipoMembresia;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RenovarMembresiaFormulario extends JFrame {

    private JComboBox<TipoMembresia> comboTipo;
    private JComboBox<String> comboMetodoPago;
    private JLabel etiquetaDuracion;

    private MembresiaControlador controlador = new MembresiaControlador();
    private int clienteId;

    public RenovarMembresiaFormulario(int clienteId, String nombreCliente) {
        this.clienteId = clienteId;

        EstiloFormulario.configurarVentana(this, "Renovar membresía - " + nombreCliente, 520, 470);

        add(EstiloFormulario.cabecera("Renovar membresía", nombreCliente), BorderLayout.NORTH);

        List<TipoMembresia> tipos;
        try {
            tipos = controlador.obtenerTiposMembresia();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudieron cargar los planes", JOptionPane.ERROR_MESSAGE);
            tipos = List.of();
        }

        comboTipo = new JComboBox<>(tipos.toArray(new TipoMembresia[0]));
        comboMetodoPago = new JComboBox<>(new String[]{"efectivo", "transferencia", "tarjeta"});

        etiquetaDuracion = new JLabel(" ");
        etiquetaDuracion.setForeground(EstiloFormulario.TEXTO_SUAVE);
        etiquetaDuracion.setFont(new Font("SansSerif", Font.PLAIN, 13));
        etiquetaDuracion.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboTipo.addActionListener(e -> actualizarDuracion());

        JPanel cuerpo = EstiloFormulario.cuerpo();
        EstiloFormulario.agregarCampo(cuerpo, "Tipo de membresía *", comboTipo);
        cuerpo.add(etiquetaDuracion);
        cuerpo.add(Box.createVerticalStrut(20));
        EstiloFormulario.agregarCampo(cuerpo, "Método de pago *", comboMetodoPago);
        add(cuerpo, BorderLayout.CENTER);

        JButton botonConfirmar = new JButton("Confirmar pago");
        botonConfirmar.addActionListener(e -> confirmarPago());
        add(EstiloFormulario.pie(this, botonConfirmar), BorderLayout.SOUTH);

        actualizarDuracion();
    }

    private void actualizarDuracion() {
        TipoMembresia tipo = (TipoMembresia) comboTipo.getSelectedItem();
        if (tipo == null) {
            etiquetaDuracion.setText(" ");
        } else {
            etiquetaDuracion.setText("Duración: " + calcularDias(tipo.getNombre()) + " días");
        }
    }

    private void confirmarPago() {
        TipoMembresia tipoSeleccionado = (TipoMembresia) comboTipo.getSelectedItem();
        if (tipoSeleccionado == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay tipos de membresía disponibles.",
                    "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String metodoPago = (String) comboMetodoPago.getSelectedItem();
        int diasDuracion = calcularDias(tipoSeleccionado.getNombre());

        try {
            controlador.renovarMembresia(clienteId, tipoSeleccionado.getId(), metodoPago, diasDuracion);
            JOptionPane.showMessageDialog(this,
                    "Membresía renovada hasta dentro de " + diasDuracion + " días");
            dispose();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo renovar", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Traduce el nombre del tipo a días de duración
    private int calcularDias(String nombreTipo) {
        String nombre = nombreTipo.toLowerCase();
        if (nombre.contains("trimestral")) return 90;
        if (nombre.contains("anual")) return 365;
        return 30; // mensual, o cualquier otro caso
    }
}