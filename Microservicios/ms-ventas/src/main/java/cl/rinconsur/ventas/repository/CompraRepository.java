package cl.rinconsur.ventas.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rinconsur.ventas.model.Compra;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    boolean existsByIdPersona(Long idPersona);

    boolean existsByNumeroBoleta(String numeroBoleta);

    boolean existsByNumeroBoletaAndIdNot(String numeroBoleta, Long id);

    List<Compra> findAllByOrderByFechaHoraDesc();

    List<Compra> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime desde, LocalDateTime hasta);
}
