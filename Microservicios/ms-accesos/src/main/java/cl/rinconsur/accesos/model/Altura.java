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
@Table(name = "altura")
@Schema(description = "Representa los atributos de la altura de un cliente dentro del sistema")
public class Altura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_altura")
    @Schema(description = "Id único del tipo de altura", example = "5")
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    @Schema(description = "Nombre de la altura", example = "1.50 a 1.90 cm.")
    private String nombre;

    @Column(nullable = false)
    @Schema(description = "Orden en el que va la altura")
    private int orden;
}
