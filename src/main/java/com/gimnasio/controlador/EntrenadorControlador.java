package com.gimnasio.controlador;

import com.gimnasio.dao.EntrenadorDAO;
import com.gimnasio.dao.impl.EntrenadorDAOImpl;
import com.gimnasio.modelo.Entrenador;

import java.util.List;

public class EntrenadorControlador {

    private EntrenadorDAO entrenadorDAO = new EntrenadorDAOImpl();

    public void registrarEntrenador(Entrenador entrenador) {
        entrenadorDAO.guardar(entrenador);
    }

    public Entrenador buscarEntrenador(int id) {
        return entrenadorDAO.buscarPorId(id);
    }

    public List<Entrenador> obtenerTodos() {
        return entrenadorDAO.listarTodos();
    }

    public void actualizarEntrenador(Entrenador entrenador) {
        entrenadorDAO.actualizar(entrenador);
    }

    public void eliminarEntrenador(int id) {
        entrenadorDAO.eliminar(id);
    }
}