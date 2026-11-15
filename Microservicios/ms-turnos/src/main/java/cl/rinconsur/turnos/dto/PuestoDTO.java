package cl.rinconsur.turnos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PuestoDTO {

    private Long id;

    private String codigo;

    private String nombre;

    private String tipo;

    private Long idAcceso;
}
