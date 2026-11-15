package cl.rinconsur.usuarios.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rinconsur.usuarios.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByRutAndActivoTrue(String rut);

    List<Usuario> findByActivoTrueOrderByNombresAsc();

    List<Usuario> findByActivoTrueAndRolCodigoOrderByNombresAsc(String codigoRol);

    boolean existsByRut(String rut);

    boolean existsByCorreo(String correo);

    boolean existsByCorreoPersonal(String correoPersonal);

    boolean existsByCorreoPersonalAndIdNot(String correoPersonal, Long id);
}
