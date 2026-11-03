package com.gimnasio.dao;

import com.gimnasio.modelo.Membresia;

import java.util.List;

public interface MembresiaDAO {

    void guardar(Membresia membresia);

    Membresia buscarPorClienteId(int clienteId);

    List<Membresia> listarTodas();

    void actualizar(Membresia membresia);

}
