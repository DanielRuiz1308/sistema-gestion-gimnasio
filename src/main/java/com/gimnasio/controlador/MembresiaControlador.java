package com.gimnasio.controlador;

import com.gimnasio.dao.MembresiaDAO;
import com.gimnasio.dao.PagoDAO;
import com.gimnasio.dao.TipoMembresiaDAO;
import com.gimnasio.dao.impl.MembresiaDAOImpl;
import com.gimnasio.dao.impl.PagoDAOImpl;
import com.gimnasio.dao.impl.TipoMembresiaDAOImpl;
import com.gimnasio.modelo.Membresia;
import com.gimnasio.modelo.Pago;
import com.gimnasio.modelo.TipoMembresia;

import java.time.LocalDate;
import java.util.List;

public class MembresiaControlador {

    private MembresiaDAO membresiaDAO = new MembresiaDAOImpl();
    private PagoDAO pagoDAO = new PagoDAOImpl();
    private TipoMembresiaDAO tipoMembresiaDAO = new TipoMembresiaDAOImpl();

    public Membresia buscarPorCliente(int clienteId) {
        return membresiaDAO.buscarPorClienteId(clienteId);
    }

    public List<Membresia> obtenerTodas() {
        return membresiaDAO.listarTodas();
    }

    public List<TipoMembresia> obtenerTiposMembresia() {
        return tipoMembresiaDAO.listarTodos();
    }

    // Registra un pago Y actualiza (o crea) la membresía del cliente, en un solo paso
    public void renovarMembresia(int clienteId, int tipoMembresiaId, String metodoPago, int diasDuracion) {
        LocalDate hoy = LocalDate.now();
        LocalDate vencimiento = hoy.plusDays(diasDuracion);

        // Buscamos si el tipo elegido tiene precio, para registrar el pago con el monto correcto
        double monto = tipoMembresiaDAO.listarTodos().stream()
                .filter(t -> t.getId() == tipoMembresiaId)
                .findFirst()
                .map(TipoMembresia::getPrecio)
                .orElse(0.0);

        // 1. Registrar el pago en el histórico
        Pago pago = new Pago(0, clienteId, tipoMembresiaId, hoy, monto, metodoPago);
        pagoDAO.guardar(pago);

        // 2. Actualizar o crear la membresía vigente
        Membresia existente = membresiaDAO.buscarPorClienteId(clienteId);

        if (existente == null) {
            Membresia nueva = new Membresia(0, clienteId, tipoMembresiaId, hoy, vencimiento);
            membresiaDAO.guardar(nueva);
        } else {
            existente.setTipoMembresiaId(tipoMembresiaId);
            existente.setFechaPago(hoy);
            existente.setFechaVencimiento(vencimiento);
            membresiaDAO.actualizar(existente);
        }
    }
}