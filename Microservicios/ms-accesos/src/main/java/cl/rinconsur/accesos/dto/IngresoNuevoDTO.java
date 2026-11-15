package cl.rinconsur.accesos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngresoNuevoDTO {

    private Long idAcceso;

    private Long idObservador;

    private Long idRango;

    private Long idAltura;

    private Long idColorSuperior;

    private Long idColorInferior;

    private Long idTipoCompania;

    private String detalle;
}
