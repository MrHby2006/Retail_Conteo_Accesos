package cl.rinconsur.accesos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rinconsur.accesos.model.Acceso;

@Repository
public interface AccesoRepository extends JpaRepository<Acceso, Long> {

    List<Acceso> findAllByOrderByIdAsc();

    List<Acceso> findByActivoTrueOrderByIdAsc();
}
