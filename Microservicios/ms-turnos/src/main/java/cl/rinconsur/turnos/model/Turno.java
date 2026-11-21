package cl.rinconsur.turnos.model;

import java.time.LocalDate;
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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "turno", uniqueConstraints = @UniqueConstraint(columnNames = {"id_usuario", "fecha", "id_franja"}))
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_turno")
    @Schema(description = "Id único del turno", example = "4")
    private Long id;

    @Column(name = "id_usuario", nullable = false)
    @Schema(description = "Id del usuario asignado", example = "2")
    private Long idUsuario;

    @Column(name = "id_supervisor", nullable = false)
    @Schema(description = "Id del supervisor que asigna", example = "4")
    private Long idSupervisor;

    @Column(nullable = false)
    @Schema(description = "Fecha del turno", example = "12/08/2008")
    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(name = "id_franja", nullable = false)
    @Schema(description = "Franja en la que ocurre el turno")
    private Franja franja;

    @ManyToOne
    @JoinColumn(name = "id_puesto", nullable = false)
    @Schema(description = "Puesto en el que ocurre el turno")
    private Puesto puesto;

    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    @Schema(description = "Estado en el que esta el turno")
    private EstadoTurno estado;

    @Column(name = "fecha_creacion", nullable = false)
    @Schema(description = "Fecha de creación del turno", example = "13/12/2026-16:27")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_publicacion")
    @Schema(description = "Fecha de publicacion de los turnos", example = "12/01/2017-13:50")
    private LocalDateTime fechaPublicacion;
}
