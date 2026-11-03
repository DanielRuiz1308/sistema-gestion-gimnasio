package com.gimnasio.dao.impl;

import com.gimnasio.dao.EjercicioDAO;
import com.gimnasio.modelo.Ejercicio;
import com.gimnasio.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EjercicioDAOImpl implements EjercicioDAO {

    @Override
    public void guardar(Ejercicio ejercicio) {
        String sql = "INSERT INTO ejercicio (rutina_id, nombre, series, repeticiones, descanso) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, ejercicio.getRutinaId());
            ps.setString(2, ejercicio.getNombre());
            ps.setInt(3, ejercicio.getSeries());
            ps.setInt(4, ejercicio.getRepeticiones());
            ps.setInt(5, ejercicio.getDescanso());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ejercicio.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            System.out.println("Error al guardar ejercicio: " + e.getMessage());
        }
    }

    @Override
    public List<Ejercicio> listarPorRutina(int rutinaId) {
        List<Ejercicio> ejercicios = new ArrayList<>();
        String sql = "SELECT * FROM ejercicio WHERE rutina_id = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, rutinaId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ejercicios.add(new Ejercicio(
                        rs.getInt("id"),
                        rs.getInt("rutina_id"),
                        rs.getString("nombre"),
                        rs.getInt("series"),
                        rs.getInt("repeticiones"),
                        rs.getInt("descanso")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar ejercicios: " + e.getMessage());
        }

        return ejercicios;
    }

    @Override
    public void eliminarPorRutina(int rutinaId) {
        String sql = "DELETE FROM ejercicio WHERE rutina_id = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, rutinaId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error al eliminar ejercicios: " + e.getMessage());
        }
    }
}