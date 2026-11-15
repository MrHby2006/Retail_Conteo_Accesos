package cl.rinconsur.turnos.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudDTO {

    private Long id;

    private Long idTurno;

    private LocalDate fecha;

    private Long idTitular;

    private String nombreTitular;

    private String franjaActual;

    private String puestoActual;

    private Long idNuevo;

    private String nombreNuevo;

    private String franjaNueva;

    private String puestoNuevo;

    private boolean aceptaTitular;

    private boolean aceptaNuevo;

    private String estado;

    private String motivo;
}
