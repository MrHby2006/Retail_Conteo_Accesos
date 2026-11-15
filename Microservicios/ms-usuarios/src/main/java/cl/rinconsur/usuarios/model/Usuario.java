package cl.rinconsur.usuarios.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String codigo;

    @Column(nullable = false, unique = true, length = 12)
    private String rut;

    @Column(nullable = false, length = 60)
    private String nombres;

    @Column(name = "apellido_paterno", nullable = false, length = 40)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 40)
    private String apellidoMaterno;

    @Column(nullable = false, length = 60)
    private String cargo;

    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    @Column(name = "correo_personal", unique = true, length = 100)
    private String correoPersonal;

    @Column(length = 15)
    private String telefono;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "debe_cambiar_contrasena", nullable = false)
    private boolean debeCambiarContrasena;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "foto_url")
    private String fotoUrl;

    @Column(nullable = false)
    private boolean activo;

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
}
