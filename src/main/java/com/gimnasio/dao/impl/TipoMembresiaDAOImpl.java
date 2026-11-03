package com.gimnasio.dao.impl;

import com.gimnasio.dao.TipoMembresiaDAO;
import com.gimnasio.modelo.TipoMembresia;
import com.gimnasio.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoMembresiaDAOImpl implements TipoMembresiaDAO {

    @Override
    public List<TipoMembresia> listarTodos() {
        List<TipoMembresia> tipos = new ArrayList<>();
        String sql = "SELECT * FROM tipo_membresia ORDER BY precio";

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tipos.add(new TipoMembresia(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar tipos de membresía: " + e.getMessage());
        }

        return tipos;
    }
}
