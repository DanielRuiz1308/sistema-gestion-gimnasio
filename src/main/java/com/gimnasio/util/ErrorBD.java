package com.gimnasio.util;

import java.sql.SQLException;

public class ErrorBD {

    public static String mensaje(SQLException e) {
        switch (e.getErrorCode()) {
            case 1062: return "Ya existe un registro con ese documento (o dato único).";
            case 1451: return "No se puede eliminar: hay información que depende de este registro (rutinas, asistencias, etc.).";
            case 1452: return "El dato relacionado no existe (cliente, entrenador o tipo de membresía inválido).";
            default:   return "Error de base de datos: " + e.getMessage();
        }
    }
}
