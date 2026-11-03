package com.gimnasio.controlador;

import com.gimnasio.dao.RegistroFisicoDAO;
import com.gimnasio.dao.impl.RegistroFisicoDAOImpl;
import com.gimnasio.modelo.RegistroFisico;

import java.time.LocalDate;
import java.util.List;

public class RegistroFisicoControlador {

    private RegistroFisicoDAO dao = new RegistroFisicoDAOImpl();

    public void registrar(int clienteId, LocalDate fecha, double peso, double altura) {
        dao.guardar(new RegistroFisico(0, clienteId, fecha, peso, altura));
    }

    public List<RegistroFisico> obtenerPorCliente(int clienteId) {
        return dao.listarPorCliente(clienteId);
    }

    public void eliminar(int id) {
        dao.eliminar(id);
    }
}