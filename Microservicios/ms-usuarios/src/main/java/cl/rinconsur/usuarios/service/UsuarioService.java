package cl.rinconsur.usuarios.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cl.rinconsur.usuarios.client.NotificacionClient;
import cl.rinconsur.usuarios.dto.CambioContrasenaDTO;
import cl.rinconsur.usuarios.dto.CorreoDTO;
import cl.rinconsur.usuarios.dto.CuentaCreadaDTO;
import cl.rinconsur.usuarios.dto.EmpleadoDTO;
import cl.rinconsur.usuarios.dto.LoginDTO;
import cl.rinconsur.usuarios.dto.PerfilDTO;
import cl.rinconsur.usuarios.dto.UsuarioDTO;
import cl.rinconsur.usuarios.model.Rol;
import cl.rinconsur.usuarios.model.Usuario;
import cl.rinconsur.usuarios.repository.RolRepository;
import cl.rinconsur.usuarios.repository.UsuarioRepository;
import cl.rinconsur.usuarios.util.PlantillasCorreo;
import cl.rinconsur.usuarios.util.RutUtil;

@Service
public class UsuarioService {

    private static final String DOMINIO = "@rinconsur.cl";
    private static final String LETRAS = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String NUMEROS = "23456789";

    @Autowired
    private UsuarioRepository usuarioRep;

    @Autowired
    private RolRepository rolRep;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private NotificacionClient notificacionCli;


    @Value("${fotos.carpeta:fotos}")
    private String carpetaFotos;

    private final SecureRandom aleatorio = new SecureRandom();

    public UsuarioDTO guardarFoto(Long id, MultipartFile archivo) throws IOException {
        Usuario user = buscar(id);
        if (archivo == null || archivo.isEmpty()) {
            throw new RuntimeException("No llegó la foto");
        }
        Path carpeta = Paths.get(carpetaFotos);
        Files.createDirectories(carpeta);
        Path destino = carpeta.resolve("usuario_" + user.getId() + ".jpg");
        Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        user.setFotoUrl("api/v1/usuarios/" + user.getId() + "/foto");
        return toDTO(usuarioRep.save(user));
    }

    public byte[] leerFoto(Long id) throws IOException {
        Path archivo = Paths.get(carpetaFotos).resolve("usuario_" + id + ".jpg");
        if (!Files.exists(archivo)) {
            throw new RuntimeException("El usuario no tiene foto");
        }
        return Files.readAllBytes(archivo);
    }

    public UsuarioDTO login(LoginDTO dto) {
        Usuario user = usuarioRep.findByRutAndActivoTrue(RutUtil.limpiar(dto.getRut()))
                .orElseThrow(() -> new RuntimeException("RUT o contraseña incorrectos"));
        if (!encoder.matches(dto.getContrasena(), user.getPasswordHash())) {
            throw new RuntimeException("RUT o contraseña incorrectos");
        }
        return toDTO(user);
    }

    public List<UsuarioDTO> listar(String codigoRol) {
        List<Usuario> usuarios;
        if (codigoRol == null || codigoRol.isBlank()) {
            usuarios = usuarioRep.findByActivoTrueOrderByNombresAsc();
        } else {
            usuarios = usuarioRep.findByActivoTrueAndRolCodigoOrderByNombresAsc(codigoRol.toUpperCase());
        }
        List<UsuarioDTO> lista = new ArrayList<>();
        for (Usuario u : usuarios) {
            lista.add(toDTO(u));
        }
        return lista;
    }

    public UsuarioDTO buscarPorId(Long id) {
        return toDTO(buscar(id));
    }

