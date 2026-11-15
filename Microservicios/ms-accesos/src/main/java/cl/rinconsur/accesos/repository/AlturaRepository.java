package cl.rinconsur.accesos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rinconsur.accesos.model.Altura;

@Repository
public interface AlturaRepository extends JpaRepository<Altura, Long> {

    List<Altura> findAllByOrderByOrdenAsc();
}
