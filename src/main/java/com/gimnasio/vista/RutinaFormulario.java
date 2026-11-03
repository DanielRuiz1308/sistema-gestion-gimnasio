package com.gimnasio.vista;

import com.gimnasio.controlador.ClienteControlador;
import com.gimnasio.controlador.EntrenadorControlador;
import com.gimnasio.controlador.RutinaControlador;
import com.gimnasio.modelo.Cliente;
import com.gimnasio.modelo.Ejercicio;
import com.gimnasio.modelo.Entrenador;
import com.gimnasio.modelo.Rutina;
import com.gimnasio.util.Sesion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RutinaFormulario extends JFrame {

    private ComboBuscable<Cliente> comboCliente;
    private ComboBuscable<Entrenador> comboEntrenador;
    private JTextField campoNombre;
    private JTextField campoNivel;
    private JTextField campoObjetivo;

    private JTextField campoEjercicioNombre;
    private JTextField campoSeries;
    private JTextField campoRepeticiones;
    private JTextField campoDescanso;

    private DefaultTableModel modeloEjercicios;
    private JTable tablaEjercicios;

    private RutinaControlador controlador = new RutinaControlador();
    private Rutina rutinaEnEdicion; // null = modo "nueva rutina"

    // Constructor para CREAR
    public RutinaFormulario() {
        this(null);
    }

    // Constructor para EDITAR
    public RutinaFormulario(Rutina rutina) {
        this.rutinaEnEdicion = rutina;
        boolean editando = rutina != null;

        EstiloFormulario.configurarVentana(this,
                editando ? "Editar Rutina" : "Nueva Rutina", 780, 800);

        add(EstiloFormulario.cabecera(
                        editando ? "Editar rutina" : "Nueva rutina",
                        editando ? "Modifica los datos y los ejercicios de la rutina"
                                : "Define el plan de entrenamiento y sus ejercicios"),
                BorderLayout.NORTH);

        // Centro: datos arriba, ejercicios abajo (la tabla se estira)
        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(EstiloFormulario.FONDO);
        centro.add(construirPanelDatosRutina(), BorderLayout.NORTH);
        centro.add(construirPanelEjercicios(), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        JButton botonGuardarRutina = new JButton(editando ? "Guardar cambios" : "Guardar rutina");
        botonGuardarRutina.addActionListener(e -> guardarRutina());
        add(EstiloFormulario.pie(this, botonGuardarRutina), BorderLayout.SOUTH);

        if (editando) {
            cargarDatosExistentes(rutina);
        }

        // Si quien está conectado es un entrenador, la rutina queda a su nombre y no puede cambiarlo
        if (Sesion.hayEntrenadorConectado() && !editando) {
            seleccionarEntrenador(Sesion.getUsuario().getEntrenadorId());
            comboEntrenador.setEnabled(false);
        }
    }

    private JPanel construirPanelDatosRutina() {
        ClienteControlador clienteControlador = new ClienteControlador();
        EntrenadorControlador entrenadorControlador = new EntrenadorControlador();

        comboCliente = new ComboBuscable<>(clienteControlador.obtenerTodos().toArray(new Cliente[0]));
        comboEntrenador = new ComboBuscable<>(entrenadorControlador.obtenerTodos().toArray(new Entrenador[0]));
        campoNombre = new JTextField();
        campoNivel = new JTextField();
        campoObjetivo = new JTextField();

        EstiloFormulario.placeholder(campoNombre, "Ej: Rutina de hipertrofia");
        EstiloFormulario.placeholder(campoNivel, "Ej: Principiante");
        EstiloFormulario.placeholder(campoObjetivo, "Ej: Ganar masa muscular");

        JPanel cuerpo = EstiloFormulario.cuerpo();
        cuerpo.setBorder(BorderFactory.createEmptyBorder(20, 28, 0, 28));
        EstiloFormulario.agregarFila(cuerpo, "Cliente * (escribe para buscar)", comboCliente,
                "Entrenador *", comboEntrenador);
        EstiloFormulario.agregarCampo(cuerpo, "Nombre de la rutina *", campoNombre);
        EstiloFormulario.agregarFila(cuerpo, "Nivel", campoNivel, "Objetivo", campoObjetivo);
        return cuerpo;
    }

    private JPanel construirPanelEjercicios() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(EstiloFormulario.FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 28, 6, 28));

        JLabel titulo = new JLabel("Ejercicios");
        titulo.setForeground(EstiloFormulario.TEXTO);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0x33, 0x33, 0x33)));
        titulo.setPreferredSize(new Dimension(100, 36));

        // Fila para agregar un ejercicio
        campoEjercicioNombre = new JTextField();
        campoSeries = new JTextField();
        campoRepeticiones = new JTextField();
        campoDescanso = new JTextField();
        EstiloFormulario.placeholder(campoEjercicioNombre, "Ej: Press de banca");
        EstiloFormulario.placeholder(campoSeries, "4");
        EstiloFormulario.placeholder(campoRepeticiones, "12");
        EstiloFormulario.placeholder(campoDescanso, "60");

        JButton botonAgregar = new JButton("Agregar");
        botonAgregar.setBackground(EstiloFormulario.ACENTO);
        botonAgregar.setForeground(Color.WHITE);
        botonAgregar.setFont(new Font("SansSerif", Font.BOLD, 13));
        botonAgregar.setFocusPainted(false);
        botonAgregar.setPreferredSize(new Dimension(100, 42));
        botonAgregar.addActionListener(e -> agregarEjercicioALaLista());
        campoDescanso.addActionListener(e -> agregarEjercicioALaLista()); // Enter agrega

        JPanel fila = new JPanel(new GridBagLayout());
        fila.setBackground(EstiloFormulario.FONDO);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 0, 10);
        c.gridy = 0;
        c.gridx = 0; c.weightx = 1.0;
        fila.add(EstiloFormulario.bloque("Ejercicio", campoEjercicioNombre), c);
        c.gridx = 1; c.weightx = 0.0;
        campoSeries.setPreferredSize(new Dimension(70, 42));
        fila.add(EstiloFormulario.bloque("Series", campoSeries), c);
        c.gridx = 2;
        fila.add(EstiloFormulario.bloque("Reps", campoRepeticiones), c);
        c.gridx = 3;
        fila.add(EstiloFormulario.bloque("Descanso (s)", campoDescanso), c);
        c.gridx = 4; c.insets = new Insets(17, 0, 0, 0);
        fila.add(botonAgregar, c);

        JPanel arriba = new JPanel(new BorderLayout(0, 12));
        arriba.setBackground(EstiloFormulario.FONDO);
        arriba.add(titulo, BorderLayout.NORTH);
        arriba.add(fila, BorderLayout.CENTER);
        panel.add(arriba, BorderLayout.NORTH);

        // Tabla con los ejercicios ya agregados (no editable: se quitan y se vuelven a agregar)
        String[] columnas = {"Ejercicio", "Series", "Repeticiones", "Descanso (seg)"};
        modeloEjercicios = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaEjercicios = new JTable(modeloEjercicios);
        tablaEjercicios.setRowHeight(30);
        tablaEjercicios.setShowGrid(false);
        tablaEjercicios.setIntercellSpacing(new Dimension(0, 0));
        panel.add(new JScrollPane(tablaEjercicios), BorderLayout.CENTER);

        JButton botonQuitar = new JButton("Quitar seleccionado");
        botonQuitar.setFocusPainted(false);
        botonQuitar.addActionListener(e -> quitarEjercicioSeleccionado());
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        abajo.setBackground(EstiloFormulario.FONDO);
        abajo.add(botonQuitar);
        panel.add(abajo, BorderLayout.SOUTH);

        return panel;
    }

    private void agregarEjercicioALaLista() {
        String nombre = campoEjercicioNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El ejercicio necesita un nombre",
                    "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int series = Integer.parseInt(campoSeries.getText().trim());
            int repeticiones = Integer.parseInt(campoRepeticiones.getText().trim());
            int descanso = Integer.parseInt(campoDescanso.getText().trim());

            modeloEjercicios.addRow(new Object[]{nombre, series, repeticiones, descanso});

            // Limpiar los campos para el siguiente ejercicio
            campoEjercicioNombre.setText("");
            campoSeries.setText("");
            campoRepeticiones.setText("");
            campoDescanso.setText("");
            campoEjercicioNombre.requestFocusInWindow();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Series, repeticiones y descanso deben ser números",
                    "Dato inválido", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void quitarEjercicioSeleccionado() {
        int fila = tablaEjercicios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un ejercicio de la tabla",
                    "Nada seleccionado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        modeloEjercicios.removeRow(fila);
    }

    private void guardarRutina() {
        Cliente cliente = (Cliente) comboCliente.getSelectedItem();
        Entrenador entrenador = (Entrenador) comboEntrenador.getSelectedItem();

        if (cliente == null || entrenador == null) {
            JOptionPane.showMessageDialog(this,
                    "Elige un cliente y un entrenador de la lista (escribe para buscarlos)",
                    "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombreRutina = campoNombre.getText().trim();
        if (nombreRutina.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La rutina necesita un nombre",
                    "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (modeloEjercicios.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Agrega al menos un ejercicio antes de guardar",
                    "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Rutina rutina;
        if (rutinaEnEdicion == null) {
            rutina = new Rutina(0, cliente.getId(), entrenador.getId(),
                    nombreRutina, campoNivel.getText().trim(), campoObjetivo.getText().trim());
        } else {
            rutina = rutinaEnEdicion;
            rutina.setEntrenadorId(entrenador.getId());
            rutina.setNombre(nombreRutina);
            rutina.setNivel(campoNivel.getText().trim());
            rutina.setObjetivo(campoObjetivo.getText().trim());
            rutina.getEjercicios().clear();
        }

        // Tomar cada fila de la tabla y convertirla en un Ejercicio
        for (int i = 0; i < modeloEjercicios.getRowCount(); i++) {
            String nombreEj = (String) modeloEjercicios.getValueAt(i, 0);
            int series = (int) modeloEjercicios.getValueAt(i, 1);
            int repeticiones = (int) modeloEjercicios.getValueAt(i, 2);
            int descanso = (int) modeloEjercicios.getValueAt(i, 3);

            rutina.agregarEjercicio(new Ejercicio(0, rutina.getId(), nombreEj, series, repeticiones, descanso));
        }

        try {
            if (rutinaEnEdicion == null) {
                controlador.registrarRutina(rutina);
                JOptionPane.showMessageDialog(this, "Rutina guardada");
            } else {
                controlador.actualizarRutina(rutina);
                JOptionPane.showMessageDialog(this, "Rutina actualizada");
            }
            dispose();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo guardar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatosExistentes(Rutina rutina) {
        campoNombre.setText(rutina.getNombre());
        campoNivel.setText(rutina.getNivel());
        campoObjetivo.setText(rutina.getObjetivo());

        // Mostrar el cliente y el entrenador reales de la rutina (no el primero de la lista)
        for (int i = 0; i < comboCliente.getItemCount(); i++) {
            if (comboCliente.getItemAt(i).getId() == rutina.getClienteId()) {
                comboCliente.setSelectedIndex(i);
                break;
            }
        }
        comboCliente.setEnabled(false); // cada cliente tiene una sola rutina: no se cambia de cliente
        seleccionarEntrenador(rutina.getEntrenadorId());

        for (Ejercicio ejercicio : rutina.getEjercicios()) {
            modeloEjercicios.addRow(new Object[]{
                    ejercicio.getNombre(), ejercicio.getSeries(), ejercicio.getRepeticiones(), ejercicio.getDescanso()
            });
        }
    }

    private void seleccionarEntrenador(int entrenadorId) {
        for (int i = 0; i < comboEntrenador.getItemCount(); i++) {
            if (comboEntrenador.getItemAt(i).getId() == entrenadorId) {
                comboEntrenador.setSelectedIndex(i);
                break;
            }
        }
    }
}