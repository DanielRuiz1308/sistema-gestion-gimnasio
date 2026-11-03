package com.gimnasio.modelo;

import java.time.LocalDate;

public class RegistroFisico {

    private int id;
    private int clienteId;
    private LocalDate fecha;
    private double peso;    // kilogramos
    private double altura;  // metros

    public RegistroFisico(int id, int clienteId, LocalDate fecha, double peso, double altura) {
        this.id = id;
        this.clienteId = clienteId;
        this.fecha = fecha;
        this.peso = peso;
        this.altura = altura;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public double getAltura() { return altura; }
    public void setAltura(double altura) { this.altura = altura; }

    // Índice de masa corporal: peso / altura²
    public double calcularImc() {
        return peso / (altura * altura);
    }

    public String clasificarImc() {
        double imc = calcularImc();
        if (imc < 18.5) return "Bajo peso";
        if (imc < 25) return "Normal";
        if (imc < 30) return "Sobrepeso";
        return "Obesidad";
    }
}