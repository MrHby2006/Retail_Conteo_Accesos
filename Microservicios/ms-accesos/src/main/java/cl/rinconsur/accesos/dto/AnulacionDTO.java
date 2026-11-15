package cl.rinconsur.accesos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnulacionDTO {

    private Long idUsuario;

    private String motivo;
}
