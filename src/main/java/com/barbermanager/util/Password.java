package com.barbermanager.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class Password {
    private static final int ITERACIONES = 600_000;
    private static final SecureRandom RANDOM = new SecureRandom();
    private Password() { }

    public static String hash(char[] password) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return "pbkdf2-sha256$" + ITERACIONES + "$" + Base64.getEncoder().encodeToString(salt)
                + "$" + Base64.getEncoder().encodeToString(derivar(password, salt, ITERACIONES));
    }

    public static boolean verificar(char[] password, String hash) {
        try {
            String[] partes = hash.split("\\$", -1);
            if (partes.length != 4 || !partes[0].equals("pbkdf2-sha256")) return false;
            int iteraciones = Integer.parseInt(partes[1]);
            if (iteraciones < 1 || iteraciones > 2_000_000) return false;
            return MessageDigest.isEqual(Base64.getDecoder().decode(partes[3]),
                    derivar(password, Base64.getDecoder().decode(partes[2]), iteraciones));
        } catch (IllegalArgumentException | NullPointerException e) { return false; }
    }

    private static byte[] derivar(char[] password, byte[] salt, int iteraciones) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iteraciones, 256);
        try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(); }
        catch (java.security.GeneralSecurityException e) { throw new IllegalStateException("No se pudo procesar la contraseña", e); }
        finally { spec.clearPassword(); }
    }
}
