package cl.rinconsur.notificaciones.model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notificacion")
@Schema(description = "Representa las notificaciones dentro del sistema")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    @Schema(description = "Id único de la notificación", example = "4")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_tipo", nullable = false)
    @Schema(description = "Tipo de notificación", example = "Alerta")
    private TipoNotificacion tipo;

    @Column(name = "id_usuario")
    @Schema(description = "Id del usuario al que le llega la notificación", example = "6")
    private Long idUsuario;

    @Column(nullable = false, length = 100)
    @Schema(description = "Nombre del usuario al que le llegara la notificación", example = "German Garmendia")
    private String destinatario;

    @Column(nullable = false, length = 150)
    @Schema(description = "Mensaje que entrega notificación")
    private String asunto;

    @Column(nullable = false)
    @Schema(description = "Representa si se a enviado la notificación. Si se a enviando = True. Si no = False")
    private boolean enviado;

    @Column(length = 255)
    @Schema(description = "Mensaje en caso de que ocurra error")
    private String error;

    @Column(name = "fecha_envio", nullable = false)
    @Schema(description = "Fecha en la que se envió la notificación", example = "09/05/2010-14:59")
    private LocalDateTime fechaEnvio;
}
