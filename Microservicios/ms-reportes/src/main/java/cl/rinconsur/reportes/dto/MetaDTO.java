package cl.rinconsur.reportes.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaDTO {

    private Long id;

    private int anio;

    private int mes;

    private BigDecimal porcentaje;

    private Long idUsuarioRegistra;
}
