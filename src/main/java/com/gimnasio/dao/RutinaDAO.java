package com.gimnasio.dao;

import com.gimnasio.modelo.Rutina;

import java.util.List;

public interface RutinaDAO {

    void guardar(Rutina rutina);

    Rutina buscarPorCliente(int clienteId);

    List<Rutina> listarTodas();

    void actualizar(Rutina rutina);

}