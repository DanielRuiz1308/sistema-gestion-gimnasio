package com.gimnasio.dao.impl;

import com.gimnasio.dao.MembresiaDAO;
import com.gimnasio.modelo.Membresia;
import com.gimnasio.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MembresiaDAOImpl implements MembresiaDAO {

    @Override
    public void guardar(Membresia membresia) {
        String sql = "INSERT INTO membresia (cliente_id, tipo_membresia_id, fecha_pago, fecha_vencimiento) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, membresia.getClienteId());
            ps.setInt(2, membresia.getTipoMembresiaId());
            ps.setDate(3, Date.valueOf(membresia.getFechaPago()));
            ps.setDate(4, Date.valueOf(membresia.getFechaVencimiento()));
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                membresia.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            System.out.println("Error al guardar membresía: " + e.getMessage());
        }
    }

    @Override
    public Membresia buscarPorClienteId(int clienteId) {
        String sql = "SELECT * FROM membresia WHERE cliente_id = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, clienteId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearMembresia(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar membresía: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Membresia> listarTodas() {
        List<Membresia> membresias = new ArrayList<>();
        String sql = "SELECT * FROM membresia";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                membresias.add(mapearMembresia(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar membresías: " + e.getMessage());
        }

        return membresias;
    }

    @Override
    public void actualizar(Membresia membresia) {
        String sql = "UPDATE membresia SET tipo_membresia_id = ?, fecha_pago = ?, fecha_vencimiento = ? " +
                "WHERE cliente_id = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, membresia.getTipoMembresiaId());
            ps.setDate(2, Date.valueOf(membresia.getFechaPago()));
            ps.setDate(3, Date.valueOf(membresia.getFechaVencimiento()));
            ps.setInt(4, membresia.getClienteId());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error al actualizar membresía: " + e.getMessage());
        }
    }

    private Membresia mapearMembresia(ResultSet rs) throws SQLException {
        return new Membresia(
                rs.getInt("id"),
                rs.getInt("cliente_id"),
                rs.getInt("tipo_membresia_id"),
                rs.getDate("fecha_pago").toLocalDate(),
                rs.getDate("fecha_vencimiento").toLocalDate()
        );
    }
}