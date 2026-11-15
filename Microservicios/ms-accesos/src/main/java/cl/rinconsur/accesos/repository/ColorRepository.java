package cl.rinconsur.accesos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rinconsur.accesos.model.Color;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {
}
