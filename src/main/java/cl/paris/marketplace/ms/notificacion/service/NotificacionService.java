package cl.paris.marketplace.ms.notificacion.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.paris.marketplace.ms.notificacion.dto.NotificacionRequest;
import cl.paris.marketplace.ms.notificacion.dto.NotificacionResponse;
import cl.paris.marketplace.ms.notificacion.mapper.NotificacionMapper;
import cl.paris.marketplace.ms.notificacion.model.EstadoNotificacion;
import cl.paris.marketplace.ms.notificacion.model.Notificacion;
import cl.paris.marketplace.ms.notificacion.repository.NotificacionRepository;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final NotificacionMapper notificacionMapper;

    // Inyección por constructor (Arquitectura estricta)
    public NotificacionService(NotificacionRepository notificacionRepository, NotificacionMapper notificacionMapper) {
        this.notificacionRepository = notificacionRepository;
        this.notificacionMapper = notificacionMapper;
    }

    // ==========================================
    // CREACIÓN Y ENVÍO SIMULADO
    // ==========================================
    @Transactional
    public NotificacionResponse crearNotificacion(NotificacionRequest request) {
        Notificacion notificacion = notificacionMapper.toEntity(request);
        
        // Lógica de envío real (ej. JavaMailSender, AWS SES o Twilio)
        // Por ahora, simulamos que el sistema logró despachar el correo con éxito al instante.
        notificacion.setEstado(EstadoNotificacion.ENVIADO);
        notificacion.setFechaEnvio(LocalDateTime.now());
        
        Notificacion notificacionGuardada = notificacionRepository.save(notificacion);
        return notificacionMapper.toResponse(notificacionGuardada);
    }

    // ==========================================
    // CONSULTAS BLINDADAS
    // ==========================================
    @Transactional(readOnly = true)
    public List<NotificacionResponse> obtenerMisNotificaciones(String emailDestinatario) {
        List<Notificacion> notificaciones = notificacionRepository
                .findByDestinatarioOrderByFechaCreacionDesc(emailDestinatario);
        
        if (notificaciones.isEmpty()) {
            throw new RuntimeException("Tu bandeja de entrada está vacía. No tienes notificaciones nuevas.");
        }

        return notificaciones.stream()
                .map(notificacionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponse> listarTodas() {
        return notificacionRepository.findAll().stream()
                .map(notificacionMapper::toResponse)
                .toList();
    }
}