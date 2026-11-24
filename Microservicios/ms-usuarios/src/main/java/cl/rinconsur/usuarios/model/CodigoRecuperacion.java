package cl.rinconsur.usuarios.model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "codigo_recuperacion")
@Schema(description = "Representa al código derecuperación de cuenta dentro del sistema")
public class CodigoRecuperacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_codigo")
    @Schema(description = "Id único del código", example = "2")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    @Schema(description = "Representa al usuario que presento el código de recuperación")
    private Usuario usuario;

    @Column(nullable = false, length = 6)
    @Schema(description = "Código de recuperación", example = "HX421S")
    private String codigo;

    @Column(name = "fecha_creacion", nullable = false)
    @Schema(description = "Fecha de la creación del código de recuperación", example = "24/05/2000-17:32")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_expira", nullable = false)
    @Schema(description = "Fecha en la que expira el código de recuperación", example = "28/02/2019-19:30")
    private LocalDateTime fechaExpira;

    @Column(nullable = false)
    @Schema(description = "Representa si el código fue utilizado o no. Si el código fue utilizado = True. Si no = False")
    private boolean usado;
}
