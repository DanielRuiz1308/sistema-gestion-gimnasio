package com.gimnasio.vista;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

// Gráfico de barras dibujado a mano con Graphics2D (sin librerías externas)
public class GraficoBarras extends JPanel {

    private static final Color FONDO = new Color(0x1A, 0x1A, 0x1A);
    private static final Color BARRA = new Color(0xE3, 0x1E, 0x24);
    private static final Color TEXTO = new Color(0xF5, 0xF5, 0xF5);
    private static final Color TEXTO_SUAVE = new Color(0xA0, 0xA0, 0xA0);
    private static final Color LINEA = new Color(0x33, 0x33, 0x33);

    private final String titulo;
    private final boolean esDinero;
    private Map<String, ? extends Number> datos = new LinkedHashMap<>();

    public GraficoBarras(String titulo, boolean esDinero) {
        this.titulo = titulo;
        this.esDinero = esDinero;
        setBackground(FONDO);
        setPreferredSize(new Dimension(400, 280));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    public void setDatos(Map<String, ? extends Number> datos) {
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

        // Título
        g2.setColor(TEXTO);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2.drawString(titulo, 16, 24);

        if (datos.isEmpty() || datos.values().stream().mapToDouble(Number::doubleValue).sum() == 0) {
            g2.setColor(TEXTO_SUAVE);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            String msg = "Sin datos todavía";
            int w = g2.getFontMetrics().stringWidth(msg);
            g2.drawString(msg, (ancho - w) / 2, alto / 2);
            g2.dispose();
            return;
        }

        int margenIzq = 16;
        int margenDer = 16;
        int arriba = 50;
        int abajo = 34;
        int areaAncho = ancho - margenIzq - margenDer;
        int areaAlto = alto - arriba - abajo;

        double max = datos.values().stream().mapToDouble(Number::doubleValue).max().orElse(1);
        if (max <= 0) {
            max = 1;
        }

        // Línea base
        g2.setColor(LINEA);
        g2.drawLine(margenIzq, arriba + areaAlto, margenIzq + areaAncho, arriba + areaAlto);

        int n = datos.size();
        double espacio = (double) areaAncho / n;
        int anchoBarra = (int) Math.max(8, Math.min(60, espacio * 0.6));

        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();

        int i = 0;
        for (Map.Entry<String, ? extends Number> e : datos.entrySet()) {
            double valor = e.getValue().doubleValue();
            int altoBarra = (int) Math.round((valor / max) * (areaAlto - 8));
            int x = (int) Math.round(margenIzq + i * espacio + (espacio - anchoBarra) / 2);
            int y = arriba + areaAlto - altoBarra;

            if (altoBarra > 0) {
                g2.setColor(BARRA);
                g2.fillRoundRect(x, y, anchoBarra, altoBarra, 6, 6);
            }

            // Valor encima de la barra
            String textoValor = formatear(valor);
            g2.setColor(TEXTO);
            int wv = fm.stringWidth(textoValor);
            g2.drawString(textoValor, x + (anchoBarra - wv) / 2, y - 5);

            // Etiqueta debajo
            g2.setColor(TEXTO_SUAVE);
            String etiqueta = e.getKey();
            int we = fm.stringWidth(etiqueta);
            g2.drawString(etiqueta, x + (anchoBarra - we) / 2, arriba + areaAlto + 16);

            i++;
        }

        g2.dispose();
    }

    private String formatear(double valor) {
        if (!esDinero) {
            return String.valueOf((int) valor);
        }
        if (valor >= 1_000_000) {
            return String.format("$%.1fM", valor / 1_000_000);
        }
        if (valor >= 1_000) {
            return String.format("$%.0fK", valor / 1_000);
        }
        return String.format("$%.0f", valor);
    }
}