package com.gimnasio.dao;

import com.gimnasio.modelo.Administrador;

import java.util.List;

public interface AdministradorDAO {

    void guardar(Administrador administrador);

    Administrador buscarPorId(int id);

    List<Administrador> listarTodos();

    void actualizar(Administrador administrador);

    void eliminar(int id);

}
