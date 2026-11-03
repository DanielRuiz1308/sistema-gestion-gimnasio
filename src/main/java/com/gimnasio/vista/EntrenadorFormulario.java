package com.gimnasio.vista;

import com.gimnasio.controlador.EntrenadorControlador;
import com.gimnasio.modelo.Entrenador;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class EntrenadorFormulario extends JFrame {

    private JTextField campoNombre;
    private JTextField campoDocumento;
    private JTextField campoTelefono;
    private JTextField campoEmail;
    private JComboBox<String> campoGenero;
    private JTextField campoFechaNacimiento;
    private JTextField campoEspecialidad;

    private EntrenadorControlador controlador = new EntrenadorControlador();
    private Entrenador entrenadorEnEdicion; // null = modo "registrar nuevo"

    // Constructor para REGISTRAR
    public EntrenadorFormulario() {
        this(null);
    }

    // Constructor para EDITAR
    public EntrenadorFormulario(Entrenador entrenador) {
        this.entrenadorEnEdicion = entrenador;
        boolean editando = entrenador != null;

        EstiloFormulario.configurarVentana(this,
                editando ? "Editar Entrenador" : "Registrar Entrenador", 520, 660);

        add(EstiloFormulario.cabecera(
                        editando ? "Editar entrenador" : "Nuevo entrenador",
                        editando ? "Modifica los datos y guarda los cambios"
                                : "Completa los datos para registrar un entrenador"),
                BorderLayout.NORTH);

        campoNombre = new JTextField();
        campoDocumento = new JTextField();
        campoTelefono = new JTextField();
        campoEmail = new JTextField();
        campoGenero = new JComboBox<>(new String[]{"M", "F", "Otro"});
        campoFechaNacimiento = new JTextField();
        campoEspecialidad = new JTextField();

        EstiloFormulario.placeholder(campoNombre, "Nombre y apellido");
        EstiloFormulario.placeholder(campoDocumento, "Número de documento");
        EstiloFormulario.placeholder(campoTelefono, "Ej: 3001234567");
        EstiloFormulario.placeholder(campoEmail, "correo@ejemplo.com");
        EstiloFormulario.placeholder(campoFechaNacimiento, "AAAA-MM-DD");
        EstiloFormulario.placeholder(campoEspecialidad, "Ej: Musculación, Funcional, Crossfit");

        JPanel cuerpo = EstiloFormulario.cuerpo();
        EstiloFormulario.agregarCampo(cuerpo, "Nombre completo *", campoNombre);
        EstiloFormulario.agregarFila(cuerpo, "Documento *", campoDocumento, "Teléfono", campoTelefono);
        EstiloFormulario.agregarCampo(cuerpo, "Correo electrónico", campoEmail);
        EstiloFormulario.agregarFila(cuerpo, "Género (M / F / Otro)", campoGenero,
                "Fecha de nacimiento *", campoFechaNacimiento);
        EstiloFormulario.agregarCampo(cuerpo, "Especialidad", campoEspecialidad);
        add(cuerpo, BorderLayout.CENTER);

        JButton botonGuardar = new JButton(editando ? "Guardar cambios" : "Registrar entrenador");
        botonGuardar.addActionListener(e -> guardarEntrenador());
        add(EstiloFormulario.pie(this, botonGuardar), BorderLayout.SOUTH);

        if (editando) {
            campoNombre.setText(entrenador.getNombre());
            campoDocumento.setText(entrenador.getDocumento());
            campoTelefono.setText(entrenador.getTelefono());
            campoEmail.setText(entrenador.getEmail());
            campoGenero.setSelectedItem(entrenador.getGenero());
            campoFechaNacimiento.setText(entrenador.getFechaNacimiento().toString());
            campoEspecialidad.setText(entrenador.getEspecialidad());
        }
    }

    private void guardarEntrenador() {
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
            if (entrenadorEnEdicion == null) {
                Entrenador entrenador = new Entrenador(0, nombre, documento,
                        campoTelefono.getText().trim(), campoEmail.getText().trim(),
                        genero, fechaNacimiento, campoEspecialidad.getText().trim());
                controlador.registrarEntrenador(entrenador);
                JOptionPane.showMessageDialog(this, "Entrenador guardado con id: " + entrenador.getId());
            } else {
                entrenadorEnEdicion.setNombre(nombre);
                entrenadorEnEdicion.setDocumento(documento);
                entrenadorEnEdicion.setTelefono(campoTelefono.getText().trim());
                entrenadorEnEdicion.setEmail(campoEmail.getText().trim());
                entrenadorEnEdicion.setGenero(genero);
                entrenadorEnEdicion.setFechaNacimiento(fechaNacimiento);
                entrenadorEnEdicion.setEspecialidad(campoEspecialidad.getText().trim());
                controlador.actualizarEntrenador(entrenadorEnEdicion);
                JOptionPane.showMessageDialog(this, "Entrenador actualizado");
            }
            dispose();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo guardar", JOptionPane.ERROR_MESSAGE);
        }
    }
}