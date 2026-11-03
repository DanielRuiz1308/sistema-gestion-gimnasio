package com.gimnasio.controlador;

import com.gimnasio.dao.PagoDAO;
import com.gimnasio.dao.impl.PagoDAOImpl;
import com.gimnasio.modelo.Pago;

import java.util.List;
import java.util.Map;

public class PagoControlador {

    private PagoDAO pagoDAO = new PagoDAOImpl();

    public void registrarPago(Pago pago) {
        pagoDAO.guardar(pago);
    }

    public List<Pago> obtenerTodos() {
        return pagoDAO.listarTodos();
    }

    public List<Pago> obtenerPorCliente(int clienteId) {
        return pagoDAO.listarPorCliente(clienteId);
    }

    public double totalRecaudadoPorMes(int anio, int mes) {
        return pagoDAO.totalRecaudadoPorMes(anio, mes);
    }

    public Map<String, Integer> clientesPorGenero() {
        return pagoDAO.contarClientesPorGenero();
    }

    public Map<String, Integer> clientesPorRangoEdad() {
        return pagoDAO.contarClientesPorRangoEdad();
    }
}