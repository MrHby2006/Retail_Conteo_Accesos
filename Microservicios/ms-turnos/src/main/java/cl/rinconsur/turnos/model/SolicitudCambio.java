package cl.rinconsur.turnos.model;

import java.time.LocalDateTime;

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
public class SolicitudCambio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_turno", nullable = false)
    private Turno turno;

    @Column(name = "id_supervisor", nullable = false)
    private Long idSupervisor;

    @Column(name = "id_usuario_nuevo")
    private Long idUsuarioNuevo;

    @ManyToOne
    @JoinColumn(name = "id_franja_nueva")
    private Franja franjaNueva;

    @ManyToOne
    @JoinColumn(name = "id_puesto_nuevo")
    private Puesto puestoNuevo;

    @Column(name = "acepta_titular", nullable = false)
    private boolean aceptaTitular;

    @Column(name = "acepta_nuevo", nullable = false)
    private boolean aceptaNuevo;

    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    private EstadoSolicitud estado;

    @Column(length = 150)
    private String motivo;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;
}
