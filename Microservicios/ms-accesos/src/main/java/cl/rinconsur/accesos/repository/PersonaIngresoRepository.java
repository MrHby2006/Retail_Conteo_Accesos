package cl.rinconsur.accesos.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rinconsur.accesos.model.PersonaIngreso;

@Repository
public interface PersonaIngresoRepository extends JpaRepository<PersonaIngreso, Long> {

    List<PersonaIngreso> findByComproFalseAndAnuladoFalseOrderByFechaHoraDesc();

    List<PersonaIngreso> findByAnuladoFalseAndFechaHoraBetweenOrderByFechaHoraAsc(LocalDateTime desde, LocalDateTime hasta);

    long countByAccesoIdAndAnuladoFalseAndFechaHoraBetween(Long idAcceso, LocalDateTime desde, LocalDateTime hasta);
}
