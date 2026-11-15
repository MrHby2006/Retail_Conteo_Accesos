package cl.rinconsur.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoDTO {

    private String nombres;

    private String apellidoPaterno;

    private String apellidoMaterno;

    private String rut;

    private String cargo;

    private String codigoRol;

    private String correoPersonal;

    private String telefono;
}
