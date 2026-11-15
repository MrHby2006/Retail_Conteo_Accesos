package cl.rinconsur.turnos.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rinconsur.turnos.model.Turno;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    List<Turno> findByFechaOrderByFranjaIdAscPuestoIdAsc(LocalDate fecha);

    List<Turno> findByFechaBetweenOrderByFechaAscFranjaIdAscPuestoIdAsc(LocalDate desde, LocalDate hasta);

    List<Turno> findByFechaBetweenAndEstadoNombre(LocalDate desde, LocalDate hasta, String estado);

    List<Turno> findByIdUsuarioAndFechaAndEstadoNombreOrderByFranjaIdAsc(Long idUsuario, LocalDate fecha, String estado);

    Optional<Turno> findFirstByIdUsuarioAndFechaAfterAndEstadoNombreOrderByFechaAscFranjaIdAsc(Long idUsuario, LocalDate fecha, String estado);

    List<Turno> findByFechaAndFranjaId(LocalDate fecha, Long idFranja);

    long countByIdUsuarioAndFechaGreaterThanEqual(Long idUsuario, LocalDate fecha);
}
