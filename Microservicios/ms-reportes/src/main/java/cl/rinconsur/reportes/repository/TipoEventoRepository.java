package cl.rinconsur.reportes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rinconsur.reportes.model.TipoEvento;

@Repository
public interface TipoEventoRepository extends JpaRepository<TipoEvento, Long> {
}
