package cl.rinconsur.accesos.model;

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
@Table(name = "persona_ingreso")
public class PersonaIngreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_acceso", nullable = false)
    private Acceso acceso;

    @Column(name = "id_observador", nullable = false)
    private Long idObservador;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @ManyToOne
    @JoinColumn(name = "id_rango", nullable = false)
    private RangoEtario rango;

    @ManyToOne
    @JoinColumn(name = "id_altura", nullable = false)
    private Altura altura;

    @ManyToOne
    @JoinColumn(name = "id_color_superior", nullable = false)
    private Color colorSuperior;

    @ManyToOne
    @JoinColumn(name = "id_color_inferior", nullable = false)
    private Color colorInferior;

    @ManyToOne
    @JoinColumn(name = "id_tipo_compania", nullable = false)
    private TipoCompania tipoCompania;

    @Column(length = 150)
    private String detalle;

    @Column(nullable = false)
    private boolean compro;

    @Column(nullable = false)
    private boolean anulado;

    @Column(name = "id_usuario_anula")
    private Long idUsuarioAnula;

    @Column(name = "fecha_anulacion")
    private LocalDateTime fechaAnulacion;

    @Column(name = "motivo_anulacion", length = 150)
    private String motivoAnulacion;
}
