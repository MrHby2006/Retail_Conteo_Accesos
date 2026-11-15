package cl.rinconsur.reportes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rinconsur.reportes.model.MetaConversion;

@Repository
public interface MetaConversionRepository extends JpaRepository<MetaConversion, Long> {

    Optional<MetaConversion> findByAnioAndMes(int anio, int mes);

    List<MetaConversion> findAllByOrderByAnioDescMesDesc();
}
