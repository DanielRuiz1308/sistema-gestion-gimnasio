package com.gimnasio.dao.impl;

import com.gimnasio.dao.RegistroFisicoDAO;
import com.gimnasio.modelo.RegistroFisico;
import com.gimnasio.util.ConexionBD;
import com.gimnasio.util.ErrorBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistroFisicoDAOImpl implements RegistroFisicoDAO {

    @Override
    public void guardar(RegistroFisico registro) {
        String sql = "INSERT INTO registro_fisico (cliente_id, fecha, peso, altura) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, registro.getClienteId());
            ps.setDate(2, Date.valueOf(registro.getFecha()));
            ps.setDouble(3, registro.getPeso());
            ps.setDouble(4, registro.getAltura());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                registro.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(ErrorBD.mensaje(e), e);
        }
    }

    @Override
    public List<RegistroFisico> listarPorCliente(int clienteId) {
        List<RegistroFisico> registros = new ArrayList<>();
        String sql = "SELECT id, cliente_id, fecha, peso, altura FROM registro_fisico " +
                "WHERE cliente_id = ? ORDER BY fecha DESC, id DESC";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, clienteId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                registros.add(new RegistroFisico(
                        rs.getInt("id"),
                        rs.getInt("cliente_id"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getDouble("peso"),
                        rs.getDouble("altura")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar registros físicos: " + e.getMessage());
        }

        return registros;
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM registro_fisico WHERE id = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(ErrorBD.mensaje(e), e);
        }
    }
}