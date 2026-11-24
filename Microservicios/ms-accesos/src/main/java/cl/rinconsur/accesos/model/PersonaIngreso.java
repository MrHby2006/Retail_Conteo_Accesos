package cl.rinconsur.accesos.model;

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
@Table(name = "persona_ingreso")
@Schema(description = "Representa la pantalla con todas las funciones que tiene el observador en el sistema")
public class PersonaIngreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona")
    @Schema(description = "Id único del personal de ingreso", example = "9")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_acceso", nullable = false)
    @Schema(description = "Id del acceso seleccionado", example = "2")
    private Acceso acceso;

    @Column(name = "id_observador", nullable = false)
    @Schema(description = "Id del trabajador con el rol de observador", example = "6")
    private Long idObservador;

    @Column(name = "fecha_hora", nullable = false)
    @Schema(description = "Fecha y hora de cuando se realizo la entrada del cliente", example = "22/09/2006 - 13:30")
    private LocalDateTime fechaHora;

    @ManyToOne
    @JoinColumn(name = "id_rango", nullable = false)
    @Schema(description = "Los rangos etarios que el observador puede elegir", example = "Niño (6-10 años)")
    private RangoEtario rango;

    @ManyToOne
    @JoinColumn(name = "id_altura", nullable = false)
    @Schema(description = "Las alturas que el observador puede elegir", example = "Medio (1.50-1.90 cm.)")
    private Altura altura;

    @ManyToOne
    @JoinColumn(name = "id_color_superior", nullable = false)
    @Schema(description = "Los colores que el observador que puede elegir para la prenda superior", example = "Blanco")
    private Color colorSuperior;

    @ManyToOne
    @JoinColumn(name = "id_color_inferior", nullable = false)
    @Schema(description = "Los colores que el observador que puede elegir para la prenda inferior", example = "Azul")
    private Color colorInferior;

    @ManyToOne
    @JoinColumn(name = "id_tipo_compania")
    @Schema(description = "Los tipos de compañia que puede elegir el observador con el que va el cliente", example = "Sola")
    private TipoCompania tipoCompania;

    @Column(length = 150)
    @Schema(description = "Detalle opcional donde el observador puede poner detalles extra para reconocer al cliente")
    private String detalle;

    @Column(nullable = false)
    @Schema(description = "Representa si el cliente compró", example = "Si el cliente compro = True. Si no = False")
    private boolean compro;

    @Column(nullable = false)
    @Schema(description = "Representa si el cliente anulo su compra", example = "Si el cliente si anulo su compra = True. Si no = False")
    private boolean anulado;

    @Column(name = "id_usuario_anula")
    @Schema(description = "Id del cliente que anulo su compra", example = "2")
    private Long idUsuarioAnula;

    @Column(name = "fecha_anulacion")
    @Schema(description = "Fecha y hora en la que el cliente anulo su compra", example = "12/03/2020-12:52")
    private LocalDateTime fechaAnulacion;

    @Column(name = "motivo_anulacion", length = 150)
    @Schema(description = "Motivo edtallado del porque el cliente anulo su compra")
    private String motivoAnulacion;
}
