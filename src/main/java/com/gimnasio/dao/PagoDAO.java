package com.gimnasio.dao;

import com.gimnasio.modelo.Pago;

import java.util.List;
import java.util.Map;

public interface PagoDAO {

    // CRUD básico
    void guardar(Pago pago);

    List<Pago> listarTodos();

    List<Pago> listarPorCliente(int clienteId);

    // Reportes para el encargado de pagos
    double totalRecaudadoPorMes(int anio, int mes);

    Map<String, Integer> contarClientesPorGenero();

    Map<String, Integer> contarClientesPorRangoEdad();
}
