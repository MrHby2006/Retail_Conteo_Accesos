package cl.rinconsur.turnos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rinconsur.turnos.model.Franja;

@Repository
public interface FranjaRepository extends JpaRepository<Franja, Long> {

    Optional<Franja> findByNombre(String nombre);
}
