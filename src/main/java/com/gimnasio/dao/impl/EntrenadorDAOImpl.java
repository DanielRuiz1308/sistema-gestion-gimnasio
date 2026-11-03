package com.gimnasio.dao.impl;

import com.gimnasio.dao.EntrenadorDAO;
import com.gimnasio.modelo.Entrenador;
import com.gimnasio.util.ConexionBD;
import com.gimnasio.util.ErrorBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntrenadorDAOImpl implements EntrenadorDAO {

    @Override
    public void guardar(Entrenador entrenador) {
        String sqlPersona = "INSERT INTO persona (nombre, documento, telefono, email, genero, fecha_nacimiento) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        String sqlEntrenador = "INSERT INTO entrenador (id, especialidad) VALUES (?, ?)";

        try (Connection con = ConexionBD.getConnection()) {

            PreparedStatement psPersona = con.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS);
            psPersona.setString(1, entrenador.getNombre());
            psPersona.setString(2, entrenador.getDocumento());
            psPersona.setString(3, entrenador.getTelefono());
            psPersona.setString(4, entrenador.getEmail());
            psPersona.setString(5, entrenador.getGenero());
            psPersona.setDate(6, Date.valueOf(entrenador.getFechaNacimiento()));
            psPersona.executeUpdate();

            ResultSet rs = psPersona.getGeneratedKeys();
            int idGenerado = 0;
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }

            PreparedStatement psEntrenador = con.prepareStatement(sqlEntrenador);
            psEntrenador.setInt(1, idGenerado);
            psEntrenador.setString(2, entrenador.getEspecialidad());
            psEntrenador.executeUpdate();

            entrenador.setId(idGenerado);

        } catch (SQLException e) {
            throw new RuntimeException(ErrorBD.mensaje(e), e);
        }
    }

    @Override
    public Entrenador buscarPorId(int id) {
        String sql = "SELECT p.id, p.nombre, p.documento, p.telefono, p.email, p.genero, p.fecha_nacimiento, " +
                "e.especialidad " +
                "FROM persona p JOIN entrenador e ON p.id = e.id " +
                "WHERE p.id = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearEntrenador(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar entrenador: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Entrenador> listarTodos() {
        List<Entrenador> entrenadores = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, p.documento, p.telefono, p.email, p.genero, p.fecha_nacimiento, " +
                "e.especialidad " +
                "FROM persona p JOIN entrenador e ON p.id = e.id";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                entrenadores.add(mapearEntrenador(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar entrenadores: " + e.getMessage());
        }

        return entrenadores;
    }

    @Override
    public void actualizar(Entrenador entrenador) {
        String sqlPersona = "UPDATE persona SET nombre = ?, documento = ?, telefono = ?, email = ?, " +
                "genero = ?, fecha_nacimiento = ? WHERE id = ?";
        String sqlEntrenador = "UPDATE entrenador SET especialidad = ? WHERE id = ?";

        try (Connection con = ConexionBD.getConnection()) {

            PreparedStatement psPersona = con.prepareStatement(sqlPersona);
            psPersona.setString(1, entrenador.getNombre());
            psPersona.setString(2, entrenador.getDocumento());
            psPersona.setString(3, entrenador.getTelefono());
            psPersona.setString(4, entrenador.getEmail());
            psPersona.setString(5, entrenador.getGenero());
            psPersona.setDate(6, Date.valueOf(entrenador.getFechaNacimiento()));
            psPersona.setInt(7, entrenador.getId());
            psPersona.executeUpdate();

            PreparedStatement psEntrenador = con.prepareStatement(sqlEntrenador);
            psEntrenador.setString(1, entrenador.getEspecialidad());
            psEntrenador.setInt(2, entrenador.getId());
            psEntrenador.executeUpdate();

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

    private Entrenador mapearEntrenador(ResultSet rs) throws SQLException {
        return new Entrenador(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("documento"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getString("genero"),
                rs.getDate("fecha_nacimiento").toLocalDate(),
                rs.getString("especialidad")
        );
    }
}