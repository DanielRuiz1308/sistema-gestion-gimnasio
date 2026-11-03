package com.gimnasio.modelo;

public class Ejercicio {

    private int id;
    private int rutinaId;
    private String nombre;
    private int series;
    private int repeticiones;
    private int descanso; // segundos

    public Ejercicio(int id, int rutinaId, String nombre, int series, int repeticiones, int descanso) {
        this.id = id;
        this.rutinaId = rutinaId;
        this.nombre = nombre;
        this.series = series;
        this.repeticiones = repeticiones;
        this.descanso = descanso;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRutinaId() {
        return rutinaId;
    }

    public void setRutinaId(int rutinaId) {
        this.rutinaId = rutinaId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }

    public int getDescanso() {
        return descanso;
    }

    public void setDescanso(int descanso) {
        this.descanso = descanso;
    }

    @Override
    public String toString() {
        return nombre + " — " + series + "x" + repeticiones;
    }
}