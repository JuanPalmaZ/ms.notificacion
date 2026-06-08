package cl.paris.marketplace.ms.notificacion.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NotificacionRequest(
    
    @NotBlank(message = "El destinatario es obligatorio")
    @Email(message = "El formato del correo del destinatario no es válido")
    String destinatario,

    @NotBlank(message = "El asunto no puede estar vacío")
    @Size(max = 150, message = "El asunto no puede superar los 150 caracteres")
    String asunto,

    @NotBlank(message = "El mensaje no puede estar vacío")
    @Size(max = 2000, message = "El mensaje no puede superar los 2000 caracteres")
    String mensaje
) {}