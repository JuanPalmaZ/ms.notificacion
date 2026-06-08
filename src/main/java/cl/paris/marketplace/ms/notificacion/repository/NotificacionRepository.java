package cl.paris.marketplace.ms.notificacion.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.paris.marketplace.ms.notificacion.model.Notificacion;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, UUID> {
    
    // Busca las notificaciones de un correo específico, ordenadas por la más reciente primero
    List<Notificacion> findByDestinatarioOrderByFechaCreacionDesc(String destinatario);
}