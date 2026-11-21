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
@Table(name = "estado_solicitud")
@Schema(description = "Representa el estado de la solicitud en el sistema")
public class EstadoSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado")
    @Schema(description = "Id único del estado de la solicitud", example = "4")
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    @Schema(description = "Nombre del estado de la solicitud", example = "Pendiente")
    private String nombre;
}
