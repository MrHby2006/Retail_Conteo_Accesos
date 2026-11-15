package cl.rinconsur.notificaciones.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rinconsur.notificaciones.model.Notificacion;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findAllByOrderByFechaEnvioDesc();

    List<Notificacion> findByIdUsuarioOrderByFechaEnvioDesc(Long idUsuario);
}
