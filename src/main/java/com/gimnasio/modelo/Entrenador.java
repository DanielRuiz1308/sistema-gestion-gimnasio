package com.gimnasio.modelo;

import java.time.LocalDate;

public class Entrenador extends Persona {

    private String especialidad;

    public Entrenador(int id, String nombre, String documento, String telefono, String email,
                      String genero, LocalDate fechaNacimiento, String especialidad) {
        super(id, nombre, documento, telefono, email, genero, fechaNacimiento);
        this.especialidad = especialidad;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
