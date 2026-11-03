package com.gimnasio.dao;

import com.gimnasio.modelo.Ejercicio;

import java.util.List;

public interface EjercicioDAO {

    void guardar(Ejercicio ejercicio);

    List<Ejercicio> listarPorRutina(int rutinaId);

    void eliminarPorRutina(int rutinaId);

}
