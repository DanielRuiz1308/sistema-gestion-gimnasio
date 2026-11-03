package com.gimnasio.vista;

import com.gimnasio.controlador.ClienteControlador;
import com.gimnasio.modelo.Cliente;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ClienteFormulario extends JFrame {

    private JTextField campoNombre;
    private JTextField campoDocumento;
    private JTextField campoTelefono;
    private JTextField campoEmail;
    private JComboBox<String> campoGenero;
    private JTextField campoFechaNacimiento;

    private ClienteControlador controlador = new ClienteControlador();
    private Cliente clienteEnEdicion; // null = modo "registrar nuevo"

    // Constructor para REGISTRAR
    public ClienteFormulario() {
        this(null);
    }

    // Constructor para EDITAR (recibe el cliente a modificar)
    public ClienteFormulario(Cliente cliente) {
        this.clienteEnEdicion = cliente;
        boolean editando = cliente != null;

        EstiloFormulario.configurarVentana(this,
                editando ? "Editar Cliente" : "Registrar Cliente", 520, 580);

        add(EstiloFormulario.cabecera(
                        editando ? "Editar cliente" : "Nuevo cliente",
                        editando ? "Modifica los datos y guarda los cambios"
                                : "Completa los datos para registrar un cliente"),
                BorderLayout.NORTH);

        campoNombre = new JTextField();
        campoDocumento = new JTextField();
        campoTelefono = new JTextField();
        campoEmail = new JTextField();
        campoGenero = new JComboBox<>(new String[]{"M", "F", "Otro"});
        campoFechaNacimiento = new JTextField();

        EstiloFormulario.placeholder(campoNombre, "Nombre y apellido");
        EstiloFormulario.placeholder(campoDocumento, "Número de documento");
        EstiloFormulario.placeholder(campoTelefono, "Ej: 3001234567");
        EstiloFormulario.placeholder(campoEmail, "correo@ejemplo.com");
        EstiloFormulario.placeholder(campoFechaNacimiento, "AAAA-MM-DD");

        JPanel cuerpo = EstiloFormulario.cuerpo();
        EstiloFormulario.agregarCampo(cuerpo, "Nombre completo *", campoNombre);
        EstiloFormulario.agregarFila(cuerpo, "Documento *", campoDocumento, "Teléfono", campoTelefono);
        EstiloFormulario.agregarCampo(cuerpo, "Correo electrónico", campoEmail);
        EstiloFormulario.agregarFila(cuerpo, "Género (M / F / Otro)", campoGenero,
                "Fecha de nacimiento *", campoFechaNacimiento);
        add(cuerpo, BorderLayout.CENTER);

        JButton botonGuardar = new JButton(editando ? "Guardar cambios" : "Registrar cliente");
        botonGuardar.addActionListener(e -> guardarCliente());
        add(EstiloFormulario.pie(this, botonGuardar), BorderLayout.SOUTH);

        // Si estamos editando, precargamos los datos existentes
        if (editando) {
            campoNombre.setText(cliente.getNombre());
            campoDocumento.setText(cliente.getDocumento());
            campoTelefono.setText(cliente.getTelefono());
            campoEmail.setText(cliente.getEmail());
            campoGenero.setSelectedItem(cliente.getGenero());
            campoFechaNacimiento.setText(cliente.getFechaNacimiento().toString());
        }
    }

    private void guardarCliente() {
        String nombre = campoNombre.getText().trim();
        String documento = campoDocumento.getText().trim();

        if (nombre.isEmpty() || documento.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El nombre y el documento son obligatorios.",
                    "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate fechaNacimiento;
        try {
            fechaNacimiento = LocalDate.parse(campoFechaNacimiento.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "La fecha debe tener el formato AAAA-MM-DD (ej: 1998-05-20)",
                    "Fecha inválida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String genero = (String) campoGenero.getSelectedItem();

        try {
            if (clienteEnEdicion == null) {
                Cliente cliente = new Cliente(0, nombre, documento,
                        campoTelefono.getText().trim(), campoEmail.getText().trim(),
                        genero, fechaNacimiento);
                controlador.registrarCliente(cliente);
                JOptionPane.showMessageDialog(this, "Cliente guardado con id: " + cliente.getId());
            } else {
                clienteEnEdicion.setNombre(nombre);
                clienteEnEdicion.setDocumento(documento);
                clienteEnEdicion.setTelefono(campoTelefono.getText().trim());
                clienteEnEdicion.setEmail(campoEmail.getText().trim());
                clienteEnEdicion.setGenero(genero);
                clienteEnEdicion.setFechaNacimiento(fechaNacimiento);
                controlador.actualizarCliente(clienteEnEdicion);
                JOptionPane.showMessageDialog(this, "Cliente actualizado");
            }
            dispose(); // cierra el formulario solo si todo salió bien
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo guardar", JOptionPane.ERROR_MESSAGE);
            // el formulario queda abierto para que el usuario corrija
        }
    }
}