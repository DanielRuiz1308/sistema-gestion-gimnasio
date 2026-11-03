package com.gimnasio.dao.impl;

import com.gimnasio.dao.AsistenciaDAO;
import com.gimnasio.modelo.Asistencia;
import com.gimnasio.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsistenciaDAOImpl implements AsistenciaDAO {

    @Override
    public void guardar(Asistencia asistencia) {
        String sql = "INSERT INTO asistencia (cliente_id, entrenador_id, fecha, hora, turno) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, asistencia.getClienteId());
            ps.setInt(2, asistencia.getEntrenadorId());
            ps.setDate(3, Date.valueOf(asistencia.getFecha()));
            ps.setTime(4, Time.valueOf(asistencia.getHora()));
            ps.setString(5, asistencia.getTurno());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                asistencia.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            System.out.println("Error al registrar asistencia: " + e.getMessage());
        }
    }

    @Override
    public List<Asistencia> listarTodas() {
        List<Asistencia> asistencias = new ArrayList<>();
        String sql = "SELECT * FROM asistencia ORDER BY fecha DESC, hora DESC";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                asistencias.add(mapearAsistencia(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar asistencias: " + e.getMessage());
        }

        return asistencias;
    }

    @Override
    public List<Asistencia> listarPorCliente(int clienteId) {
        List<Asistencia> asistencias = new ArrayList<>();
        String sql = "SELECT * FROM asistencia WHERE cliente_id = ? ORDER BY fecha DESC, hora DESC";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, clienteId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                asistencias.add(mapearAsistencia(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar asistencias del cliente: " + e.getMessage());
        }

        return asistencias;
    }

    private Asistencia mapearAsistencia(ResultSet rs) throws SQLException {
        return new Asistencia(
                rs.getInt("id"),
                rs.getInt("cliente_id"),
                rs.getInt("entrenador_id"),
                rs.getDate("fecha").toLocalDate(),
                rs.getTime("hora").toLocalTime(),
                rs.getString("turno")
        );
    }
}
