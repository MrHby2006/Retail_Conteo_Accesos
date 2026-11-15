package cl.rinconsur.turnos.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoDTO {

    private Long id;

    private Long idUsuario;

    private String nombreTrabajador;

    private Long idSupervisor;

    private LocalDate fecha;

    private Long idFranja;

    private String franja;

    private LocalTime horaInicio;

    private LocalTime horaFin;

    private Long idPuesto;

    private String codigoPuesto;

    private String puesto;

    private String tipoPuesto;

    private String estado;

    private boolean cambioPendiente;
}
