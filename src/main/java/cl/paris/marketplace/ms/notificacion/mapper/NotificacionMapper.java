package cl.paris.marketplace.ms.notificacion.mapper;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

import cl.paris.marketplace.ms.notificacion.dto.NotificacionRequest;
import cl.paris.marketplace.ms.notificacion.dto.NotificacionResponse;
import cl.paris.marketplace.ms.notificacion.model.EstadoNotificacion;
import cl.paris.marketplace.ms.notificacion.model.Notificacion;

@Component
public class NotificacionMapper {

    public Notificacion toEntity(NotificacionRequest request) {
        if (request == null) return null;

        return Notificacion.builder()
                .destinatario(request.destinatario())
                .asunto(request.asunto())
                .mensaje(request.mensaje())
                .estado(EstadoNotificacion.PENDIENTE)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    public NotificacionResponse toResponse(Notificacion notificacion) {
        if (notificacion == null) return null;

        return new NotificacionResponse(
                notificacion.getId(),
                notificacion.getDestinatario(),
                notificacion.getAsunto(),
                notificacion.getMensaje(),
                notificacion.getEstado().name(), // Convertimos el Enum a String
                notificacion.getFechaCreacion(),
                notificacion.getFechaEnvio()
        );
    }
}