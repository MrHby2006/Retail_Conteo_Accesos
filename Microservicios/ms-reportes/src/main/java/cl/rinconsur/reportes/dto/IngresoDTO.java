package cl.rinconsur.reportes.dto;

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

    private LocalDateTime fechaHora;

    private String rango;

    private String tipoCompania;

    private boolean compro;
}
