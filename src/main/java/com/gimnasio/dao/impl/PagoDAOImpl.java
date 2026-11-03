package com.gimnasio.dao.impl;

import com.gimnasio.dao.PagoDAO;
import com.gimnasio.modelo.Pago;
import com.gimnasio.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PagoDAOImpl implements PagoDAO {

    @Override
    public void guardar(Pago pago) {
        String sql = "INSERT INTO pago (cliente_id, tipo_membresia_id, fecha_pago, monto, metodo_pago) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, pago.getClienteId());
            ps.setInt(2, pago.getTipoMembresiaId());
            ps.setDate(3, Date.valueOf(pago.getFechaPago()));
            ps.setDouble(4, pago.getMonto());
            ps.setString(5, pago.getMetodoPago());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                pago.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            System.out.println("Error al guardar pago: " + e.getMessage());
        }
    }

    @Override
    public List<Pago> listarTodos() {
        List<Pago> pagos = new ArrayList<>();
        String sql = "SELECT * FROM pago ORDER BY fecha_pago DESC";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pagos.add(mapearPago(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar pagos: " + e.getMessage());
        }

        return pagos;
    }

    @Override
    public List<Pago> listarPorCliente(int clienteId) {
        List<Pago> pagos = new ArrayList<>();
        String sql = "SELECT * FROM pago WHERE cliente_id = ? ORDER BY fecha_pago DESC";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, clienteId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                pagos.add(mapearPago(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar pagos del cliente: " + e.getMessage());
        }

        return pagos;
    }

    @Override
    public double totalRecaudadoPorMes(int anio, int mes) {
        String sql = "SELECT SUM(monto) AS total FROM pago " +
                "WHERE YEAR(fecha_pago) = ? AND MONTH(fecha_pago) = ?";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, anio);
            ps.setInt(2, mes);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total"); // si no hay pagos, SUM() devuelve 0 automáticamente
            }

        } catch (SQLException e) {
            System.out.println("Error al calcular total recaudado: " + e.getMessage());
        }

        return 0;
    }

    @Override
    public Map<String, Integer> contarClientesPorGenero() {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        String sql = "SELECT p.genero, COUNT(*) AS total " +
                "FROM persona p JOIN cliente c ON p.id = c.id " +
                "GROUP BY p.genero";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultado.put(rs.getString("genero"), rs.getInt("total"));
            }

        } catch (SQLException e) {
            System.out.println("Error al contar clientes por género: " + e.getMessage());
        }

        return resultado;
    }

    @Override
    public Map<String, Integer> contarClientesPorRangoEdad() {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        String sql = "SELECT " +
                "  CASE " +
                "    WHEN TIMESTAMPDIFF(YEAR, p.fecha_nacimiento, CURDATE()) < 20 THEN 'Menor de 20' " +
                "    WHEN TIMESTAMPDIFF(YEAR, p.fecha_nacimiento, CURDATE()) BETWEEN 20 AND 30 THEN '20-30' " +
                "    WHEN TIMESTAMPDIFF(YEAR, p.fecha_nacimiento, CURDATE()) BETWEEN 31 AND 45 THEN '31-45' " +
                "    ELSE 'Más de 45' " +
                "  END AS rango_edad, " +
                "  COUNT(*) AS total " +
                "FROM persona p JOIN cliente c ON p.id = c.id " +
                "GROUP BY rango_edad";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultado.put(rs.getString("rango_edad"), rs.getInt("total"));
            }

        } catch (SQLException e) {
            System.out.println("Error al contar clientes por edad: " + e.getMessage());
        }

        return resultado;
    }

    private Pago mapearPago(ResultSet rs) throws SQLException {
        return new Pago(
                rs.getInt("id"),
                rs.getInt("cliente_id"),
                rs.getInt("tipo_membresia_id"),
                rs.getDate("fecha_pago").toLocalDate(),
                rs.getDouble("monto"),
                rs.getString("metodo_pago")
        );
    }
}