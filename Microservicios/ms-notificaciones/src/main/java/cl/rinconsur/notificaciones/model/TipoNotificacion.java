package cl.rinconsur.notificaciones.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tipo_notificacion")
@Schema(description = "Representa el tipo de notificación dentro edl sistema")
public class TipoNotificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo")
    @Schema(description = "Id único del tipo de notificación", example = "9")
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    @Schema(description = "Nombre del tipo de notificación", example = "Alerta")
    private String nombre;
}
