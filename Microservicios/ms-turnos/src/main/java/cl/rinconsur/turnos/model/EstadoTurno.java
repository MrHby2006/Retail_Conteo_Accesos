package cl.rinconsur.turnos.model;

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
@Table(name = "estado_turno")
@Schema(description = "Representa el estado del turno en el sistema")
public class EstadoTurno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado")
    @Schema(description = "Id único del estado del turno", example = "9")
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    @Schema(description = "Nombre del estado del turno", example = "En Proceso")
    private String nombre;
}
