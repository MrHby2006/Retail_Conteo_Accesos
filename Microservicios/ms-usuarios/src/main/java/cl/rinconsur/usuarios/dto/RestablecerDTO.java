package cl.rinconsur.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestablecerDTO {

    private String rut;

    private String codigo;

    private String nueva;
}
