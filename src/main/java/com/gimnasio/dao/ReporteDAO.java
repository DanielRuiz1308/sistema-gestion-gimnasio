package com.gimnasio.dao;

import java.util.List;
import java.util.Map;

public interface ReporteDAO {

    int contarClientes();

    int contarMembresiasVigentes();

    int contarMembresiasVencidas();

    // Ingresos de cada mes (Ene..Dic) del año indicado
    Map<String, Double> ingresosPorMes(int anio);

    // Ingresos acumulados por tipo de membresía (Mensual, Trimestral, Anual)
    Map<String, Double> ingresosPorTipoMembresia();

    // Cantidad de asistencias por turno (mañana / noche)
    Map<String, Integer> asistenciasPorTurno();

    // Membresías vencidas o que vencen dentro de los próximos "dias" días
    // Cada fila: {nombre, teléfono, tipo, fecha vencimiento, estado}
    List<String[]> membresiasPorVencer(int dias);
}