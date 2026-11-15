package cl.rinconsur.usuarios.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    private Long id;

    private String codigo;

    private String rut;

    private String nombres;

    private String apellidoPaterno;

    private String apellidoMaterno;

    private String nombreCompleto;

    private String cargo;

    private String correo;

    private String correoPersonal;

    private String telefono;

    private String codigoRol;

    private String nombreRol;

    private boolean debeCambiarContrasena;

    private LocalDate fechaNacimiento;

    private String fotoUrl;

    private boolean activo;
}
