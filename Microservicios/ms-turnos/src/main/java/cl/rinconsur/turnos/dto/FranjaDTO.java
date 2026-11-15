package cl.rinconsur.turnos.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FranjaDTO {

    private Long id;

    private String nombre;

    private LocalTime horaInicio;

    private LocalTime horaFin;
}
