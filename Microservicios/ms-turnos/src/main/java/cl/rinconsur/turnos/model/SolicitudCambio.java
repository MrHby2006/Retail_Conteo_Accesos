package cl.rinconsur.turnos.model;

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
@Table(name = "solicitud_cambio")
@Schema(description = "Representa cuando la solicitud cambia dentro del sistema")
public class SolicitudCambio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    @Schema(description = "Id único de la solicitud cambiada", example = "7")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_turno", nullable = false)
    @Schema(description = "Los turnos que tiene y que pueden cambiar")
    private Turno turno;

    @Column(name = "id_supervisor", nullable = false)
    @Schema(description = "Id del supervisor", example = "6")
    private Long idSupervisor;

    @Column(name = "id_usuario_nuevo")
    @Schema(description = "Id nuevo del usuario", example = "7")
    private Long idUsuarioNuevo;

    @ManyToOne
    @JoinColumn(name = "id_franja_nueva")
    @Schema(description = "Franja que se busca cambiar en la solicitud")
    private Franja franjaNueva;

    @ManyToOne
    @JoinColumn(name = "id_puesto_nuevo")
    @Schema(description = "Nuevo puesto que se desea crear")
    private Puesto puestoNuevo;

    @Column(name = "acepta_titular", nullable = false)
    @Schema(description = "Representa si el titularacepto el cambio. Si el titular acepto = True. Si no = False")
    private boolean aceptaTitular;

    @Column(name = "acepta_nuevo", nullable = false)
    @Schema(description = "Representa si se acepto la nueva solicitud. Si se acepto lasolicitud = True. Si no = False")
    private boolean aceptaNuevo;

    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    @Schema(description = "Estado de la solicitud", example = "Aceptada")
    private EstadoSolicitud estado;

    @Column(length = 150)
    @Schema(description = "Motivo del porque se hace la solicitud del cambio")
    private String motivo;

    @Column(name = "fecha_creacion", nullable = false)
    @Schema(description = "Fecha de la creacion de la solicitud", example = "23/02/2005-13:51")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_resolucion")
    @Schema(description = "Fecha de resolución de la solicitud", example = "15/07/2025-17:30")
    private LocalDateTime fechaResolucion;
}
