package cl.rinconsur.usuarios.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.rinconsur.usuarios.dto.CodigoEnviadoDTO;
import cl.rinconsur.usuarios.dto.RecuperarDTO;
import cl.rinconsur.usuarios.dto.RestablecerDTO;
import cl.rinconsur.usuarios.model.CodigoRecuperacion;
import cl.rinconsur.usuarios.model.Usuario;
import cl.rinconsur.usuarios.repository.CodigoRecuperacionRepository;
import cl.rinconsur.usuarios.repository.UsuarioRepository;
import cl.rinconsur.usuarios.util.PlantillasCorreo;

@Service
public class RecuperacionService {

    private static final int MINUTOS_VALIDEZ = 15;

    @Autowired
    private CodigoRecuperacionRepository codigoRep;

    @Autowired
    private UsuarioRepository usuarioRep;

    @Autowired
    private UsuarioService usuarioServ;

    @Autowired
    private PasswordEncoder encoder;

    private final SecureRandom aleatorio = new SecureRandom();

    @Transactional
    public CodigoEnviadoDTO enviarCodigo(RecuperarDTO dto) {
        Usuario user = usuarioServ.buscarPorRut(dto.getRut());
        for (CodigoRecuperacion anterior : codigoRep.findByUsuarioIdAndUsadoFalse(user.getId())) {
            anterior.setUsado(true);
            codigoRep.save(anterior);
        }

        String codigo = String.valueOf(aleatorio.nextInt(900000) + 100000);
        CodigoRecuperacion nuevo = new CodigoRecuperacion();
        nuevo.setUsuario(user);
        nuevo.setCodigo(codigo);
        nuevo.setFechaCreacion(LocalDateTime.now());
        nuevo.setFechaExpira(LocalDateTime.now().plusMinutes(MINUTOS_VALIDEZ));
        nuevo.setUsado(false);
        codigoRep.save(nuevo);

        boolean enviado = usuarioServ.enviarAEmpresa("CODIGO_RECUPERACION", user,
                "Código para recuperar tu contraseña: " + codigo,
                PlantillasCorreo.codigoRecuperacion(user.getNombres(), codigo, MINUTOS_VALIDEZ));
        return new CodigoEnviadoDTO(ocultar(user.getCorreo()), enviado, MINUTOS_VALIDEZ);
    }

    @Transactional
    public void restablecer(RestablecerDTO dto) {
        Usuario user = usuarioServ.buscarPorRut(dto.getRut());
        CodigoRecuperacion codigo = codigoRep
                .findFirstByUsuarioIdAndCodigoAndUsadoFalseAndFechaExpiraAfter(user.getId(), dto.getCodigo(), LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Código incorrecto o vencido. Puedes pedir uno nuevo"));
        codigo.setUsado(true);
        codigoRep.save(codigo);

        user.setPasswordHash(encoder.encode(dto.getNueva()));
        user.setDebeCambiarContrasena(false);
        usuarioRep.save(user);
        usuarioServ.enviarAEmpresa("CONTRASENA_CAMBIADA", user, "Tu contraseña de FlujoSur cambió",
                PlantillasCorreo.contrasenaCambiada(user.getNombres()));
    }

    private String ocultar(String correo) {
        String usuario = correo.substring(0, correo.indexOf("@"));
        String dominio = correo.substring(correo.indexOf("@"));
        int ocultos = Math.max(usuario.length() - 2, 1);
        return usuario.substring(0, Math.min(2, usuario.length())) + "*".repeat(ocultos) + dominio;
    }
}
