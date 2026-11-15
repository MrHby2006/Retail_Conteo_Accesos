package cl.rinconsur.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CorreoDTO {

    private String tipo;

    private Long idUsuario;

    private String destinatario;

    private String asunto;

    private String mensaje;
}
