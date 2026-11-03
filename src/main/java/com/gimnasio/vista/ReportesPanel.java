package com.gimnasio.vista;

import com.gimnasio.controlador.PagoControlador;
import com.gimnasio.controlador.ReporteControlador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.LocalDate;
import java.util.List;

public class ReportesPanel extends JPanel {

    private static final Color FONDO = new Color(0x0D, 0x0D, 0x0D);
    private static final Color TARJETA = new Color(0x1A, 0x1A, 0x1A);
    private static final Color ACENTO = new Color(0xE3, 0x1E, 0x24);
    private static final Color TEXTO = new Color(0xF5, 0xF5, 0xF5);
    private static final Color TEXTO_SUAVE = new Color(0xA0, 0xA0, 0xA0);

    private ReporteControlador reporteControlador = new ReporteControlador();
    private PagoControlador pagoControlador = new PagoControlador();

    // Tarjetas de resumen
    private JLabel valorClientes;
    private JLabel valorVigentes;
    private JLabel valorVencidas;
    private JLabel valorIngresosMes;

    // Gráficos
    private GraficoTorta graficoGenero;
    private GraficoBarras graficoEdad;
    private GraficoBarras graficoIngresosMes;
    private GraficoBarras graficoIngresosTipo;
    private GraficoBarras graficoTurno;

    private JTextField campoAnio;

    // Tabla de membresías por vencer
    private JComboBox<String> comboDias;
    private DefaultTableModel modeloVencer;

    public ReportesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(FONDO);

        // Encabezado: título + botón actualizar
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(FONDO);
        JLabel titulo = new JLabel("Reportes del Gimnasio");
        titulo.setForeground(TEXTO);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        encabezado.add(titulo, BorderLayout.WEST);
        JButton botonActualizar = new JButton("Actualizar reportes");
        botonActualizar.addActionListener(e -> cargarTodo());
        encabezado.add(botonActualizar, BorderLayout.EAST);
        add(encabezado, BorderLayout.NORTH);

