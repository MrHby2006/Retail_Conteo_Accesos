package cl.rinconsur.accesos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rinconsur.accesos.model.TipoCompania;

@Repository
public interface TipoCompaniaRepository extends JpaRepository<TipoCompania, Long> {
}
