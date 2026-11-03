package com.gimnasio.controlador;

import com.gimnasio.dao.ClienteDAO;
import com.gimnasio.dao.impl.ClienteDAOImpl;
import com.gimnasio.modelo.Cliente;

import java.util.List;

public class ClienteControlador {

    private ClienteDAO clienteDAO = new ClienteDAOImpl();

    public void registrarCliente(Cliente cliente) {
        clienteDAO.guardar(cliente);
    }

    public Cliente buscarCliente(int id) {
        return clienteDAO.buscarPorId(id);
    }

    public List<Cliente> obtenerTodos() {
        return clienteDAO.listarTodos();
    }

    public void actualizarCliente(Cliente cliente) {
        clienteDAO.actualizar(cliente);
    }

    public void eliminarCliente(int id) {
        clienteDAO.eliminar(id);
    }
}