        // Contenido que se desplaza si la pantalla es pequeña
        JPanel contenido = new JPanel(new BorderLayout(0, 15));
        contenido.setBackground(FONDO);
        contenido.add(construirTarjetas(), BorderLayout.NORTH);
        contenido.add(construirGraficos(), BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // Cada vez que se abre esta pantalla, se recalculan los números
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                cargarTodo();
            }
        });

        cargarTodo();
    }

    // ---------- Construcción de la interfaz ----------

    private JPanel construirTarjetas() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBackground(FONDO);
        panel.setPreferredSize(new Dimension(0, 90));

        valorClientes = new JLabel("0");
        valorVigentes = new JLabel("0");
        valorVencidas = new JLabel("0");
        valorIngresosMes = new JLabel("$0");

        panel.add(crearTarjeta("Clientes registrados", valorClientes));
        panel.add(crearTarjeta("Membresías vigentes", valorVigentes));
        panel.add(crearTarjeta("Membresías vencidas", valorVencidas));
        panel.add(crearTarjeta("Ingresos de este mes", valorIngresosMes));

        return panel;
    }

    private JPanel crearTarjeta(String texto, JLabel valor) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 4));
        tarjeta.setBackground(TARJETA);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, ACENTO),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        JLabel etiqueta = new JLabel(texto);
        etiqueta.setForeground(TEXTO_SUAVE);
        etiqueta.setFont(new Font("SansSerif", Font.PLAIN, 13));

        valor.setForeground(TEXTO);
        valor.setFont(new Font("SansSerif", Font.BOLD, 28));

        tarjeta.add(etiqueta, BorderLayout.NORTH);
        tarjeta.add(valor, BorderLayout.CENTER);
        return tarjeta;
    }

    private JPanel construirGraficos() {
        graficoGenero = new GraficoTorta("Clientes por género");
        graficoEdad = new GraficoBarras("Clientes por rango de edad", false);
        graficoIngresosMes = new GraficoBarras("Ingresos por mes", true);
        graficoIngresosTipo = new GraficoBarras("Ingresos por tipo de membresía", true);
        graficoTurno = new GraficoBarras("Asistencias por turno", false);

        JPanel panel = new JPanel(new GridLayout(3, 2, 15, 15));
        panel.setBackground(FONDO);
        panel.setPreferredSize(new Dimension(0, 3 * 290 + 30));

        panel.add(graficoGenero);
        panel.add(graficoEdad);
        panel.add(envolverConAnio(graficoIngresosMes));
        panel.add(graficoIngresosTipo);
        panel.add(graficoTurno);
        panel.add(construirTablaVencer());

        return panel;
    }

    // Gráfico de ingresos por mes con selector de año arriba
    private JPanel envolverConAnio(GraficoBarras grafico) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(TARJETA);

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        controles.setBackground(TARJETA);
        campoAnio = new JTextField(String.valueOf(LocalDate.now().getYear()), 5);
        JButton botonConsultar = new JButton("Ver año");
        botonConsultar.addActionListener(e -> cargarIngresosMes());
        JLabel etiquetaAnio = new JLabel("Año:");
        etiquetaAnio.setForeground(TEXTO);
        controles.add(etiquetaAnio);
        controles.add(campoAnio);
        controles.add(botonConsultar);

        panel.add(controles, BorderLayout.NORTH);
        panel.add(grafico, BorderLayout.CENTER);
        return panel;
    }

    private JPanel construirTablaVencer() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(TARJETA);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(TARJETA);
        JLabel titulo = new JLabel("Membresías por vencer");
        titulo.setForeground(TEXTO);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        cabecera.add(titulo, BorderLayout.WEST);

        JPanel selector = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        selector.setBackground(TARJETA);
        comboDias = new JComboBox<>(new String[]{"7 días", "15 días", "30 días"});
        comboDias.setSelectedIndex(1);
        comboDias.addActionListener(e -> cargarPorVencer());
        JLabel etiquetaDias = new JLabel("Próximos:");
        etiquetaDias.setForeground(TEXTO);
        selector.add(etiquetaDias);
        selector.add(comboDias);
        cabecera.add(selector, BorderLayout.EAST);
        panel.add(cabecera, BorderLayout.NORTH);

        modeloVencer = new DefaultTableModel(
                new String[]{"Cliente", "Teléfono", "Plan", "Vencimiento", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabla = new JTable(modeloVencer);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        return panel;
    }

    // ---------- Carga de datos ----------

    private void cargarTodo() {
        valorClientes.setText(String.valueOf(reporteControlador.totalClientes()));
        valorVigentes.setText(String.valueOf(reporteControlador.membresiasVigentes()));
        valorVencidas.setText(String.valueOf(reporteControlador.membresiasVencidas()));

        LocalDate hoy = LocalDate.now();
        double ingresosMes = pagoControlador.totalRecaudadoPorMes(hoy.getYear(), hoy.getMonthValue());
        valorIngresosMes.setText(String.format("$%,.0f", ingresosMes));

        graficoGenero.setDatos(pagoControlador.clientesPorGenero());
        graficoEdad.setDatos(pagoControlador.clientesPorRangoEdad());
        graficoIngresosTipo.setDatos(reporteControlador.ingresosPorTipoMembresia());
        graficoTurno.setDatos(reporteControlador.asistenciasPorTurno());

        cargarIngresosMes();
        cargarPorVencer();
    }

    private void cargarIngresosMes() {
        try {
            int anio = Integer.parseInt(campoAnio.getText().trim());
            graficoIngresosMes.setDatos(reporteControlador.ingresosPorMes(anio));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El año debe ser un número válido (ej: 2026)",
                    "Año inválido", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarPorVencer() {
        int[] opciones = {7, 15, 30};
        int dias = opciones[comboDias.getSelectedIndex()];

        modeloVencer.setRowCount(0);
        List<String[]> filas = reporteControlador.membresiasPorVencer(dias);
        for (String[] fila : filas) {
            modeloVencer.addRow(fila);
        }
    }
}