package com.gimnasio.vista;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.Normalizer;

// Barra de búsqueda que se conecta a cualquier JTable:
// filtra mientras se escribe, ignora mayúsculas y tildes,
// y si hay varias palabras, todas deben aparecer en la fila.
public class BuscadorTabla {

    // Crea la barra y deja la tabla lista para filtrar y ordenar.
    public static JPanel crear(JTable tabla, String textoAyuda) {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tabla.getModel());
        tabla.setRowSorter(sorter);

        JTextField campo = new JTextField();
        campo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campo.putClientProperty("JTextField.placeholderText", textoAyuda);
        campo.putClientProperty("JTextField.showClearButton", true);
        campo.setPreferredSize(new Dimension(360, 38));

        JLabel contador = new JLabel(" ");
        contador.setForeground(EstiloFormulario.TEXTO_SUAVE);
        contador.setFont(new Font("SansSerif", Font.PLAIN, 12));

        Runnable aplicar = () -> {
            String[] palabras = normalizar(campo.getText()).trim().split("\\s+");
            if (palabras.length == 1 && palabras[0].isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(new RowFilter<TableModel, Integer>() {
                    @Override
                    public boolean include(Entry<? extends TableModel, ? extends Integer> fila) {
                        StringBuilder texto = new StringBuilder();
                        for (int i = 0; i < fila.getValueCount(); i++) {
                            Object valor = fila.getValue(i);
                            if (valor != null) texto.append(normalizar(valor.toString())).append(' ');
                        }
                        String todo = texto.toString();
                        for (String palabra : palabras) {
                            if (!todo.contains(palabra)) return false;
                        }
                        return true;
                    }
                });
            }
            actualizarContador(tabla, contador, campo.getText().trim().isEmpty());
        };

        campo.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { aplicar.run(); }
            public void removeUpdate(DocumentEvent e) { aplicar.run(); }
            public void changedUpdate(DocumentEvent e) { aplicar.run(); }
        });

        // Si la tabla se recarga (nuevo registro, edición, etc.), el contador se actualiza solo
        tabla.getModel().addTableModelListener(e ->
                SwingUtilities.invokeLater(() ->
                        actualizarContador(tabla, contador, campo.getText().trim().isEmpty())));

        JPanel panel = new JPanel(new BorderLayout(14, 0));
        panel.setBackground(EstiloFormulario.FONDO);
        panel.add(campo, BorderLayout.WEST);
        panel.add(contador, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        actualizarContador(tabla, contador, true);
        return panel;
    }

    private static void actualizarContador(JTable tabla, JLabel contador, boolean sinFiltro) {
        int total = tabla.getModel().getRowCount();
        int visibles = tabla.getRowCount();
        if (sinFiltro) {
            contador.setText(total + (total == 1 ? " registro" : " registros"));
        } else {
            contador.setText(visibles + " de " + total + " resultados");
        }
    }

    // Minúsculas y sin tildes: "José" y "jose" cuentan como iguales
    private static String normalizar(String texto) {
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return sinTildes.toLowerCase();
    }
}