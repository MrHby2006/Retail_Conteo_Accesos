package cl.rinconsur.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilaIndicadorDTO {

    private String grupo;

    private int ingresos;

    private int compradores;

    private double conversion;
}
