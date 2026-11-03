package com.gimnasio.dao;

import com.gimnasio.modelo.Cliente;
import java.util.List;

public interface ClienteDAO {

    void guardar(Cliente cliente);

    Cliente buscarPorId(int id);

    List<Cliente> listarTodos();

    void actualizar(Cliente cliente);

    void eliminar(int id);

}
