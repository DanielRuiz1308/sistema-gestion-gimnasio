package com.gimnasio.dao.impl;

import com.gimnasio.dao.ReporteDAO;
import com.gimnasio.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReporteDAOImpl implements ReporteDAO {

    private static final String[] MESES = {"Ene", "Feb", "Mar", "Abr", "May", "Jun",
            "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};

    @Override
    public int contarClientes() {
        return contar("SELECT COUNT(*) FROM cliente");
    }

    @Override
    public int contarMembresiasVigentes() {
        return contar("SELECT COUNT(*) FROM membresia WHERE fecha_vencimiento >= CURDATE()");
    }

    @Override
    public int contarMembresiasVencidas() {
        return contar("SELECT COUNT(*) FROM membresia WHERE fecha_vencimiento < CURDATE()");
    }

    // Método auxiliar: ejecuta un SELECT COUNT(*) y devuelve el número
    private int contar(String sql) {
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Error al contar: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public Map<String, Double> ingresosPorMes(int anio) {
        // Arrancamos con los 12 meses en 0, para que el gráfico siempre muestre el año completo
        Map<String, Double> resultado = new LinkedHashMap<>();
        for (String mes : MESES) {
            resultado.put(mes, 0.0);
        }

        String sql = "SELECT MONTH(fecha_pago) AS mes, SUM(monto) AS total " +
                "FROM pago WHERE YEAR(fecha_pago) = ? GROUP BY MONTH(fecha_pago)";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, anio);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                resultado.put(MESES[rs.getInt("mes") - 1], rs.getDouble("total"));
            }

        } catch (SQLException e) {
            System.out.println("Error al calcular ingresos por mes: " + e.getMessage());
        }

        return resultado;
    }

    @Override
    public Map<String, Double> ingresosPorTipoMembresia() {
        Map<String, Double> resultado = new LinkedHashMap<>();
        String sql = "SELECT t.nombre, COALESCE(SUM(p.monto), 0) AS total " +
                "FROM tipo_membresia t LEFT JOIN pago p ON p.tipo_membresia_id = t.id " +
                "GROUP BY t.id, t.nombre ORDER BY t.id";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultado.put(rs.getString("nombre"), rs.getDouble("total"));
            }

        } catch (SQLException e) {
            System.out.println("Error al calcular ingresos por tipo: " + e.getMessage());
        }

        return resultado;
    }

    @Override
    public Map<String, Integer> asistenciasPorTurno() {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        resultado.put("mañana", 0);
        resultado.put("noche", 0);

        String sql = "SELECT turno, COUNT(*) AS total FROM asistencia GROUP BY turno";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultado.put(rs.getString("turno"), rs.getInt("total"));
            }

        } catch (SQLException e) {
            System.out.println("Error al contar asistencias por turno: " + e.getMessage());
        }

        return resultado;
    }

    @Override
    public List<String[]> membresiasPorVencer(int dias) {
        List<String[]> filas = new ArrayList<>();
        String sql = "SELECT p.nombre, p.telefono, t.nombre AS tipo, m.fecha_vencimiento, " +
                "DATEDIFF(m.fecha_vencimiento, CURDATE()) AS dias_restantes " +
                "FROM membresia m " +
                "JOIN persona p ON p.id = m.cliente_id " +
                "JOIN tipo_membresia t ON t.id = m.tipo_membresia_id " +
                "WHERE m.fecha_vencimiento <= DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
                "ORDER BY m.fecha_vencimiento";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, dias);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int restantes = rs.getInt("dias_restantes");
                String estado;
                if (restantes < 0) {
                    estado = "Vencida hace " + (-restantes) + (restantes == -1 ? " día" : " días");
                } else if (restantes == 0) {
                    estado = "Vence hoy";
                } else {
                    estado = "Vence en " + restantes + (restantes == 1 ? " día" : " días");
                }

                filas.add(new String[]{
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("tipo"),
                        rs.getDate("fecha_vencimiento").toString(),
                        estado
                });
            }

        } catch (SQLException e) {
            System.out.println("Error al listar membresías por vencer: " + e.getMessage());
        }

        return filas;
    }
}