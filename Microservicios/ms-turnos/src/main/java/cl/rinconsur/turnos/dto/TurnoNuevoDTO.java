package cl.rinconsur.turnos.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoNuevoDTO {

    private Long idUsuario;

    private Long idSupervisor;

    private LocalDate fecha;

    private Long idFranja;

    private Long idPuesto;
}
