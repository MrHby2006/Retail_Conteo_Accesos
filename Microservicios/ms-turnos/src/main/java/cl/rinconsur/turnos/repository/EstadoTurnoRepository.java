package cl.rinconsur.turnos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rinconsur.turnos.model.EstadoTurno;

@Repository
public interface EstadoTurnoRepository extends JpaRepository<EstadoTurno, Long> {

    Optional<EstadoTurno> findByNombre(String nombre);
}
