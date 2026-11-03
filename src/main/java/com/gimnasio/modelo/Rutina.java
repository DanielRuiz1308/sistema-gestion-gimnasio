package com.gimnasio.modelo;

import java.util.ArrayList;
import java.util.List;

public class Rutina {

    private int id;
    private int clienteId;
    private int entrenadorId;
    private String nombre;
    private String nivel;
    private String objetivo;
    private List<Ejercicio> ejercicios;

    public Rutina(int id, int clienteId, int entrenadorId, String nombre, String nivel, String objetivo) {
        this.id = id;
        this.clienteId = clienteId;
        this.entrenadorId = entrenadorId;
        this.nombre = nombre;
        this.nivel = nivel;
        this.objetivo = objetivo;
        this.ejercicios = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public int getEntrenadorId() {
        return entrenadorId;
    }

    public void setEntrenadorId(int entrenadorId) {
        this.entrenadorId = entrenadorId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public List<Ejercicio> getEjercicios() {
        return ejercicios;
    }

    public void setEjercicios(List<Ejercicio> ejercicios) {
        this.ejercicios = ejercicios;
    }

    public void agregarEjercicio(Ejercicio ejercicio) {
        ejercicios.add(ejercicio);
    }
}