package cl.rinconsur.usuarios.model;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rol")
@Schema(description = "Representa al rol que tiene el usuario dentro del sistema")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    @Schema(description = "Id único del rol", example = "9")
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    @Schema(description = "Código del rol")
    private String codigo;

    @Column(nullable = false, unique = true, length = 40)
    @Schema(description = "Nombre del rol", example = "Admin")
    private String nombre;

    @Column(length = 150)
    @Schema(description = "Descripción detallada de los deberes de el rol")
    private String descripcion;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "rol_permiso",
        joinColumns = @JoinColumn(name = "id_rol"),
        inverseJoinColumns = @JoinColumn(name = "id_permiso")
    )
    @Schema(description = "Representa la lista de permisos que tiene el rol en el sistema")
    private List<Permiso> permisos;
}
