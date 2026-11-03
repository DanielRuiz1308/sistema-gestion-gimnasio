package com.gimnasio.dao;

import com.gimnasio.modelo.Usuario;

import java.util.List;

public interface UsuarioDAO {

    // Devuelve el usuario si el nombre y la contraseña son correctos; si no, null
    Usuario autenticar(String nombreUsuario, String contrasena);

    void guardar(Usuario usuario, String contrasena);

    List<Usuario> listarTodos();

    void cambiarContrasena(int id, String nuevaContrasena);

    void eliminar(int id);

    int contarAdmins();
}