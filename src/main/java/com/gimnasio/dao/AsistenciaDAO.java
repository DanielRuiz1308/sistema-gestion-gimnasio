package com.gimnasio.dao;

import com.gimnasio.modelo.Asistencia;

import java.util.List;

public interface AsistenciaDAO {

    void guardar(Asistencia asistencia);

    List<Asistencia> listarTodas();

    List<Asistencia> listarPorCliente(int clienteId);

}
