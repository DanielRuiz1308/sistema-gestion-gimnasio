package com.gimnasio.dao.impl;

import com.gimnasio.dao.ClienteDAO;
import com.gimnasio.modelo.Cliente;
import com.gimnasio.util.ConexionBD;
import com.gimnasio.util.ErrorBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public void guardar(Cliente cliente) {
        String sqlPersona = "INSERT INTO persona (nombre, documento, telefono, email, genero, fecha_nacimiento) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        String sqlCliente = "INSERT INTO cliente (id) VALUES (?)";

        try (Connection con = ConexionBD.getConnection()) {

            PreparedStatement psPersona = con.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS);
            psPersona.setString(1, cliente.getNombre());
            psPersona.setString(2, cliente.getDocumento());
            psPersona.setString(3, cliente.getTelefono());
            psPersona.setString(4, cliente.getEmail());
            psPersona.setString(5, cliente.getGenero());
            psPersona.setDate(6, Date.valueOf(cliente.getFechaNacimiento()));
            psPersona.executeUpdate();

            ResultSet rs = psPersona.getGeneratedKeys();
            int idGenerado = 0;
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }

            PreparedStatement psCliente = con.prepareStatement(sqlCliente);
            psCliente.setInt(1, idGenerado);
            psCliente.executeUpdate();

            cliente.setId(idGenerado);

        } catch (SQLException e) {
            throw new RuntimeException(ErrorBD.mensaje(e), e);
        }
    }

    @Override
    public Cliente buscarPorId(int id) {
        String sql = "SELECT p.id, p.nombre, p.documento, p.telefono, p.email, p.genero, p.fecha_nacimiento " +
                "FROM persona p JOIN cliente c ON p.id = c.id " +
                "WHERE p.id = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearCliente(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar cliente: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Cliente> listarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, p.documento, p.telefono, p.email, p.genero, p.fecha_nacimiento " +
                "FROM persona p JOIN cliente c ON p.id = c.id";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar clientes: " + e.getMessage());
        }

        return clientes;
    }

    @Override
    public void actualizar(Cliente cliente) {
        String sql = "UPDATE persona SET nombre = ?, documento = ?, telefono = ?, email = ?, " +
                "genero = ?, fecha_nacimiento = ? WHERE id = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getDocumento());
            ps.setString(3, cliente.getTelefono());
            ps.setString(4, cliente.getEmail());
            ps.setString(5, cliente.getGenero());
            ps.setDate(6, Date.valueOf(cliente.getFechaNacimiento()));
            ps.setInt(7, cliente.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(ErrorBD.mensaje(e), e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM persona WHERE id = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(ErrorBD.mensaje(e), e);
        }
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("documento"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getString("genero"),
                rs.getDate("fecha_nacimiento").toLocalDate()
        );
    }
}