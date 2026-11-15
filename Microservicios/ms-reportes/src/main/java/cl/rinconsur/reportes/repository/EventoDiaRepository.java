package cl.rinconsur.reportes.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rinconsur.reportes.model.EventoDia;

@Repository
public interface EventoDiaRepository extends JpaRepository<EventoDia, Long> {

    List<EventoDia> findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqualOrderByFechaInicioAsc(LocalDate hasta, LocalDate desde);
}
