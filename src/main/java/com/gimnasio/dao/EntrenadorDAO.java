package com.gimnasio.dao;

import com.gimnasio.modelo.Entrenador;

import java.util.List;

public interface EntrenadorDAO {

    void guardar(Entrenador entrenador);

    Entrenador buscarPorId(int id);

    List<Entrenador> listarTodos();

    void actualizar(Entrenador entrenador);

    void eliminar(int id);

}
