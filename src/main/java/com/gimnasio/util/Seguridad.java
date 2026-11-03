package com.gimnasio.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

// Las contraseñas nunca se guardan en texto plano: se guarda un "hash" (huella irreversible)
// calculado con SHA-256 sobre (sal + contraseña). La sal es un texto aleatorio distinto por usuario.
public class Seguridad {

    public static String generarSal() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return aHex(bytes);
    }

    public static String hash(String sal, String contrasena) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] resultado = md.digest((sal + contrasena).getBytes(StandardCharsets.UTF_8));
            return aHex(resultado);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    private static String aHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}