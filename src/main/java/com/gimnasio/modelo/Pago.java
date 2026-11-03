package com.gimnasio.modelo;

import java.time.LocalDate;

public class Pago {

    private int id;
    private int clienteId;
    private int tipoMembresiaId;
    private LocalDate fechaPago;
    private double monto;
    private String metodoPago;

    public Pago(int id, int clienteId, int tipoMembresiaId, LocalDate fechaPago, double monto, String metodoPago) {
        this.id = id;
        this.clienteId = clienteId;
        this.tipoMembresiaId = tipoMembresiaId;
        this.fechaPago = fechaPago;
        this.monto = monto;
        this.metodoPago = metodoPago;
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

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
}