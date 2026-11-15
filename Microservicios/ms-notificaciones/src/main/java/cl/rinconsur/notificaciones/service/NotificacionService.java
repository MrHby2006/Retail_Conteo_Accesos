package cl.rinconsur.notificaciones.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import cl.rinconsur.notificaciones.dto.CorreoDTO;
import cl.rinconsur.notificaciones.dto.CorreoMasivoDTO;
import cl.rinconsur.notificaciones.dto.NotificacionDTO;
import cl.rinconsur.notificaciones.dto.ResultadoMasivoDTO;
import cl.rinconsur.notificaciones.model.Notificacion;
import cl.rinconsur.notificaciones.model.TipoNotificacion;
import cl.rinconsur.notificaciones.repository.NotificacionRepository;
import cl.rinconsur.notificaciones.repository.TipoNotificacionRepository;

@Service
public class NotificacionService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private NotificacionRepository notificacionRep;

    @Autowired
    private TipoNotificacionRepository tipoRep;

    @Value("${spring.mail.username:}")
    private String remitente;

    public NotificacionDTO enviarCorreo(CorreoDTO dto) {
        String error = null;
        try {
            revisarConfiguracion();
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(remitente);
            mensaje.setTo(dto.getDestinatario());
            mensaje.setSubject(dto.getAsunto());
            mensaje.setText(dto.getMensaje());
            mailSender.send(mensaje);
        } catch (Exception e) {
            error = e.getMessage();
            System.out.println("Error al enviar correo a " + dto.getDestinatario() + ": " + error);
        }
        Notificacion guardada = guardar(dto.getTipo(), dto.getIdUsuario(), dto.getDestinatario(), dto.getAsunto(), error);
        return toDTO(guardada);
    }

    public ResultadoMasivoDTO enviarMasivo(CorreoMasivoDTO dto) {
        if (dto.getDestinatarios() == null || dto.getDestinatarios().isEmpty()) {
            return new ResultadoMasivoDTO(false, 0, "No hay destinatarios");
        }
        String error = null;
        try {
            revisarConfiguracion();
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(remitente);
            mensaje.setTo(remitente);
            mensaje.setBcc(dto.getDestinatarios().toArray(new String[0]));
            mensaje.setSubject(dto.getAsunto());
            mensaje.setText(dto.getMensaje());
            mailSender.send(mensaje);
        } catch (Exception e) {
            error = e.getMessage();
            System.out.println("Error al enviar correo masivo: " + error);
        }
        for (String destinatario : dto.getDestinatarios()) {
            guardar(dto.getTipo(), null, destinatario, dto.getAsunto(), error);
        }
        return new ResultadoMasivoDTO(error == null, dto.getDestinatarios().size(), error);
    }

    public List<NotificacionDTO> listar() {
        List<NotificacionDTO> lista = new ArrayList<>();
        for (Notificacion n : notificacionRep.findAllByOrderByFechaEnvioDesc()) {
            lista.add(toDTO(n));
        }
        return lista;
    }

    public List<NotificacionDTO> listarPorUsuario(Long idUsuario) {
        List<NotificacionDTO> lista = new ArrayList<>();
        for (Notificacion n : notificacionRep.findByIdUsuarioOrderByFechaEnvioDesc(idUsuario)) {
            lista.add(toDTO(n));
        }
        return lista;
    }

    private void revisarConfiguracion() {
        if (remitente == null || remitente.isBlank()) {
            throw new RuntimeException("El envío de correos no está configurado (faltan MAIL_USUARIO y MAIL_CLAVE)");
        }
    }

    private Notificacion guardar(String tipo, Long idUsuario, String destinatario, String asunto, String error) {
        TipoNotificacion tipoNotificacion = tipoRep.findByNombre(tipo == null ? "OTRO" : tipo)
                .orElseGet(() -> tipoRep.findByNombre("OTRO").orElseThrow());
        Notificacion n = new Notificacion();
        n.setTipo(tipoNotificacion);
        n.setIdUsuario(idUsuario);
        n.setDestinatario(destinatario);
        n.setAsunto(asunto);
        n.setEnviado(error == null);
        n.setError(error == null ? null : recortar(error));
        n.setFechaEnvio(LocalDateTime.now());
        return notificacionRep.save(n);
    }

    private String recortar(String texto) {
        return texto.length() > 250 ? texto.substring(0, 250) : texto;
    }

    private NotificacionDTO toDTO(Notificacion n) {
        return new NotificacionDTO(n.getId(), n.getTipo().getNombre(), n.getIdUsuario(), n.getDestinatario(),
                n.getAsunto(), n.isEnviado(), n.getError(), n.getFechaEnvio());
    }
}
