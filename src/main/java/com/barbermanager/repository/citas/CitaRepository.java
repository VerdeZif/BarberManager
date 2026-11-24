package com.barbermanager.repository.citas;

import com.barbermanager.model.Cita;
import com.barbermanager.repository.RepositoryBase;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class CitaRepository extends RepositoryBase<Cita> {
    public CitaRepository(EntityManager em) {
        super(em, Cita.class);
    }

    /** La consulta debe ejecutarse con el barbero bloqueado en la misma transacción. */
    public List<Cita> buscarSolapadas(Long barberoId, LocalDateTime inicio, LocalDateTime fin) {
        return em.createQuery("select c from Cita c where c.barbero.id = :id and c.inicio < :fin and c.fin > :inicio and c.estado not in ('CANCELADA', 'NO_ASISTIO')", Cita.class)
                .setParameter("id", barberoId).setParameter("inicio", inicio).setParameter("fin", fin).getResultList();
    }
}
