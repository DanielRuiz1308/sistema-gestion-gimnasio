package com.gimnasio.controlador;

import com.gimnasio.dao.UsuarioDAO;
import com.gimnasio.dao.impl.UsuarioDAOImpl;
import com.gimnasio.modelo.Usuario;

import java.util.List;

public class UsuarioControlador {

    private UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    public Usuario iniciarSesion(String nombreUsuario, String contrasena) {
        return usuarioDAO.autenticar(nombreUsuario, contrasena);
    }

    public void crearUsuario(Usuario usuario, String contrasena) {
        usuarioDAO.guardar(usuario, contrasena);
    }

    public List<Usuario> obtenerTodos() {
        return usuarioDAO.listarTodos();
    }

    public void cambiarContrasena(int id, String nuevaContrasena) {
        usuarioDAO.cambiarContrasena(id, nuevaContrasena);
    }

    public void eliminarUsuario(Usuario usuario) {
        // Regla de negocio: el sistema nunca puede quedarse sin administradores
        if (usuario.esAdmin() && usuarioDAO.contarAdmins() <= 1) {
            throw new RuntimeException("No puedes eliminar al único administrador del sistema.");
        }
        usuarioDAO.eliminar(usuario.getId());
    }
}