package cl.paris.marketplace.ms.notificacion.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import cl.paris.marketplace.ms.notificacion.dto.NotificacionRequest;
import cl.paris.marketplace.ms.notificacion.dto.NotificacionResponse;
import cl.paris.marketplace.ms.notificacion.service.NotificacionService;
import jakarta.validation.Valid;

// Imports añadidos según la pauta
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/notificaciones")
@Tag(name = "Notificaciones", description = "EndPoints para la gestión y envío de notificaciones del Marketplace")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    // ==========================================
    // CREAR NOTIFICACIÓN (Llamado internamente por Feign desde Ventas o Tickets)
    // ==========================================
    @Operation(summary = "Crear notificación", description = "Permite registrar y enviar una nueva notificación. Endpoint llamado internamente vía Feign.")
    @ApiResponse(responseCode = "201", description = "Notificación creada exitosamente")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Cuerpo de la carga útil con la información de la notificación")
    @ExampleObject(
        name = "Ejemplo de Notificación",
        value = "{\n  \"destinatario\": \"cliente@paris.cl\",\n  \"asunto\": \"Confirmación de Compra\",\n  \"mensaje\": \"Tu pedido ha sido procesado con éxito.\"\n}"
    )
    @PostMapping
    @PreAuthorize("isAuthenticated()") 
    public ResponseEntity<NotificacionResponse> crearNotificacion(
            @Valid @RequestBody NotificacionRequest request) {
        
        NotificacionResponse response = notificacionService.crearNotificacion(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ==========================================
    // BANDEJA DE ENTRADA SEGURA E INVISIBLE
    // ==========================================
    @Operation(summary = "Obtener notificaciones del usuario", description = "Recupera el listado de notificaciones asociadas al usuario autenticado a través del Token.")
    @ApiResponse(responseCode = "200", description = "Lista de notificaciones obtenida con éxito")
    @ApiResponse(responseCode = "500", description = "Error Crítico: No se pudo extraer el correo electrónico del token.")
    @ApiResponse(responseCode = "404", description = "No se encontraron notificaciones o error en el servicio")
    @GetMapping("/mis-notificaciones")
    @PreAuthorize("isAuthenticated()") // Cualquier usuario logueado puede revisar su propia bandeja
    public ResponseEntity<?> misNotificaciones(Authentication authentication) {
        try {
            // En JwtAuthenticationFilter, el correo se guardó en el Principal (Name)
            String miEmail = authentication.getName(); 
            
            if (miEmail == null || miEmail.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error Crítico: No se pudo extraer el correo electrónico del token.");
            }
            
            // Le pasamos al servicio el correo criptográfico del Token, no de la URL
            List<NotificacionResponse> response = notificacionService.obtenerMisNotificaciones(miEmail);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
        }
    }

    // ==========================================
    // HISTORIAL GLOBAL (Solo ADMIN)
    // ==========================================
    @Operation(summary = "Historial global de notificaciones", description = "Permite obtener el listado completo de todas las notificaciones del sistema. Restringido para rol ADMIN.")
    @ApiResponse(responseCode = "200", description = "Historial global obtenido con éxito")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificacionResponse>> listarTodas() {
        return ResponseEntity.ok(notificacionService.listarTodas());
    }
}