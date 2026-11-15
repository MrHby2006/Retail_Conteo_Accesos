package cl.rinconsur.turnos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicacionResultadoDTO {

    private int publicados;

    private boolean correoEnviado;

    private int destinatarios;
}
