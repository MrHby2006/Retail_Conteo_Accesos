package cl.rinconsur.reportes.model;

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "evento_dia")
@Schema(description = "Representa los eventos que ocurren en algunos dias en el sistema")
public class EventoDia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    @Schema(description = "Id único del evento", example = "1")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_tipo_evento", nullable = false)
    @Schema(description = "Tipo de evento", example = "Cyber Day")
    private TipoEvento tipo;

    @Column(name = "fecha_inicio", nullable = false)
    @Schema(description = "Fecha de inicio del evento", example = "04/04/2004")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    @Schema(description = "Fecha de término del evento", example = "06/07/2013")
    private LocalDate fechaFin;

    @Column(name = "id_acceso")
    @Schema(description = "Id del acceso de la compra", example = "2")
    private Long idAcceso;

    @Column(length = 200)
    @Schema(description = "Descripción de la compra")
    private String descripcion;

    @Column(name = "id_usuario_registra", nullable = false)
    @Schema(description = "Id del usuario que se registra en la compra", example = "3")
    private Long idUsuarioRegistra;

    @Column(name = "fecha_registro", nullable = false)
    @Schema(description = "Fecha de registro de la compra", example = "27/10/2023-15:32")
    private LocalDateTime fechaRegistro;
}
