package com.gimnasio.modelo;

import java.util.Set;

public class Usuario {

    public static final String ROL_ADMIN = "admin";
    public static final String ROL_RECEPCION = "recepcion";
    public static final String ROL_ENTRENADOR = "entrenador";

    // Módulos que puede abrir cada rol (el administrador puede abrir todos)
    private static final Set<String> MODULOS_RECEPCION =
            Set.of("clientes", "membresias", "rutinas", "asistencia", "fisico");
    private static final Set<String> MODULOS_ENTRENADOR =
            Set.of("rutinas", "asistencia", "fisico");

    private int id;
    private String nombreCompleto;
    private String nombreUsuario;
    private String rol;
    private Integer entrenadorId; // solo para el rol entrenador: a qué entrenador corresponde

    public Usuario(int id, String nombreCompleto, String nombreUsuario, String rol) {
        this(id, nombreCompleto, nombreUsuario, rol, null);
    }

    public Usuario(int id, String nombreCompleto, String nombreUsuario, String rol, Integer entrenadorId) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.nombreUsuario = nombreUsuario;
        this.rol = rol;
        this.entrenadorId = entrenadorId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Integer getEntrenadorId() { return entrenadorId; }
    public void setEntrenadorId(Integer entrenadorId) { this.entrenadorId = entrenadorId; }

    public boolean esAdmin() {
        return ROL_ADMIN.equals(rol);
    }

    public boolean esEntrenador() {
        return ROL_ENTRENADOR.equals(rol);
    }

    public boolean puedeAcceder(String modulo) {
        if (esAdmin()) {
            return true;
        }
        if (esEntrenador()) {
            return MODULOS_ENTRENADOR.contains(modulo);
        }
        return MODULOS_RECEPCION.contains(modulo);
    }

    // Texto para mostrar el rol en pantalla
    public String nombreRol() {
        if (esAdmin()) {
            return "Administrador";
        }
        if (esEntrenador()) {
            return "Entrenador";
        }
        return "Recepción";
    }
}