package com.gimnasio.dao.impl;

import com.gimnasio.dao.EjercicioDAO;
import com.gimnasio.dao.RutinaDAO;
import com.gimnasio.dao.impl.EjercicioDAOImpl;
import com.gimnasio.modelo.Ejercicio;
import com.gimnasio.modelo.Rutina;
import com.gimnasio.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RutinaDAOImpl implements RutinaDAO {

    private EjercicioDAO ejercicioDAO = new EjercicioDAOImpl();

    @Override
    public void guardar(Rutina rutina) {
        String sql = "INSERT INTO rutina (cliente_id, entrenador_id, nombre, nivel, objetivo) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, rutina.getClienteId());
            ps.setInt(2, rutina.getEntrenadorId());
            ps.setString(3, rutina.getNombre());
            ps.setString(4, rutina.getNivel());
            ps.setString(5, rutina.getObjetivo());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                rutina.setId(rs.getInt(1));
            }

            // Guardar cada ejercicio de la lista, asociado a esta rutina
            for (Ejercicio ejercicio : rutina.getEjercicios()) {
                ejercicio.setRutinaId(rutina.getId());
                ejercicioDAO.guardar(ejercicio);
            }

        } catch (SQLException e) {
            System.out.println("Error al guardar rutina: " + e.getMessage());
        }
    }

    @Override
    public Rutina buscarPorCliente(int clienteId) {
        String sql = "SELECT * FROM rutina WHERE cliente_id = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, clienteId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearRutina(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar rutina: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Rutina> listarTodas() {
        List<Rutina> rutinas = new ArrayList<>();
        String sql = "SELECT * FROM rutina";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rutinas.add(mapearRutina(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar rutinas: " + e.getMessage());
        }

        return rutinas;
    }

    @Override
    public void actualizar(Rutina rutina) {
        String sql = "UPDATE rutina SET entrenador_id = ?, nombre = ?, nivel = ?, objetivo = ? WHERE id = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, rutina.getEntrenadorId());
            ps.setString(2, rutina.getNombre());
            ps.setString(3, rutina.getNivel());
            ps.setString(4, rutina.getObjetivo());
            ps.setInt(5, rutina.getId());
            ps.executeUpdate();

            // Estrategia simple: borrar todos los ejercicios viejos y guardar los nuevos
            ejercicioDAO.eliminarPorRutina(rutina.getId());
            for (Ejercicio ejercicio : rutina.getEjercicios()) {
                ejercicio.setRutinaId(rutina.getId());
                ejercicioDAO.guardar(ejercicio);
            }

        } catch (SQLException e) {
            System.out.println("Error al actualizar rutina: " + e.getMessage());
        }
    }

    private Rutina mapearRutina(ResultSet rs) throws SQLException {
        Rutina rutina = new Rutina(
                rs.getInt("id"),
                rs.getInt("cliente_id"),
                rs.getInt("entrenador_id"),
                rs.getString("nombre"),
                rs.getString("nivel"),
                rs.getString("objetivo")
        );

        // Cargar también sus ejercicios
        rutina.setEjercicios(ejercicioDAO.listarPorRutina(rutina.getId()));

        return rutina;
    }
}