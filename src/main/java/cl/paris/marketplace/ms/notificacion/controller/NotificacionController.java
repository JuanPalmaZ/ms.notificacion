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

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    // ==========================================
    // CREAR NOTIFICACIÓN (Llamado internamente por Feign desde Ventas o Tickets)
    // ==========================================
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
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificacionResponse>> listarTodas() {
        return ResponseEntity.ok(notificacionService.listarTodas());
    }
}