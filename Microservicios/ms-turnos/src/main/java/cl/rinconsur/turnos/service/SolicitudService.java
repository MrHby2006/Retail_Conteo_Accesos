package cl.rinconsur.turnos.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.rinconsur.turnos.client.NotificacionClient;
import cl.rinconsur.turnos.client.UsuarioClient;
import cl.rinconsur.turnos.dto.CorreoDTO;
import cl.rinconsur.turnos.dto.RespuestaSolicitudDTO;
import cl.rinconsur.turnos.dto.SolicitudDTO;
import cl.rinconsur.turnos.dto.SolicitudNuevaDTO;
import cl.rinconsur.turnos.dto.UsuarioDTO;
import cl.rinconsur.turnos.model.EstadoSolicitud;
import cl.rinconsur.turnos.model.Franja;
import cl.rinconsur.turnos.model.Puesto;
import cl.rinconsur.turnos.model.SolicitudCambio;
import cl.rinconsur.turnos.model.Turno;
import cl.rinconsur.turnos.repository.EstadoSolicitudRepository;
import cl.rinconsur.turnos.repository.FranjaRepository;
import cl.rinconsur.turnos.repository.PuestoRepository;
import cl.rinconsur.turnos.repository.SolicitudCambioRepository;
import cl.rinconsur.turnos.repository.TurnoRepository;
import cl.rinconsur.turnos.util.PlantillasCorreo;

@Service
public class SolicitudService {

    public static final String PENDIENTE = "PENDIENTE";
    public static final String ACEPTADA = "ACEPTADA";
    public static final String RECHAZADA = "RECHAZADA";

    @Autowired
    private SolicitudCambioRepository solicitudRep;

    @Autowired
    private EstadoSolicitudRepository estadoRep;

    @Autowired
    private TurnoRepository turnoRep;

    @Autowired
    private FranjaRepository franjaRep;

    @Autowired
    private PuestoRepository puestoRep;

    @Autowired
    private TurnoService turnoServ;

    @Autowired
    private UsuarioClient usuarioCli;

    @Autowired
    private NotificacionClient notificacionCli;

    public SolicitudDTO crear(SolicitudNuevaDTO dto) {
        if (dto.getIdTurno() == null) {
            throw new RuntimeException("Falta el turno que se quiere cambiar");
        }
        Turno turno = turnoServ.buscar(dto.getIdTurno());

        SolicitudCambio s = new SolicitudCambio();
        s.setTurno(turno);
        s.setIdSupervisor(dto.getIdSupervisor());
        s.setIdUsuarioNuevo(dto.getIdUsuarioNuevo() == null ? turno.getIdUsuario() : dto.getIdUsuarioNuevo());
        s.setFranjaNueva(dto.getIdFranjaNueva() == null ? turno.getFranja()
                : franjaRep.findById(dto.getIdFranjaNueva()).orElseThrow(() -> new RuntimeException("Franja no encontrada")));
        s.setPuestoNuevo(dto.getIdPuestoNuevo() == null ? turno.getPuesto()
                : puestoRep.findById(dto.getIdPuestoNuevo()).orElseThrow(() -> new RuntimeException("Puesto no encontrado")));
        s.setAceptaTitular(false);
        s.setAceptaNuevo(false);
        s.setEstado(estado(PENDIENTE));
        s.setMotivo(dto.getMotivo());
        s.setFechaCreacion(LocalDateTime.now());
        solicitudRep.save(s);

        Map<Long, UsuarioDTO> usuarios = usuarioCli.mapaPorId();
        SolicitudDTO creada = toDTO(s, usuarios);
        avisar(usuarios.get(turno.getIdUsuario()), creada);
        if (!s.getIdUsuarioNuevo().equals(turno.getIdUsuario())) {
            avisar(usuarios.get(s.getIdUsuarioNuevo()), creada);
        }
        return creada;
    }

    public List<SolicitudDTO> pendientes() {
        Map<Long, UsuarioDTO> usuarios = usuarioCli.mapaPorId();
        List<SolicitudDTO> lista = new ArrayList<>();
        for (SolicitudCambio s : solicitudRep.findByEstadoNombreOrderByFechaCreacionDesc(PENDIENTE)) {
            lista.add(toDTO(s, usuarios));
        }
        return lista;
    }

    public List<SolicitudDTO> pendientesPara(Long idUsuario) {
        Map<Long, UsuarioDTO> usuarios = usuarioCli.mapaPorId();
        List<SolicitudDTO> lista = new ArrayList<>();
        for (SolicitudCambio s : solicitudRep.findByEstadoNombreOrderByFechaCreacionDesc(PENDIENTE)) {
            if (s.getTurno().getFecha().isBefore(LocalDate.now())) {
                continue;
            }
            boolean falta = (s.getTurno().getIdUsuario().equals(idUsuario) && !s.isAceptaTitular())
                    || (s.getIdUsuarioNuevo().equals(idUsuario) && !s.isAceptaNuevo());
            if (falta) {
                lista.add(toDTO(s, usuarios));
            }
        }
        return lista;
    }

