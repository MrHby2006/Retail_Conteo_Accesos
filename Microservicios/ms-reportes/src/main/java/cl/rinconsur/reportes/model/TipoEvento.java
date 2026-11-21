package cl.rinconsur.reportes.model;

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
@Table(name = "tipo_evento")
@Schema(description = "Representa el tipo de eventos que ocurren en la tienda en el sistema")
public class TipoEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_evento")
    @Schema(description = "Id único del tipo de evento", example = "6")
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    @Schema(description = "Nombre del evento", example = "Cyber Day")
    private String nombre;
}
