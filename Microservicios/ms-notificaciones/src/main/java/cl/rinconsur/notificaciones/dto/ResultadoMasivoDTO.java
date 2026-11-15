package cl.rinconsur.notificaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoMasivoDTO {

    private boolean enviado;

    private int destinatarios;

    private String error;
}
