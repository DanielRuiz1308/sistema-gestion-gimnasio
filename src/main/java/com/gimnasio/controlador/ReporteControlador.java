package com.gimnasio.controlador;

import com.gimnasio.dao.ReporteDAO;
import com.gimnasio.dao.impl.ReporteDAOImpl;

import java.util.List;
import java.util.Map;

public class ReporteControlador {

    private ReporteDAO reporteDAO = new ReporteDAOImpl();

    public int totalClientes() {
        return reporteDAO.contarClientes();
    }

    public int membresiasVigentes() {
        return reporteDAO.contarMembresiasVigentes();
    }

    public int membresiasVencidas() {
        return reporteDAO.contarMembresiasVencidas();
    }

    public Map<String, Double> ingresosPorMes(int anio) {
        return reporteDAO.ingresosPorMes(anio);
    }

    public Map<String, Double> ingresosPorTipoMembresia() {
        return reporteDAO.ingresosPorTipoMembresia();
    }

    public Map<String, Integer> asistenciasPorTurno() {
        return reporteDAO.asistenciasPorTurno();
    }

    public List<String[]> membresiasPorVencer(int dias) {
        return reporteDAO.membresiasPorVencer(dias);
    }
}