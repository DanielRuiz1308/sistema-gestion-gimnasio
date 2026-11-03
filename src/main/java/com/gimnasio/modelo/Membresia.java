package com.gimnasio.modelo;

import java.time.LocalDate;

public class Membresia {

    private int id;
    private int clienteId;
    private int tipoMembresiaId;
    private LocalDate fechaPago;
    private LocalDate fechaVencimiento;

    public Membresia(int id, int clienteId, int tipoMembresiaId, LocalDate fechaPago, LocalDate fechaVencimiento) {
        this.id = id;
        this.clienteId = clienteId;
        this.tipoMembresiaId = tipoMembresiaId;
        this.fechaPago = fechaPago;
        this.fechaVencimiento = fechaVencimiento;
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

    public int getTipoMembresiaId() {
        return tipoMembresiaId;
    }

    public void setTipoMembresiaId(int tipoMembresiaId) {
        this.tipoMembresiaId = tipoMembresiaId;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    // Calcula si la membresía sigue vigente comparando con la fecha de hoy
    public boolean estaVigente() {
        return !LocalDate.now().isAfter(fechaVencimiento);
    }
}
