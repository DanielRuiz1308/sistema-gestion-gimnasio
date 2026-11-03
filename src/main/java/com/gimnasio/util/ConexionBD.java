
package com.gimnasio.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL =
            System.getenv("DB_URL");

    private static final String USUARIO =
            System.getenv("DB_USER");

    private static final String CONTRASENA =
            System.getenv("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {

        if (URL == null || USUARIO == null || CONTRASENA == null) {
            throw new SQLException(
                    "Faltan las variables de entorno de la base de datos."
            );
        }

        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }
}

