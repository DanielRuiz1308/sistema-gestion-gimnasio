package com.gimnasio.vista;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

// Un JComboBox en el que se puede escribir para buscar.
// Se usa igual que un combo normal: getSelectedItem(), setSelectedIndex(), getItemAt()...
// - Al escribir, la lista se reduce a los elementos que contienen lo escrito
//   (sin importar mayúsculas ni tildes).
// - getSelectedItem() devuelve el elemento elegido, o null si lo escrito no corresponde a uno.
// - cargar(...) reemplaza todos los elementos (para refrescar la lista desde la base de datos).
// - Mientras se filtra al escribir NO se disparan eventos de selección: solo cuando
//   el usuario elige de verdad (clic en la lista, o Enter).
public class ComboBuscable<T> extends JComboBox<T> {

    private List<T> todos = new ArrayList<>();
    private final JTextField editor;
    private boolean ajustando = false;

    public ComboBuscable(T[] elementos) {
        super();
        setEditable(true);
        editor = (JTextField) getEditor().getEditorComponent();
        editor.putClientProperty("JTextField.placeholderText", "Escribe para buscar...");

        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int tecla = e.getKeyCode();
                // Las flechas, Enter, Esc y Tab no cambian el texto: no hay que refiltrar
                if (tecla == KeyEvent.VK_UP || tecla == KeyEvent.VK_DOWN
                        || tecla == KeyEvent.VK_ENTER || tecla == KeyEvent.VK_ESCAPE
                        || tecla == KeyEvent.VK_TAB || tecla == KeyEvent.VK_SHIFT) {
                    return;
                }
                filtrar(editor.getText());
            }
        });

        cargar(Arrays.asList(elementos));
    }

    // Reemplaza todos los elementos del combo (queda seleccionado el primero, como un combo normal)
    public void cargar(Collection<T> elementos) {
        boolean antes = ajustando;
        ajustando = true;
        try {
            todos = new ArrayList<>(elementos);
            setModel(new DefaultComboBoxModel<>(new Vector<>(todos)));
        } finally {
            ajustando = antes;
        }
    }

    // Deja en la lista solo los elementos que contienen el texto escrito
    public void filtrar(String texto) {
        if (ajustando) return;
        ajustando = true;
        try {
            String[] palabras = normalizar(texto).trim().split("\\s+");
            DefaultComboBoxModel<T> modelo = new DefaultComboBoxModel<>();
            for (T item : todos) {
                String nombre = normalizar(String.valueOf(item));
                boolean coincide = true;
                for (String palabra : palabras) {
                    if (!nombre.contains(palabra)) {
                        coincide = false;
                        break;
                    }
                }
                if (coincide) modelo.addElement(item);
            }
            setModel(modelo);
            modelo.setSelectedItem(null);   // no elegir nada solo
            editor.setText(texto);          // conservar lo que el usuario escribió
            editor.setCaretPosition(texto.length());

            if (modelo.getSize() > 0 && isShowing()) {
                setMaximumRowCount(Math.min(8, modelo.getSize()));
                showPopup();
            } else {
                hidePopup();
            }
        } finally {
            ajustando = false;
        }
    }

    // Cantidad de elementos que se están mostrando ahora mismo
    public int cantidadVisible() {
        return getModel().getSize();
    }

    // Mientras se filtra, los cambios internos no avisan a nadie
    @Override
    protected void fireActionEvent() {
        if (!ajustando) super.fireActionEvent();
    }

    @Override
    protected void selectedItemChanged() {
        if (!ajustando) super.selectedItemChanged();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getSelectedItem() {
        Object elegido = super.getSelectedItem();
        if (editor == null) {
            return elegido; // todavía se está construyendo el combo
        }
        if (elegido != null && !(elegido instanceof String)) {
            return elegido; // se eligió un elemento de la lista
        }

        // Si solo se escribió texto: sirve si coincide exacto con un elemento, o si queda uno solo
        String escrito = normalizar(editor.getText()).trim();
        if (escrito.isEmpty()) return null;

        T unico = null;
        int coincidencias = 0;
        for (int i = 0; i < getModel().getSize(); i++) {
            T item = getModel().getElementAt(i);
            if (normalizar(String.valueOf(item)).trim().equals(escrito)) return item;
            unico = item;
            coincidencias++;
        }
        return coincidencias == 1 ? unico : null;
    }

    private static String normalizar(String texto) {
        String sinTildes = Normalizer.normalize(texto == null ? "" : texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return sinTildes.toLowerCase();
    }
}