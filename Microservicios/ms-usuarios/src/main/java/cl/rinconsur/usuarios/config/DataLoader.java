package cl.rinconsur.usuarios.config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import cl.rinconsur.usuarios.model.Permiso;
import cl.rinconsur.usuarios.model.Rol;
import cl.rinconsur.usuarios.model.Usuario;
import cl.rinconsur.usuarios.repository.PermisoRepository;
import cl.rinconsur.usuarios.repository.RolRepository;
import cl.rinconsur.usuarios.repository.UsuarioRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(RolRepository rolRep, PermisoRepository permisoRep,
                               UsuarioRepository usuarioRep, PasswordEncoder encoder) {
        return args -> {
            if (rolRep.count() > 0) {
                return;
            }

            String[][] permisos = {
                {"EMPLEADO_GESTIONAR", "Crear, editar y desactivar empleados"},
                {"ACCESO_GESTIONAR", "Activar o desactivar los accesos de la tienda"},
                {"INDICADOR_VER", "Ver los indicadores de tránsito y compra"},
                {"TURNO_GESTIONAR", "Armar y publicar los turnos de la semana"},
                {"SOLICITUD_CREAR", "Pedir cambios en turnos publicados"},
                {"HORARIO_VER", "Ver el horario publicado"},
                {"INGRESO_REGISTRAR", "Registrar a las personas que entran por una puerta"},
                {"COMPRA_REGISTRAR", "Registrar compras en caja"}
            };
            for (String[] p : permisos) {
                permisoRep.save(new Permiso(null, p[0], p[1]));
            }

            Rol admin = rolRep.save(new Rol(null, "ADMIN", "Administrador", "Jefatura de la tienda",
                    buscar(permisoRep, "EMPLEADO_GESTIONAR", "ACCESO_GESTIONAR", "INDICADOR_VER", "HORARIO_VER")));
            Rol supervisor = rolRep.save(new Rol(null, "SUPERVISOR", "Supervisor de piso", "Arma los turnos de los trabajadores",
                    buscar(permisoRep, "TURNO_GESTIONAR", "SOLICITUD_CREAR", "HORARIO_VER")));
            Rol trabajador = rolRep.save(new Rol(null, "TRABAJADOR", "Trabajador", "Trabaja en una puerta o en caja según su turno",
                    buscar(permisoRep, "HORARIO_VER", "INGRESO_REGISTRAR", "COMPRA_REGISTRAR")));

            Object[][] usuarios = {
                {"C-01", "152348711", "Carolina", "Fuentes", "Araya", "Jefa de tienda", "carolina@rinconsur.cl", admin},
                {"C-02", "139874420", "Rodrigo", "Saavedra", "Muñoz", "Subgerente comercial", "rodrigo@rinconsur.cl", admin},
                {"C-03", "176543205", "Daniela", "Pizarro", "Toro", "Supervisora de piso (mañana)", "daniela@rinconsur.cl", supervisor},
                {"C-04", "164430197", "Matías", "Contreras", "Vidal", "Supervisor de piso (tarde)", "matias@rinconsur.cl", supervisor},
                {"C-05", "198763454", "Tamara", "Rojas", "Espinoza", "Vendedora, Vestuario Mujer", "tamara@rinconsur.cl", trabajador},
                {"C-06", "201456789", "Felipe", "Ibarra", "Soto", "Vendedor, Hogar y Deco", "felipe@rinconsur.cl", trabajador},
                {"C-07", "183229044", "Javiera", "Morales", "Cáceres", "Cajera, Cajas Centrales", "javiera@rinconsur.cl", trabajador},
                {"C-08", "178902342", "Ignacio", "Navarro", "Pérez", "Vendedor, Deportes", "ignacio@rinconsur.cl", trabajador},
                {"C-09", "203451180", "Valentina", "Reyes", "Lagos", "Cajera, Cajas Centrales", "valentina@rinconsur.cl", trabajador},
                {"C-10", "167894550", "Sebastián", "Castillo", "Mora", "Vendedor, Electro", "sebastian@rinconsur.cl", trabajador}
            };
            for (Object[] u : usuarios) {
                Usuario user = new Usuario();
                user.setCodigo((String) u[0]);
                user.setRut((String) u[1]);
                user.setNombres((String) u[2]);
                user.setApellidoPaterno((String) u[3]);
                user.setApellidoMaterno((String) u[4]);
                user.setCargo((String) u[5]);
                user.setCorreo((String) u[6]);
                user.setRol((Rol) u[7]);
                user.setPasswordHash(encoder.encode("123456"));
                user.setDebeCambiarContrasena(false);
                user.setActivo(true);
                user.setFechaCreacion(LocalDateTime.now());
                usuarioRep.save(user);
            }
            System.out.println("Datos de ejemplo de ms-usuarios cargados");
        };
    }

    private List<Permiso> buscar(PermisoRepository permisoRep, String... codigos) {
        List<Permiso> lista = new ArrayList<>();
        for (String codigo : codigos) {
            permisoRep.findByCodigo(codigo).ifPresent(lista::add);
        }
        return lista;
    }
}
