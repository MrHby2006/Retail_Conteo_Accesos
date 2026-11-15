package cl.rinconsur.notificaciones.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDTO {

    private Long id;

    private String tipo;

    private Long idUsuario;

    private String destinatario;

    private String asunto;

    private boolean enviado;

    private String error;

    private LocalDateTime fechaEnvio;
}
