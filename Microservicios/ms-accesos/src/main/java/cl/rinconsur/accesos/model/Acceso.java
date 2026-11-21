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
@Table(name = "acceso")
@Schema(description = "Representa uno de los accesos dentros del sistema")
public class Acceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acceso")
    @Schema(description = "Id único del acceso", example = "3")
    private Long id;

    @Column(nullable = false, unique = true, length = 2)
    @Schema(description = "Código del acceso", example = "A1")
    private String codigo;

    @Column(nullable = false, length = 50)
    @Schema(description = "Nombre para reconocer el acceso", example = "Acceso Principal")
    private String nombre;

    @Column(length = 150)
    @Schema(description = "Ubicación más detallada de donde esta el acceso", example = "Costado del Patio de Comida")
    private String ubicacion;

    @Column(nullable = false)
    @Schema(description = "Representa si el acceso esta en funcionamiento o fue desactivado", example = "Si esta en funcionamiento = True. Si no = False")
    private boolean activo;
}
