package com.gimnasio.vista;

import com.gimnasio.controlador.ClienteControlador;
import com.gimnasio.controlador.EntrenadorControlador;
import com.gimnasio.controlador.RutinaControlador;
import com.gimnasio.modelo.Ejercicio;
import com.gimnasio.modelo.Rutina;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RutinaListado extends JPanel {

    private static final Color FONDO = new Color(0x0D, 0x0D, 0x0D);
    private static final Color TARJETA = new Color(0x1A, 0x1A, 0x1A);
    private static final Color TEXTO = new Color(0xF5, 0xF5, 0xF5);
    private static final Color TEXTO_SUAVE = new Color(0xA0, 0xA0, 0xA0);

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private RutinaControlador controlador = new RutinaControlador();
    private List<Rutina> rutinasActuales; // misma posición que las filas del MODELO de la tabla

    // Detalle de la rutina seleccionada
    private JLabel tituloDetalle;
    private JLabel subtituloDetalle;
    private DefaultTableModel modeloEjercicios;

    // Para mostrar nombres en vez de ids
    private Map<Integer, String> nombresClientes;

    public RutinaListado() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(FONDO);

        // Arriba: lista de rutinas. Abajo: ejercicios de la rutina seleccionada
        String[] columnas = {"Cliente", "Entrenador", "Nombre", "Nivel", "Objetivo", "Ejercicios"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            // La cantidad de ejercicios es numérica para que ordene bien (2 antes que 10)
            @Override
            public Class<?> getColumnClass(int columna) {
                return columna == 5 ? Integer.class : String.class;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(28);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalle();
            }
        });

        // Arriba: título + barra de búsqueda
        JLabel titulo = new JLabel("Gestión de Rutinas");
        titulo.setForeground(TEXTO);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(FONDO);
        encabezado.add(titulo, BorderLayout.NORTH);
        encabezado.add(BuscadorTabla.crear(tabla, "Buscar por cliente, entrenador, rutina, nivel u objetivo..."),
                BorderLayout.CENTER);
        add(encabezado, BorderLayout.NORTH);

        JSplitPane division = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tabla), construirPanelDetalle());
        division.setResizeWeight(0.5);
        division.setBorder(null);
        division.setOpaque(false);
        add(division, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(FONDO);
        JButton botonNueva = new JButton("Nueva Rutina");
        JButton botonActualizar = new JButton("Actualizar lista");
        JButton botonEditar = new JButton("Editar seleccionada");

        panelBotones.add(botonNueva);
        panelBotones.add(botonActualizar);
        panelBotones.add(botonEditar);
        add(panelBotones, BorderLayout.SOUTH);

        botonNueva.addActionListener(e -> nuevaRutina());
        botonActualizar.addActionListener(e -> cargarRutinas());
        botonEditar.addActionListener(e -> editarSeleccionada());

        cargarRutinas();
    }

    private JPanel construirPanelDetalle() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(TARJETA);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JPanel cabecera = new JPanel();
        cabecera.setLayout(new BoxLayout(cabecera, BoxLayout.Y_AXIS));
        cabecera.setBackground(TARJETA);

        tituloDetalle = new JLabel("Selecciona una rutina para ver sus ejercicios");
        tituloDetalle.setForeground(TEXTO);
        tituloDetalle.setFont(new Font("SansSerif", Font.BOLD, 16));

        subtituloDetalle = new JLabel(" ");
        subtituloDetalle.setForeground(TEXTO_SUAVE);
        subtituloDetalle.setFont(new Font("SansSerif", Font.PLAIN, 13));

        cabecera.add(tituloDetalle);
        cabecera.add(Box.createVerticalStrut(4));
        cabecera.add(subtituloDetalle);
        panel.add(cabecera, BorderLayout.NORTH);

        modeloEjercicios = new DefaultTableModel(
                new String[]{"#", "Ejercicio", "Series", "Repeticiones", "Descanso (seg)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tablaEjercicios = new JTable(modeloEjercicios);
        tablaEjercicios.setRowHeight(26);
        panel.add(new JScrollPane(tablaEjercicios), BorderLayout.CENTER);

        return panel;
    }

    // La tabla puede estar filtrada u ordenada: la fila que se ve no es la posición en la lista.
    // Este método convierte la fila de la pantalla a la rutina real (o null si no hay selección).
    private Rutina rutinaSeleccionada() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista == -1 || rutinasActuales == null) {
            return null;
        }
        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        if (filaModelo >= rutinasActuales.size()) {
            return null;
        }
        return rutinasActuales.get(filaModelo);
    }

    private void cargarRutinas() {
        // Recordamos cuál estaba seleccionada para volver a seleccionarla después de recargar
        Rutina anterior = rutinaSeleccionada();
        int idSeleccionada = anterior != null ? anterior.getId() : -1;

        modeloTabla.setRowCount(0);

        nombresClientes = new ClienteControlador().obtenerTodos().stream()
                .collect(Collectors.toMap(c -> c.getId(), c -> c.getNombre()));
        Map<Integer, String> nombresEntrenadores = new EntrenadorControlador().obtenerTodos().stream()
                .collect(Collectors.toMap(e -> e.getId(), e -> e.getNombre()));

        rutinasActuales = controlador.obtenerTodas();

        for (Rutina r : rutinasActuales) {
            modeloTabla.addRow(new Object[]{
                    nombresClientes.getOrDefault(r.getClienteId(), "Desconocido"),
                    nombresEntrenadores.getOrDefault(r.getEntrenadorId(), "Desconocido"),
                    r.getNombre(), r.getNivel(), r.getObjetivo(),
                    r.getEjercicios().size()
            });
        }

        // Volver a seleccionar la misma rutina (o la primera visible, si no había ninguna)
        int filaModeloASeleccionar = -1;
        for (int i = 0; i < rutinasActuales.size(); i++) {
            if (rutinasActuales.get(i).getId() == idSeleccionada) {
                filaModeloASeleccionar = i;
                break;
            }
        }

        int filaVista = -1;
        if (filaModeloASeleccionar != -1) {
            filaVista = tabla.convertRowIndexToView(filaModeloASeleccionar); // -1 si el filtro la oculta
        }
        if (filaVista == -1 && tabla.getRowCount() > 0 && idSeleccionada == -1) {
            filaVista = 0;
        }

        if (filaVista != -1) {
            tabla.setRowSelectionInterval(filaVista, filaVista);
        } else {
            mostrarDetalle();
        }
    }

    // Llena el panel de abajo con los ejercicios de la rutina seleccionada
    private void mostrarDetalle() {
        modeloEjercicios.setRowCount(0);

        Rutina rutina = rutinaSeleccionada();
        if (rutina == null) {
            tituloDetalle.setText("Selecciona una rutina para ver sus ejercicios");
            subtituloDetalle.setText(" ");
            return;
        }

        String cliente = nombresClientes.getOrDefault(rutina.getClienteId(), "Desconocido");

        tituloDetalle.setText(rutina.getNombre() + "  ·  " + cliente);
        subtituloDetalle.setText("Nivel: " + valorONada(rutina.getNivel())
                + "   |   Objetivo: " + valorONada(rutina.getObjetivo())
                + "   |   " + rutina.getEjercicios().size() + " ejercicios");

        int numero = 1;
        for (Ejercicio ej : rutina.getEjercicios()) {
            modeloEjercicios.addRow(new Object[]{
                    numero++, ej.getNombre(), ej.getSeries(), ej.getRepeticiones(), ej.getDescanso()
            });
        }
    }

    private String valorONada(String texto) {
        return (texto == null || texto.isBlank()) ? "-" : texto;
    }

    private void nuevaRutina() {
        RutinaFormulario formulario = new RutinaFormulario();

        formulario.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                cargarRutinas();
            }
        });

        formulario.setVisible(true);
    }

    private void editarSeleccionada() {
        Rutina rutina = rutinaSeleccionada();

        if (rutina == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una rutina primero");
            return;
        }

        RutinaFormulario formulario = new RutinaFormulario(rutina);

        formulario.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                cargarRutinas();
            }
        });

        formulario.setVisible(true);
    }
}