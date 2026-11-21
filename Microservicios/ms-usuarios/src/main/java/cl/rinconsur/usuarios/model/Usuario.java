package cl.rinconsur.usuarios.model;

import java.time.LocalDate;
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
@Table(name = "usuario")
@Schema(description = "Representa al usuario dentro del sistema")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    @Schema(description = "Id punico del usuario", example = "10")
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    @Schema(description = "Código de identificación del usuario")
    private String codigo;

    @Column(nullable = false, unique = true, length = 12)
    @Schema(description = "Rut del usuario", example = "12051788-0")
    private String rut;

    @Column(nullable = false, length = 60)
    @Schema(description = "Nombres del usuario", example = "Juan Carlos")
    private String nombres;

    @Column(name = "apellido_paterno", nullable = false, length = 40)
    @Schema(description = "Apellido paterno del usuario", example = "Fernández")
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 40)
    @Schema(description = "Apellido materno del usuario", example = "González")
    private String apellidoMaterno;

    @Column(nullable = false, length = 60)
    @Schema(description = "Cargo del usuario")
    private String cargo;

    @Column(nullable = false, unique = true, length = 100)
    @Schema(description = "Correo laboral del usuario", example = "juanca@rinconsur.cl")
    private String correo;

    @Column(name = "correo_personal", unique = true, length = 100)
    @Schema(description = "Correo personal del usuario", example = "Juan2006@gmail.com")
    private String correoPersonal;

    @Column(length = 15)
    @Schema(description = "+56 9 18081079")
    private String telefono;

    @Column(name = "password_hash", nullable = false)
    @Schema(description = "Contraseña del usuario", example = "gomarojanosemoja")
    private String passwordHash;

    @Column(name = "debe_cambiar_contrasena", nullable = false)
    @Schema(description = "Representa en caso de que la ocntraseña sea erronea y deba cambiarse por otra. En caso de que deba cambiarse = True. En caso de que no = False")
    private boolean debeCambiarContrasena;

    @Column(name = "fecha_nacimiento")
    @Schema(description = "Fecha de nacimiento del usuario", example = "22/09/1990")
    private LocalDate fechaNacimiento;

    @Column(name = "foto_url")
    @Schema(description = "URL donde se guarda la imagen del usuario")
    private String fotoUrl;

    @Column(nullable = false)
    @Schema(description = "Representa si el usuario esta activo en ese momento. Si esta activo = True. Si no = False")
    private boolean activo;

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    @Schema(description = "Rol que tiene el usuario dentro del sistema")
    private Rol rol;

    @Column(name = "fecha_creacion", nullable = false)
    @Schema(description = "Fecha de creación del usuario", example = "13/07/2014-15:37")
    private LocalDateTime fechaCreacion;
}
