package cl.rinconsur.turnos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudNuevaDTO {

    private Long idTurno;

    private Long idSupervisor;

    private Long idUsuarioNuevo;

    private Long idFranjaNueva;

    private Long idPuestoNuevo;

    private String motivo;
}