    public CuentaCreadaDTO crear(EmpleadoDTO dto) {
        if (vacio(dto.getRut())) {
            throw new RuntimeException("Falta el RUT");
        }
        revisarDatosEmpleado(dto);
        String rut = RutUtil.limpiar(dto.getRut());
        Rol rol = buscarRol(dto.getCodigoRol());

        String temporal = contrasenaTemporal();
        Usuario user = new Usuario();
        user.setCodigo(siguienteCodigo());
        user.setRut(rut);
        user.setNombres(dto.getNombres().trim());
        user.setApellidoPaterno(dto.getApellidoPaterno().trim());
        user.setApellidoMaterno(dto.getApellidoMaterno() == null ? null : dto.getApellidoMaterno().trim());
        user.setCargo(dto.getCargo().trim());
        user.setCorreo(generarCorreo(dto.getNombres(), dto.getApellidoPaterno()));
        user.setCorreoPersonal(dto.getCorreoPersonal() == null ? null : dto.getCorreoPersonal().trim().toLowerCase());
        user.setTelefono(dto.getTelefono());
        user.setPasswordHash(encoder.encode(temporal));
        user.setDebeCambiarContrasena(true);
        user.setActivo(true);
        user.setRol(rol);
        user.setFechaCreacion(LocalDateTime.now());
        usuarioRep.save(user);

        boolean enviado = enviar("CUENTA_NUEVA", user, "Tu cuenta de FlujoSur ya está lista",
                PlantillasCorreo.cuentaNueva(user.getNombres(), RutUtil.formatear(rut), user.getCorreo(), temporal));
        return new CuentaCreadaDTO(toDTO(user), temporal, enviado);
    }

    public UsuarioDTO editar(Long id, EmpleadoDTO dto) {
        revisarDatosEmpleado(dto);
        Usuario user = buscar(id);
        user.setNombres(dto.getNombres().trim());
        user.setApellidoPaterno(dto.getApellidoPaterno().trim());
        user.setApellidoMaterno(dto.getApellidoMaterno() == null ? null : dto.getApellidoMaterno().trim());
        user.setCargo(dto.getCargo().trim());
        user.setTelefono(dto.getTelefono());
        user.setCorreoPersonal(dto.getCorreoPersonal() == null ? null : dto.getCorreoPersonal().trim().toLowerCase());
        if (dto.getCodigoRol() != null && !user.getRol().getCodigo().equals("ADMIN")) {
            user.setRol(buscarRol(dto.getCodigoRol()));
        }
        return toDTO(usuarioRep.save(user));
    }

    public void desactivar(Long id) {
        Usuario user = buscar(id);
        user.setActivo(false);
        usuarioRep.save(user);
    }

    public UsuarioDTO actualizarPerfil(Long id, PerfilDTO dto) {
        Usuario user = buscar(id);
        user.setTelefono(dto.getTelefono());
        user.setFechaNacimiento(dto.getFechaNacimiento());
        if (dto.getFotoUrl() != null) {
            user.setFotoUrl(dto.getFotoUrl());
        }
        return toDTO(usuarioRep.save(user));
    }

    public UsuarioDTO cambiarContrasena(Long id, CambioContrasenaDTO dto) {
        if (vacio(dto.getNueva())) {
            throw new RuntimeException("Falta la contraseña nueva");
        }
        Usuario user = buscar(id);
        if (!user.isDebeCambiarContrasena() && !encoder.matches(dto.getActual(), user.getPasswordHash())) {
            throw new RuntimeException("La contraseña actual no es correcta");
        }
        user.setPasswordHash(encoder.encode(dto.getNueva()));
        user.setDebeCambiarContrasena(false);
        usuarioRep.save(user);
        enviarAEmpresa("CONTRASENA_CAMBIADA", user, "Tu contraseña de FlujoSur cambió",
                PlantillasCorreo.contrasenaCambiada(user.getNombres()));
        return toDTO(user);
    }

    public CuentaCreadaDTO nuevaContrasenaTemporal(Long id) {
        Usuario user = buscar(id);
        String temporal = contrasenaTemporal();
        user.setPasswordHash(encoder.encode(temporal));
        user.setDebeCambiarContrasena(true);
        usuarioRep.save(user);
        boolean enviado = enviar("CONTRASENA_TEMPORAL", user, "Nueva contraseña temporal de FlujoSur",
                PlantillasCorreo.contrasenaTemporal(user.getNombres(), RutUtil.formatear(user.getRut()), temporal));
        return new CuentaCreadaDTO(toDTO(user), temporal, enviado);
    }

