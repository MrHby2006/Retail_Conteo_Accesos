package cl.rinconsur.usuarios.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rinconsur.usuarios.model.CodigoRecuperacion;

@Repository
public interface CodigoRecuperacionRepository extends JpaRepository<CodigoRecuperacion, Long> {

    List<CodigoRecuperacion> findByUsuarioIdAndUsadoFalse(Long idUsuario);

    Optional<CodigoRecuperacion> findFirstByUsuarioIdAndCodigoAndUsadoFalseAndFechaExpiraAfter(
        Long idUsuario, String codigo, LocalDateTime ahora);
}
