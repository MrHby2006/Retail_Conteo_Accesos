package cl.rinconsur.usuarios.model;

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
@Table(name = "permiso")
@Schema(description = "Representa los permisos de los roles dentro del sistema")
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permiso")
    @Schema(description = "Id único del permiso", example = "8")
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    @Schema(description = "Código del permiso")
    private String codigo;

    @Column(length = 150)
    @Schema(description = "Descripción detallada de lo que puede hacer el usuario con el permiso")
    private String descripcion;
}
