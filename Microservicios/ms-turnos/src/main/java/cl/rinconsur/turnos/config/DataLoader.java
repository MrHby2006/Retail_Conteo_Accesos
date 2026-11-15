package cl.rinconsur.turnos.config;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.rinconsur.turnos.model.EstadoSolicitud;
import cl.rinconsur.turnos.model.EstadoTurno;
import cl.rinconsur.turnos.model.Franja;
import cl.rinconsur.turnos.model.Puesto;
import cl.rinconsur.turnos.model.Turno;
import cl.rinconsur.turnos.repository.EstadoSolicitudRepository;
import cl.rinconsur.turnos.repository.EstadoTurnoRepository;
import cl.rinconsur.turnos.repository.FranjaRepository;
import cl.rinconsur.turnos.repository.PuestoRepository;
import cl.rinconsur.turnos.repository.TurnoRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(FranjaRepository franjaRep, PuestoRepository puestoRep, EstadoTurnoRepository estadoTurnoRep,
                               EstadoSolicitudRepository estadoSolicitudRep, TurnoRepository turnoRep) {
        return args -> {
            if (franjaRep.count() > 0) {
                return;
            }
            Franja manana = franjaRep.save(new Franja(null, "Mañana", LocalTime.of(10, 0), LocalTime.of(15, 30)));
            Franja tarde = franjaRep.save(new Franja(null, "Tarde", LocalTime.of(15, 30), LocalTime.of(21, 0)));

            Puesto caja = puestoRep.save(new Puesto(null, "CAJA", "Caja", "CAJA", null, true));
            Puesto a1 = puestoRep.save(new Puesto(null, "A1", "Puerta A1", "PUERTA", 1L, true));
            puestoRep.save(new Puesto(null, "A2", "Puerta A2", "PUERTA", 2L, true));
            puestoRep.save(new Puesto(null, "A3", "Puerta A3", "PUERTA", 3L, true));
            Puesto a4 = puestoRep.save(new Puesto(null, "A4", "Puerta A4", "PUERTA", 4L, true));

            estadoTurnoRep.save(new EstadoTurno(null, "BORRADOR"));
            EstadoTurno publicado = estadoTurnoRep.save(new EstadoTurno(null, "PUBLICADO"));

            estadoSolicitudRep.save(new EstadoSolicitud(null, "PENDIENTE"));
            estadoSolicitudRep.save(new EstadoSolicitud(null, "ACEPTADA"));
            estadoSolicitudRep.save(new EstadoSolicitud(null, "RECHAZADA"));

            LocalDate lunes = LocalDate.now().with(DayOfWeek.MONDAY);
            Object[][] turnos = {
                {5L, 3L, manana, a4},
                {7L, 3L, manana, caja},
                {6L, 4L, tarde, a1},
                {9L, 4L, tarde, caja}
            };
            for (int dia = 0; dia < 7; dia++) {
                LocalDate fecha = lunes.plusDays(dia);
                for (Object[] t : turnos) {
                    turnoRep.save(new Turno(null, (Long) t[0], (Long) t[1], fecha, (Franja) t[2], (Puesto) t[3], publicado,
                            LocalDateTime.now(), LocalDateTime.now()));
                }
            }
            System.out.println("Datos de ejemplo de ms-turnos cargados");
        };
    }
}
