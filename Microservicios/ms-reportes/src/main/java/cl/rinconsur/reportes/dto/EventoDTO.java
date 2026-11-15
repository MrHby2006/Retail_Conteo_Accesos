package cl.rinconsur.reportes.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoDTO {

    private Long id;

    private Long idTipo;

    private String tipo;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private Long idAcceso;

    private String descripcion;

    private Long idUsuarioRegistra;
}
