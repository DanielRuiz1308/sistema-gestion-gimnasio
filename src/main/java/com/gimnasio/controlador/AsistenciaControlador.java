package com.gimnasio.controlador;

import com.gimnasio.dao.AsistenciaDAO;
import com.gimnasio.dao.impl.AsistenciaDAOImpl;
import com.gimnasio.modelo.Asistencia;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AsistenciaControlador {

    private AsistenciaDAO asistenciaDAO = new AsistenciaDAOImpl();

    // Registra la asistencia usando la fecha/hora actuales, calculando el turno automáticamente
    public void registrarAsistenciaAhora(int clienteId, int entrenadorId) {
        LocalDate hoy = LocalDate.now();
        LocalTime horaActual = LocalTime.now();
        String turno = calcularTurno(horaActual);

        Asistencia asistencia = new Asistencia(0, clienteId, entrenadorId, hoy, horaActual, turno);
        asistenciaDAO.guardar(asistencia);
    }

    public List<Asistencia> obtenerTodas() {
        return asistenciaDAO.listarTodas();
    }

    public List<Asistencia> obtenerPorCliente(int clienteId) {
        return asistenciaDAO.listarPorCliente(clienteId);
    }

    // Antes del mediodía = mañana, resto del día = noche
    private String calcularTurno(LocalTime hora) {
        return hora.isBefore(LocalTime.NOON) ? "mañana" : "noche";
    }
}