package com.gimnasio.modelo;

import java.time.LocalDate;

public class Administrador extends Persona {

    private String permisos;

    public Administrador(int id, String nombre, String documento, String telefono, String email,
                         String genero, LocalDate fechaNacimiento, String permisos) {
        super(id, nombre, documento, telefono, email, genero, fechaNacimiento);
        this.permisos = permisos;
    }

    public String getPermisos() {
        return permisos;
    }

    public void setPermisos(String permisos) {
        this.permisos = permisos;
    }
}