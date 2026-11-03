package com.gimnasio.dao;

import com.gimnasio.modelo.RegistroFisico;

import java.util.List;

public interface RegistroFisicoDAO {
    void guardar(RegistroFisico registro);
    List<RegistroFisico> listarPorCliente(int clienteId);
    void eliminar(int id);
}
