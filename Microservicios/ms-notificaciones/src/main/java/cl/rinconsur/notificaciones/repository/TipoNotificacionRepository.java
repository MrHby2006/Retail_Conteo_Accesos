package cl.rinconsur.notificaciones.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rinconsur.notificaciones.model.TipoNotificacion;

@Repository
public interface TipoNotificacionRepository extends JpaRepository<TipoNotificacion, Long> {

    Optional<TipoNotificacion> findByNombre(String nombre);
}
