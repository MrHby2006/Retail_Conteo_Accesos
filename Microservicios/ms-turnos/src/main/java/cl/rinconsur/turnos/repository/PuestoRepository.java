package cl.rinconsur.turnos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rinconsur.turnos.model.Puesto;

@Repository
public interface PuestoRepository extends JpaRepository<Puesto, Long> {

    List<Puesto> findByActivoTrueOrderByIdAsc();

    Optional<Puesto> findByCodigo(String codigo);
}
