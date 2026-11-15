package cl.rinconsur.accesos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rinconsur.accesos.model.RangoEtario;

@Repository
public interface RangoEtarioRepository extends JpaRepository<RangoEtario, Long> {

    List<RangoEtario> findAllByOrderByOrdenAsc();
}
