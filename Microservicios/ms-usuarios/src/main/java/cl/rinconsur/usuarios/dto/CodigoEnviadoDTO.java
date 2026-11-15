package cl.rinconsur.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodigoEnviadoDTO {

    private String correoOculto;

    private boolean correoEnviado;

    private int minutosValidez;
}
