package cl.rinconsur.reportes.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndicadoresDTO {

    private LocalDate desde;

    private LocalDate hasta;

    private Long idAcceso;

    private int totalIngresos;

    private int compradores;

    private int noCompradores;

    private double conversion;

    private BigDecimal metaMes;

    private Boolean cumpleMeta;

    private List<FilaIndicadorDTO> porAcceso;

    private List<FilaIndicadorDTO> porRango;

    private List<FilaIndicadorDTO> porCompania;

    private List<FilaIndicadorDTO> porHora;

    private List<EventoDTO> eventos;
}
