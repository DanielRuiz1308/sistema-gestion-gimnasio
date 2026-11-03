package com.gimnasio.modelo;

import java.time.LocalDate;

public class Cliente extends Persona {

    public Cliente(int id, String nombre, String documento, String telefono, String email,
                   String genero, LocalDate fechaNacimiento) {
        super(id, nombre, documento, telefono, email, genero, fechaNacimiento);
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
