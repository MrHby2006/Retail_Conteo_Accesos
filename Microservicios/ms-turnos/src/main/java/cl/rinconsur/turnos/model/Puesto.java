package cl.rinconsur.turnos.model;

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
@Table(name = "puesto")
@Schema(description = "Representa al puesto del trabaja")
public class Puesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_puesto")
    @Schema(description = "Id único del puesto", example = "3")
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    @Schema(description = "Código del puesto", example = "ADX189")
    private String codigo;

    @Column(nullable = false, length = 40)
    @Schema(description = "Nombre del puesto")
    private String nombre;

    @Column(nullable = false, length = 10)
    @Schema(description = "Tipo de puesto")
    private String tipo;

    @Column(name = "id_acceso")
    @Schema(description = "Id del acceso correspondiente", example = "6")
    private Long idAcceso;

    @Column(nullable = false)
    @Schema(description = "Representa si el puesto esta activo o no. Si esta activo = True. Si no = False")
    private boolean activo;
}
