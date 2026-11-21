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
@Table(name = "color")
@Schema(description = "Representa al color de las prendas del cliente dentro del sistema")
public class Color {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_color")
    @Schema(description = "Id único de el color", example = "3")
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    @Schema(description = "Nombre del color", example = "Rojo")
    private String nombre;

    @Column(name = "codigo_hex", length = 7)
    @Schema(description = "Código hexadecimal del color", example = "#CD853F")
    private String codigoHex;
}
