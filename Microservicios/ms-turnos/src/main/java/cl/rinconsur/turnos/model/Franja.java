package cl.rinconsur.turnos.model;

import java.time.LocalTime;

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
@Table(name = "franja")
@Schema(description = "Representa la franja horaica dentro del sistema")
public class Franja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_franja")
    @Schema(description = "Id único de la franja", example = "1")
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    @Schema(description = "Nombred ela franja", example = "Tarde")
    private String nombre;

    @Column(name = "hora_inicio", nullable = false)
    @Schema(description = "Hora de inicio de la franja", example = "08:30")
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    @Schema(description = "Hora de termino de la franja", example = "13:00")
    private LocalTime horaFin;
}
