package com.gimnasio.dao.impl;

import com.gimnasio.dao.AdministradorDAO;
import com.gimnasio.modelo.Administrador;
import com.gimnasio.util.ConexionBD;
import com.gimnasio.util.ErrorBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdministradorDAOImpl implements AdministradorDAO {

    @Override
    public void guardar(Administrador administrador) {
        String sqlPersona = "INSERT INTO persona (nombre, documento, telefono, email, genero, fecha_nacimiento) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        String sqlAdministrador = "INSERT INTO administrador (id, permisos) VALUES (?, ?)";

        try (Connection con = ConexionBD.getConnection()) {

            PreparedStatement psPersona = con.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS);
            psPersona.setString(1, administrador.getNombre());
            psPersona.setString(2, administrador.getDocumento());
            psPersona.setString(3, administrador.getTelefono());
            psPersona.setString(4, administrador.getEmail());
            psPersona.setString(5, administrador.getGenero());
            psPersona.setDate(6, Date.valueOf(administrador.getFechaNacimiento()));
            psPersona.executeUpdate();

            ResultSet rs = psPersona.getGeneratedKeys();
            int idGenerado = 0;
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }

            PreparedStatement psAdministrador = con.prepareStatement(sqlAdministrador);
            psAdministrador.setInt(1, idGenerado);
            psAdministrador.setString(2, administrador.getPermisos());
            psAdministrador.executeUpdate();

            administrador.setId(idGenerado);

        } catch (SQLException e) {
            throw new RuntimeException(ErrorBD.mensaje(e), e);
        }
    }

    @Override
    public Administrador buscarPorId(int id) {
        String sql = "SELECT p.id, p.nombre, p.documento, p.telefono, p.email, p.genero, p.fecha_nacimiento, " +
                "a.permisos " +
                "FROM persona p JOIN administrador a ON p.id = a.id " +
                "WHERE p.id = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearAdministrador(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar administrador: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Administrador> listarTodos() {
        List<Administrador> administradores = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, p.documento, p.telefono, p.email, p.genero, p.fecha_nacimiento, " +
                "a.permisos " +
                "FROM persona p JOIN administrador a ON p.id = a.id";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                administradores.add(mapearAdministrador(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar administradores: " + e.getMessage());
        }

        return administradores;
    }

    @Override
    public void actualizar(Administrador administrador) {
        String sqlPersona = "UPDATE persona SET nombre = ?, documento = ?, telefono = ?, email = ?, " +
                "genero = ?, fecha_nacimiento = ? WHERE id = ?";
        String sqlAdministrador = "UPDATE administrador SET permisos = ? WHERE id = ?";

        try (Connection con = ConexionBD.getConnection()) {

            PreparedStatement psPersona = con.prepareStatement(sqlPersona);
            psPersona.setString(1, administrador.getNombre());
            psPersona.setString(2, administrador.getDocumento());
            psPersona.setString(3, administrador.getTelefono());
            psPersona.setString(4, administrador.getEmail());
            psPersona.setString(5, administrador.getGenero());
            psPersona.setDate(6, Date.valueOf(administrador.getFechaNacimiento()));
            psPersona.setInt(7, administrador.getId());
            psPersona.executeUpdate();

            PreparedStatement psAdministrador = con.prepareStatement(sqlAdministrador);
            psAdministrador.setString(1, administrador.getPermisos());
            psAdministrador.setInt(2, administrador.getId());
            psAdministrador.executeUpdate();

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

    private Administrador mapearAdministrador(ResultSet rs) throws SQLException {
        return new Administrador(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("documento"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getString("genero"),
                rs.getDate("fecha_nacimiento").toLocalDate(),
                rs.getString("permisos")
        );
    }
}