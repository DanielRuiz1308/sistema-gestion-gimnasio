package com.gimnasio.vista;

import com.gimnasio.controlador.AdministradorControlador;
import com.gimnasio.modelo.Administrador;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AdministradorFormulario extends JFrame {

    private JTextField campoNombre;
    private JTextField campoDocumento;
    private JTextField campoTelefono;
    private JTextField campoEmail;
    private JComboBox<String> campoGenero;
    private JTextField campoFechaNacimiento;
    private JTextField campoPermisos;

    private AdministradorControlador controlador = new AdministradorControlador();
    private Administrador administradorEnEdicion; // null = modo "registrar nuevo"

    // Constructor para REGISTRAR
    public AdministradorFormulario() {
        this(null);
    }

    // Constructor para EDITAR
    public AdministradorFormulario(Administrador administrador) {
        this.administradorEnEdicion = administrador;
        boolean editando = administrador != null;

        EstiloFormulario.configurarVentana(this,
                editando ? "Editar Administrador" : "Registrar Administrador", 520, 660);

        add(EstiloFormulario.cabecera(
                        editando ? "Editar administrador" : "Nuevo administrador",
                        editando ? "Modifica los datos y guarda los cambios"
                                : "Completa los datos para registrar un administrador"),
                BorderLayout.NORTH);

        campoNombre = new JTextField();
        campoDocumento = new JTextField();
        campoTelefono = new JTextField();
        campoEmail = new JTextField();
        campoGenero = new JComboBox<>(new String[]{"M", "F", "Otro"});
        campoFechaNacimiento = new JTextField();
        campoPermisos = new JTextField();

        EstiloFormulario.placeholder(campoNombre, "Nombre y apellido");
        EstiloFormulario.placeholder(campoDocumento, "Número de documento");
        EstiloFormulario.placeholder(campoTelefono, "Ej: 3001234567");
        EstiloFormulario.placeholder(campoEmail, "correo@ejemplo.com");
        EstiloFormulario.placeholder(campoFechaNacimiento, "AAAA-MM-DD");
        EstiloFormulario.placeholder(campoPermisos, "Ej: Total, Solo lectura");

        JPanel cuerpo = EstiloFormulario.cuerpo();
        EstiloFormulario.agregarCampo(cuerpo, "Nombre completo *", campoNombre);
        EstiloFormulario.agregarFila(cuerpo, "Documento *", campoDocumento, "Teléfono", campoTelefono);
        EstiloFormulario.agregarCampo(cuerpo, "Correo electrónico", campoEmail);
        EstiloFormulario.agregarFila(cuerpo, "Género (M / F / Otro)", campoGenero,
                "Fecha de nacimiento *", campoFechaNacimiento);
        EstiloFormulario.agregarCampo(cuerpo, "Permisos", campoPermisos);
        add(cuerpo, BorderLayout.CENTER);

        JButton botonGuardar = new JButton(editando ? "Guardar cambios" : "Registrar administrador");
        botonGuardar.addActionListener(e -> guardarAdministrador());
        add(EstiloFormulario.pie(this, botonGuardar), BorderLayout.SOUTH);

        if (editando) {
            campoNombre.setText(administrador.getNombre());
            campoDocumento.setText(administrador.getDocumento());
            campoTelefono.setText(administrador.getTelefono());
            campoEmail.setText(administrador.getEmail());
            campoGenero.setSelectedItem(administrador.getGenero());
            campoFechaNacimiento.setText(administrador.getFechaNacimiento().toString());
            campoPermisos.setText(administrador.getPermisos());
        }
    }

    private void guardarAdministrador() {
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
            if (administradorEnEdicion == null) {
                Administrador administrador = new Administrador(0, nombre, documento,
                        campoTelefono.getText().trim(), campoEmail.getText().trim(),
                        genero, fechaNacimiento, campoPermisos.getText().trim());
                controlador.registrarAdministrador(administrador);
                JOptionPane.showMessageDialog(this, "Administrador guardado con id: " + administrador.getId());
            } else {
                administradorEnEdicion.setNombre(nombre);
                administradorEnEdicion.setDocumento(documento);
                administradorEnEdicion.setTelefono(campoTelefono.getText().trim());
                administradorEnEdicion.setEmail(campoEmail.getText().trim());
                administradorEnEdicion.setGenero(genero);
                administradorEnEdicion.setFechaNacimiento(fechaNacimiento);
                administradorEnEdicion.setPermisos(campoPermisos.getText().trim());
                controlador.actualizarAdministrador(administradorEnEdicion);
                JOptionPane.showMessageDialog(this, "Administrador actualizado");
            }
            dispose();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo guardar", JOptionPane.ERROR_MESSAGE);
        }
    }
}