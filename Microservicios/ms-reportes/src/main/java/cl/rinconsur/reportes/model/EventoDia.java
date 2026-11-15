package cl.rinconsur.reportes.model;

import java.time.LocalDate;
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
@Table(name = "evento_dia")
public class EventoDia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_tipo_evento", nullable = false)
    private TipoEvento tipo;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "id_acceso")
    private Long idAcceso;

    @Column(length = 200)
    private String descripcion;

    @Column(name = "id_usuario_registra", nullable = false)
    private Long idUsuarioRegistra;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;
}
