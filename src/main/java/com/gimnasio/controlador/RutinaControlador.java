package com.gimnasio.controlador;

import com.gimnasio.dao.RutinaDAO;
import com.gimnasio.dao.impl.RutinaDAOImpl;
import com.gimnasio.modelo.Rutina;

import java.util.List;

public class RutinaControlador {

    private RutinaDAO rutinaDAO = new RutinaDAOImpl();

    public void registrarRutina(Rutina rutina) {
        rutinaDAO.guardar(rutina);
    }

    public Rutina buscarPorCliente(int clienteId) {
        return rutinaDAO.buscarPorCliente(clienteId);
    }

    public List<Rutina> obtenerTodas() {
        return rutinaDAO.listarTodas();
    }

    public void actualizarRutina(Rutina rutina) {
        rutinaDAO.actualizar(rutina);
    }
}
