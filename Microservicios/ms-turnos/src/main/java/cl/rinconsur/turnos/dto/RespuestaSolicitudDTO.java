package cl.rinconsur.turnos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaSolicitudDTO {

    private Long idUsuario;

    private boolean acepta;
}
