package cl.rinconsur.turnos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    private Long id;

    private String nombres;

    private String nombreCompleto;

    private String codigoRol;

    private String correo;

    private boolean activo;
}
