package com.gimnasio.dao.impl;

import com.gimnasio.dao.UsuarioDAO;
import com.gimnasio.modelo.Usuario;
import com.gimnasio.util.ConexionBD;
import com.gimnasio.util.ErrorBD;
import com.gimnasio.util.Seguridad;

import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public Usuario autenticar(String nombreUsuario, String contrasena) {
        String sql = "SELECT id, nombre_completo, nombre_usuario, rol, entrenador_id, salt, password_hash " +
                "FROM usuario WHERE nombre_usuario = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombreUsuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String hashCalculado = Seguridad.hash(rs.getString("salt"), contrasena);
                String hashGuardado = rs.getString("password_hash");

                // Comparación segura (no revela en qué posición falla)
                boolean coincide = MessageDigest.isEqual(hashCalculado.getBytes(), hashGuardado.getBytes());
                if (coincide) {
                    return mapearUsuario(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al autenticar: " + e.getMessage());
            throw new RuntimeException("No se pudo conectar con la base de datos", e);
        }

        return null;
    }

    @Override
    public void guardar(Usuario usuario, String contrasena) {
        String sql = "INSERT INTO usuario (nombre_completo, nombre_usuario, salt, password_hash, rol, entrenador_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        String sal = Seguridad.generarSal();

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getNombreCompleto());
            ps.setString(2, usuario.getNombreUsuario());
            ps.setString(3, sal);
            ps.setString(4, Seguridad.hash(sal, contrasena));
            ps.setString(5, usuario.getRol());
            if (usuario.getEntrenadorId() == null) {
                ps.setNull(6, Types.INTEGER);
            } else {
                ps.setInt(6, usuario.getEntrenadorId());
            }
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                usuario.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new RuntimeException("Ya existe un usuario con ese nombre de usuario (o ese entrenador ya tiene usuario).", e);
            }
            throw new RuntimeException(ErrorBD.mensaje(e), e);
        }
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nombre_completo, nombre_usuario, rol, entrenador_id FROM usuario ORDER BY id";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        }

        return usuarios;
    }

    @Override
    public void cambiarContrasena(int id, String nuevaContrasena) {
        String sql = "UPDATE usuario SET salt = ?, password_hash = ? WHERE id = ?";
        String sal = Seguridad.generarSal();

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sal);
            ps.setString(2, Seguridad.hash(sal, nuevaContrasena));
            ps.setInt(3, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(ErrorBD.mensaje(e), e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM usuario WHERE id = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(ErrorBD.mensaje(e), e);
        }
    }

    // Arma un Usuario a partir de una fila del ResultSet
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        int entrenadorId = rs.getInt("entrenador_id");
        Integer vinculo = rs.wasNull() ? null : entrenadorId;

        return new Usuario(rs.getInt("id"), rs.getString("nombre_completo"),
                rs.getString("nombre_usuario"), rs.getString("rol"), vinculo);
    }

    @Override
    public int contarAdmins() {
        String sql = "SELECT COUNT(*) FROM usuario WHERE rol = 'admin'";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Error al contar administradores: " + e.getMessage());
        }
        return 0;
    }
}