    @Transactional
    public String responder(Long id, RespuestaSolicitudDTO dto) {
        if (dto.getIdUsuario() == null) {
            throw new RuntimeException("Falta quién responde la solicitud");
        }
        SolicitudCambio s = solicitudRep.findById(id).orElseThrow(() -> new RuntimeException("La solicitud ya no existe"));
        Turno turno = s.getTurno();
        boolean esTitular = turno.getIdUsuario().equals(dto.getIdUsuario());
        boolean esNuevo = s.getIdUsuarioNuevo().equals(dto.getIdUsuario());

        if (!dto.isAcepta()) {
            cerrar(s, RECHAZADA);
            return "Rechazaste el cambio. El turno queda como estaba";
        }

        boolean cambiaPersona = !s.getIdUsuarioNuevo().equals(turno.getIdUsuario());
        s.setAceptaTitular(s.isAceptaTitular() || esTitular);
        s.setAceptaNuevo(s.isAceptaNuevo() || esNuevo || !cambiaPersona);
        if (!s.isAceptaTitular() || !s.isAceptaNuevo()) {
            solicitudRep.save(s);
            return "Aceptaste. Falta que el otro trabajador acepte para aplicar el cambio";
        }

        turno.setIdUsuario(s.getIdUsuarioNuevo());
        turno.setFranja(s.getFranjaNueva());
        turno.setPuesto(s.getPuestoNuevo());
        turnoRep.save(turno);
        cerrar(s, ACEPTADA);
        return "Cambio aceptado por ambos. El turno ya quedó actualizado";
    }

    private void cerrar(SolicitudCambio s, String estado) {
        s.setEstado(estado(estado));
        s.setFechaResolucion(LocalDateTime.now());
        solicitudRep.save(s);
    }

    private void avisar(UsuarioDTO persona, SolicitudDTO solicitud) {
        if (persona == null || persona.getCorreo() == null) {
            return;
        }
        notificacionCli.enviarCorreo(new CorreoDTO("SOLICITUD_CAMBIO", persona.getId(), persona.getCorreo(),
                "Te proponen un cambio de turno", PlantillasCorreo.solicitud(persona.getNombres(), descripcion(solicitud))));
    }

    private String descripcion(SolicitudDTO s) {
        String dia = PlantillasCorreo.dia(s.getFecha());
        String antes = s.getFranjaActual() + " · " + s.getPuestoActual();
        String despues = s.getFranjaNueva() + " · " + s.getPuestoNuevo();
        if (s.getIdTitular().equals(s.getIdNuevo())) {
            return "Tu turno del " + dia + " cambia de " + antes + " a " + despues;
        }
        return "El turno del " + dia + " (" + antes + ") pasa de " + s.getNombreTitular() + " a " + s.getNombreNuevo() + " (" + despues + ")";
    }

    private EstadoSolicitud estado(String nombre) {
        return estadoRep.findByNombre(nombre).orElseThrow(() -> new RuntimeException("Estado " + nombre + " no existe"));
    }

    private SolicitudDTO toDTO(SolicitudCambio s, Map<Long, UsuarioDTO> usuarios) {
        Turno t = s.getTurno();
        UsuarioDTO titular = usuarios.get(t.getIdUsuario());
        UsuarioDTO nuevo = usuarios.get(s.getIdUsuarioNuevo());
        SolicitudDTO dto = new SolicitudDTO();
        dto.setId(s.getId());
        dto.setIdTurno(t.getId());
        dto.setFecha(t.getFecha());
        dto.setIdTitular(t.getIdUsuario());
        dto.setNombreTitular(titular == null ? "Usuario " + t.getIdUsuario() : titular.getNombreCompleto());
        dto.setFranjaActual(t.getFranja().getNombre());
        dto.setPuestoActual(t.getPuesto().getNombre());
        dto.setIdNuevo(s.getIdUsuarioNuevo());
        dto.setNombreNuevo(nuevo == null ? "Usuario " + s.getIdUsuarioNuevo() : nuevo.getNombreCompleto());
        dto.setFranjaNueva(s.getFranjaNueva().getNombre());
        dto.setPuestoNuevo(s.getPuestoNuevo().getNombre());
        dto.setAceptaTitular(s.isAceptaTitular());
        dto.setAceptaNuevo(s.isAceptaNuevo());
        dto.setEstado(s.getEstado().getNombre());
        dto.setMotivo(s.getMotivo());
        return dto;
    }
}
