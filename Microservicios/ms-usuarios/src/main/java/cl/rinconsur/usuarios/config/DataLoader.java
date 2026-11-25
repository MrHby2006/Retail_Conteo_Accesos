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
                if (permisoRep.findByCodigo(p[0]).isPresent()) {
                    continue;
                }
                permisoRep.save(new Permiso(null, p[0], p[1]));
            }

            Rol admin = rolRep.findByCodigo("ADMIN").orElseGet(() -> rolRep.save(new Rol(null, "ADMIN", "Administrador", "Jefatura de la tienda",
                    buscar(permisoRep, "EMPLEADO_GESTIONAR", "ACCESO_GESTIONAR", "INDICADOR_VER", "HORARIO_VER"))));
            Rol supervisor = rolRep.findByCodigo("SUPERVISOR").orElseGet(() -> rolRep.save(new Rol(null, "SUPERVISOR", "Supervisor de piso", "Arma los turnos de los trabajadores",
                    buscar(permisoRep, "TURNO_GESTIONAR", "SOLICITUD_CREAR", "HORARIO_VER"))));
            Rol trabajador = rolRep.findByCodigo("TRABAJADOR").orElseGet(() -> rolRep.save(new Rol(null, "TRABAJADOR", "Trabajador", "Trabaja en una puerta o en caja según su turno",
                    buscar(permisoRep, "HORARIO_VER", "INGRESO_REGISTRAR", "COMPRA_REGISTRAR"))));

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
                {"C-10", "167894550", "Sebastián", "Castillo", "Mora", "Vendedor, Electro", "sebastian@rinconsur.cl", trabajador},
                {"C-11", "173456123", "Camila", "Vargas", "Riquelme", "Vendedora, Vestuario Mujer", "camila.vargas@rinconsur.cl", trabajador},
                {"C-12", "184567237", "Diego", "Fuentealba", "Cortés", "Vendedor, Vestuario Hombre", "diego.fuentealba@rinconsur.cl", trabajador},
                {"C-13", "195678340", "Antonia", "Sepúlveda", "Lara", "Cajera, Cajas Centrales", "antonia.sepulveda@rinconsur.cl", trabajador},
                {"C-14", "20678945K", "Benjamín", "Olivares", "Pinto", "Vendedor, Electro", "benjamin.olivares@rinconsur.cl", trabajador},
                {"C-15", "16789156K", "Francisca", "Molina", "Godoy", "Vendedora, Calzado", "francisca.molina@rinconsur.cl", trabajador},
                {"C-16", "188902677", "Vicente", "Carrasco", "Ulloa", "Vendedor, Deportes", "vicente.carrasco@rinconsur.cl", trabajador},
                {"C-17", "199013785", "Constanza", "Figueroa", "Salas", "Cajera, Cajas Centrales", "constanza.figueroa@rinconsur.cl", trabajador},
                {"C-18", "201124891", "Matilde", "Henríquez", "Bravo", "Vendedora, Infantil", "matilde.henriquez@rinconsur.cl", trabajador},
                {"C-19", "172235913", "Cristóbal", "Leiva", "Donoso", "Vendedor, Hogar y Deco", "cristobal.leiva@rinconsur.cl", trabajador},
                {"C-20", "183346024", "Josefa", "Maldonado", "Reyes", "Vendedora, Perfumería", "josefa.maldonado@rinconsur.cl", trabajador},
                {"C-21", "194457138", "Nicolás", "Parra", "Yáñez", "Reponedor, Bodega", "nicolas.parra@rinconsur.cl", trabajador},
                {"C-22", "205568247", "Daniel", "Quiroz", "Tapia", "Reponedor, Bodega", "daniel.quiroz@rinconsur.cl", trabajador},
                {"C-23", "166679354", "Isidora", "Valdés", "Campos", "Vendedora, Accesorios", "isidora.valdes@rinconsur.cl", trabajador},
                {"C-24", "17779046K", "Martín", "Zúñiga", "Araya", "Vendedor, Vestuario Hombre", "martin.zuniga@rinconsur.cl", trabajador}
            };
            for (Object[] u : usuarios) {
                if (usuarioRep.existsByRut((String) u[1]) || usuarioRep.existsByCorreo((String) u[6])) {
                    continue;
                }
                Usuario user = new Usuario();
                user.setCodigo(String.format("C-%02d", usuarioRep.count() + 1));
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
