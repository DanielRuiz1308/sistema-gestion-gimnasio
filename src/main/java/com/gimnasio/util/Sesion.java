package com.gimnasio.util;

import com.gimnasio.modelo.Usuario;

// Guarda quién inició sesión, para que cualquier pantalla pueda saberlo
// (por ejemplo, para preseleccionar al entrenador que está conectado).
public class Sesion {

    private static Usuario usuarioActual;

    public static void iniciar(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static void cerrar() {
        usuarioActual = null;
    }

    public static Usuario getUsuario() {
        return usuarioActual;
    }

    // true si hay un entrenador conectado que está vinculado a un entrenador real
    public static boolean hayEntrenadorConectado() {
        return usuarioActual != null && usuarioActual.esEntrenador() && usuarioActual.getEntrenadorId() != null;
    }
}