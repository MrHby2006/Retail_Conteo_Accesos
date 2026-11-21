package cl.rinconsur.accesos.model;

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
@Table(name = "rango_etario")
@Schema(description = "Representa el rango etario del cliente dentro del sistema")
public class RangoEtario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rango")
    @Schema(description = "Id único del rango etario", example = "7")
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    @Schema(description = "Nombre del rango etario", example = "Adolecente (12-18 años)")
    private String nombre;

    @Column(name = "edad_min", nullable = false)
    @Schema(description = "Edad mínima del rango que desea guardar", example = "12")
    private int edadMin;

    @Column(name = "edad_max", nullable = false)
    @Schema(description = "Edad máxima del rango que desea guardar", example = "18")
    private int edadMax;

    @Column(nullable = false)
    @Schema(description = "Orden en el que se presentan los rangos etarios")
    private int orden;
}
