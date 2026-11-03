package com.gimnasio.controlador;

import com.gimnasio.dao.AdministradorDAO;
import com.gimnasio.dao.impl.AdministradorDAOImpl;
import com.gimnasio.modelo.Administrador;

import java.util.List;

public class AdministradorControlador {

    private AdministradorDAO administradorDAO = new AdministradorDAOImpl();

    public void registrarAdministrador(Administrador administrador) {
        administradorDAO.guardar(administrador);
    }

    public Administrador buscarAdministrador(int id) {
        return administradorDAO.buscarPorId(id);
    }

    public List<Administrador> obtenerTodos() {
        return administradorDAO.listarTodos();
    }

    public void actualizarAdministrador(Administrador administrador) {
        administradorDAO.actualizar(administrador);
    }

    public void eliminarAdministrador(int id) {
        administradorDAO.eliminar(id);
    }
}