package cl.rinconsur.reportes.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "meta_conversion", uniqueConstraints = @UniqueConstraint(columnNames = {"anio", "mes"}))
@Schema(description = "Representa a la conversión a metrica dentro del sistema")
public class MetaConversion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_meta")
    @Schema(description = "Id único de la conversión", example = "7")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Año de la conversión", example = "2006")
    private int anio;

    @Column(nullable = false)
    @Schema(description = "Mes de la conversión", example = "07")
    private int mes;

    @Column(nullable = false, precision = 5, scale = 2)
    @Schema(description = "Porcentaje de la conversión", example = "56.7")
    private BigDecimal porcentaje;

    @Column(name = "id_usuario_registra", nullable = false)
    @Schema(description = "Id del usuario de la conversión", example = "4")
    private Long idUsuarioRegistra;

    @Column(name = "fecha_registro", nullable = false)
    @Schema(description = "Fecha de registro de la conversión", example = "27/05/2024-20:00")
    private LocalDateTime fechaRegistro;
}
