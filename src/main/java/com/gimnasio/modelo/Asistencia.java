package com.gimnasio.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Asistencia {

    private int id;
    private int clienteId;
    private int entrenadorId;
    private LocalDate fecha;
    private LocalTime hora;
    private String turno;

    public Asistencia(int id, int clienteId, int entrenadorId, LocalDate fecha, LocalTime hora, String turno) {
        this.id = id;
        this.clienteId = clienteId;
        this.entrenadorId = entrenadorId;
        this.fecha = fecha;
        this.hora = hora;
        this.turno = turno;
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }
}
