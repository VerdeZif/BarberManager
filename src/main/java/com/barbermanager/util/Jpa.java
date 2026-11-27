
package com.barbermanager.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Map;

public final class Jpa {

    private Jpa() {
    }

    private static final class Holder {
        static final EntityManagerFactory FACTORY = crear();
    }

    private static EntityManagerFactory crear() {

        String url = System.getenv("BARBER_DB_URL");
        String usuario = System.getenv("BARBER_DB_USER");
        String clave = System.getenv("BARBER_DB_PASSWORD");

        // Permitir contraseña vacía solo cuando se
        // autorice explícitamente en el entorno local.
        boolean permitirClaveVacia = Boolean.parseBoolean(
                System.getenv("BARBER_DB_ALLOW_EMPTY_PASSWORD")
        );

        if (url == null || url.isBlank()
                || usuario == null || usuario.isBlank()) {

            throw new IllegalStateException(
                    "Configure BARBER_DB_URL y BARBER_DB_USER."
            );
        }

        if (clave == null) {

            if (!permitirClaveVacia) {
                throw new IllegalStateException(
                        "Configure BARBER_DB_PASSWORD o habilite " +
                                "BARBER_DB_ALLOW_EMPTY_PASSWORD para desarrollo local."
                );
            }

            clave = "";
        }

        return Persistence.createEntityManagerFactory(
                "barbermanager",
                Map.of(
                        "jakarta.persistence.jdbc.url", url,
                        "jakarta.persistence.jdbc.user", usuario,
                        "jakarta.persistence.jdbc.password", clave
                )
        );
    }

    public static EntityManagerFactory factory() {
        return Holder.FACTORY;
    }
}
