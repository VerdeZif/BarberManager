package com.barbermanager.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.Map;

public final class Jpa {
    private Jpa() { }
    private static final class Holder {
        static final EntityManagerFactory FACTORY = crear();
    }
    private static EntityManagerFactory crear() {
        String url = System.getenv("BARBER_DB_URL");
        String usuario = System.getenv("BARBER_DB_USER");
        String clave = System.getenv("BARBER_DB_PASSWORD");
        if (url == null || url.isBlank() || usuario == null || usuario.isBlank() || clave == null) {
            throw new IllegalStateException("Configure BARBER_DB_URL, BARBER_DB_USER y BARBER_DB_PASSWORD");
        }
        return Persistence.createEntityManagerFactory("barbermanager", Map.of(
                "jakarta.persistence.jdbc.url", url,
                "jakarta.persistence.jdbc.user", usuario,
                "jakarta.persistence.jdbc.password", clave));
    }
    public static EntityManagerFactory factory() { return Holder.FACTORY; }
}
