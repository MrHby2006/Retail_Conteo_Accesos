package cl.rinconsur.turnos.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CorreoMasivoDTO {

    private String tipo;

    private List<String> destinatarios;

    private String asunto;

    private String mensaje;
}
