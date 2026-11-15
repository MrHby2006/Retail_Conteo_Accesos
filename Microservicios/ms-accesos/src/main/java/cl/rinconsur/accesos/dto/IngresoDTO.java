package cl.rinconsur.accesos.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngresoDTO {

    private Long id;

    private Long idAcceso;

    private String codigoAcceso;

    private Long idObservador;

    private LocalDateTime fechaHora;

    private String rango;

    private String altura;

    private String colorSuperior;

    private String colorInferior;

    private String tipoCompania;

    private String detalle;

    private boolean compro;

    private boolean anulado;
}
