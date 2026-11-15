package cl.rinconsur.turnos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rinconsur.turnos.model.SolicitudCambio;

@Repository
public interface SolicitudCambioRepository extends JpaRepository<SolicitudCambio, Long> {

    List<SolicitudCambio> findByEstadoNombreOrderByFechaCreacionDesc(String estado);

    boolean existsByTurnoIdAndEstadoNombre(Long idTurno, String estado);
}