    private void revisarDatosEmpleado(EmpleadoDTO dto) {
        if (vacio(dto.getNombres()) || vacio(dto.getApellidoPaterno()) || vacio(dto.getCargo())) {
            throw new RuntimeException("Faltan el nombre, el apellido paterno o el cargo");
        }
    }

    private boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }

    public Usuario buscar(Long id) {
        return usuarioRep.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario buscarPorRut(String rut) {
        return usuarioRep.findByRutAndActivoTrue(RutUtil.limpiar(rut))
                .orElseThrow(() -> new RuntimeException("No hay una cuenta activa con ese RUT"));
    }

    public boolean enviar(String tipo, Usuario user, String asunto, String mensaje) {
        if (user.getCorreoPersonal() == null) {
            return false;
        }
        return notificacionCli.enviarCorreo(new CorreoDTO(tipo, user.getId(), user.getCorreoPersonal(), asunto, mensaje));
    }

    public boolean enviarAEmpresa(String tipo, Usuario user, String asunto, String mensaje) {
        return notificacionCli.enviarCorreo(new CorreoDTO(tipo, user.getId(), user.getCorreo(), asunto, mensaje));
    }

    public UsuarioDTO toDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setCodigo(u.getCodigo());
        dto.setRut(u.getRut());
        dto.setNombres(u.getNombres());
        dto.setApellidoPaterno(u.getApellidoPaterno());
        dto.setApellidoMaterno(u.getApellidoMaterno());
        String completo = u.getNombres() + " " + u.getApellidoPaterno();
        if (u.getApellidoMaterno() != null && !u.getApellidoMaterno().isBlank()) {
            completo = completo + " " + u.getApellidoMaterno();
        }
        dto.setNombreCompleto(completo);
        dto.setCargo(u.getCargo());
        dto.setCorreo(u.getCorreo());
        dto.setCorreoPersonal(u.getCorreoPersonal());
        dto.setTelefono(u.getTelefono());
        dto.setCodigoRol(u.getRol().getCodigo());
        dto.setNombreRol(u.getRol().getNombre());
        dto.setDebeCambiarContrasena(u.isDebeCambiarContrasena());
        dto.setFechaNacimiento(u.getFechaNacimiento());
        dto.setFotoUrl(u.getFotoUrl());
        dto.setActivo(u.isActivo());
        return dto;
    }

    private Rol buscarRol(String codigo) {
        if (codigo == null) {
            throw new RuntimeException("Debes elegir un rol");
        }
        return rolRep.findByCodigo(codigo.toUpperCase()).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
    }

    private String siguienteCodigo() {
        int mayor = 0;
        for (Usuario u : usuarioRep.findAll()) {
            try {
                int numero = Integer.parseInt(u.getCodigo().replace("C-", ""));
                if (numero > mayor) {
                    mayor = numero;
                }
            } catch (NumberFormatException e) {
                System.out.println("Código sin número: " + u.getCodigo());
            }
        }
        return String.format("C-%02d", mayor + 1);
    }

    private String generarCorreo(String nombres, String apellido) {
        String base = limpiarTexto(nombres.trim().split(" ")[0]) + "." + limpiarTexto(apellido.trim().split(" ")[0]);
        String correo = base + DOMINIO;
        int numero = 2;
        while (usuarioRep.existsByCorreo(correo)) {
            correo = base + numero + DOMINIO;
            numero++;
        }
        return correo;
    }

    private String limpiarTexto(String texto) {
        String sinTildes = Normalizer.normalize(texto.toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{Mn}+", "");
        return sinTildes.replaceAll("[^a-z]", "");
    }

    private String contrasenaTemporal() {
        List<Character> caracteres = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            caracteres.add(LETRAS.charAt(aleatorio.nextInt(LETRAS.length())));
        }
        for (int i = 0; i < 2; i++) {
            caracteres.add(NUMEROS.charAt(aleatorio.nextInt(NUMEROS.length())));
        }
        java.util.Collections.shuffle(caracteres, aleatorio);
        StringBuilder sb = new StringBuilder();
        for (Character c : caracteres) {
            sb.append(c);
        }
        return sb.toString();
    }
}
