package com.gimnasio.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.LinkedHashMap;
import java.util.Map;

// Gráfico de torta dibujado a mano con Graphics2D (sin librerías externas)
public class GraficoTorta extends JPanel {

    private static final Color FONDO = new Color(0x1A, 0x1A, 0x1A);
    private static final Color TEXTO = new Color(0xF5, 0xF5, 0xF5);
    private static final Color TEXTO_SUAVE = new Color(0xA0, 0xA0, 0xA0);

    // Rojo de la marca primero, luego tonos que contrastan sobre fondo negro
    private static final Color[] COLORES = {
            new Color(0xE3, 0x1E, 0x24),
            new Color(0xF5, 0xF5, 0xF5),
            new Color(0x8A, 0x8A, 0x8A),
            new Color(0x8C, 0x14, 0x18),
            new Color(0x55, 0x55, 0x55)
    };

    private final String titulo;
    private Map<String, Integer> datos = new LinkedHashMap<>();

    public GraficoTorta(String titulo) {
        this.titulo = titulo;
        setBackground(FONDO);
        setPreferredSize(new Dimension(400, 280));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    public void setDatos(Map<String, Integer> datos) {
        this.datos = datos;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();

        g2.setColor(TEXTO);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2.drawString(titulo, 16, 24);

        int total = datos.values().stream().mapToInt(Integer::intValue).sum();
        if (total == 0) {
            g2.setColor(TEXTO_SUAVE);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            String msg = "Sin datos todavía";
            int w = g2.getFontMetrics().stringWidth(msg);
            g2.drawString(msg, (ancho - w) / 2, alto / 2);
            g2.dispose();
            return;
        }

        int diametro = Math.min(alto - 70, (int) (ancho * 0.5));
        diametro = Math.max(diametro, 60);
        int x = 24;
        int y = 44 + ((alto - 60) - diametro) / 2;

        // Porciones
        double inicio = 90; // empieza arriba
        int i = 0;
        for (Map.Entry<String, Integer> e : datos.entrySet()) {
            double angulo = 360.0 * e.getValue() / total;
            g2.setColor(COLORES[i % COLORES.length]);
            g2.fill(new Arc2D.Double(x, y, diametro, diametro, inicio, -angulo, Arc2D.PIE));
            inicio -= angulo;
            i++;
        }

        // Borde entre porciones para que se vean separadas
        g2.setColor(FONDO);
        g2.setStroke(new BasicStroke(2f));
        inicio = 90;
        for (Map.Entry<String, Integer> e : datos.entrySet()) {
            double angulo = 360.0 * e.getValue() / total;
            g2.draw(new Arc2D.Double(x, y, diametro, diametro, inicio, -angulo, Arc2D.PIE));
            inicio -= angulo;
        }

        // Leyenda a la derecha
        int lx = x + diametro + 24;
        int ly = y + 20;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        i = 0;
        for (Map.Entry<String, Integer> e : datos.entrySet()) {
            g2.setColor(COLORES[i % COLORES.length]);
            g2.fillRoundRect(lx, ly - 11, 12, 12, 3, 3);
            g2.setColor(TEXTO);
            int porcentaje = (int) Math.round(100.0 * e.getValue() / total);
            g2.drawString(nombreGenero(e.getKey()) + ": " + e.getValue() + " (" + porcentaje + "%)", lx + 20, ly);
            ly += 24;
            i++;
        }

        g2.dispose();
    }

    // En la base se guarda M / F / Otro; en pantalla mostramos el nombre completo
    private String nombreGenero(String clave) {
        switch (clave) {
            case "M": return "Hombres";
            case "F": return "Mujeres";
            default:  return clave;
        }
    }
}