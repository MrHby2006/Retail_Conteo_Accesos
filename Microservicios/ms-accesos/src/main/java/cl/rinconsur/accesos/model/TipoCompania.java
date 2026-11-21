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
@Table(name = "tipo_compania")
@Schema(description = "Representa el tipo de compañia que tiene el cliente dentro del sistema")
public class TipoCompania {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_compania")
    @Schema(description = "Id único del tipo de compañia", example = "5")
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    @Schema(description = "Nombre del tipo de compañia", example = "Sola")
    private String nombre;
}
