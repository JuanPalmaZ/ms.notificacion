package cl.paris.marketplace.ms.notificacion.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificacionResponse(
    UUID id,
    String destinatario,
    String asunto,
    String mensaje,
    String estado,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaEnvio
) {}