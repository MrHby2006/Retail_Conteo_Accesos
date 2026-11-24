package cl.rinconsur.turnos.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.rinconsur.turnos.client.AccesoClient;
import cl.rinconsur.turnos.client.NotificacionClient;
import cl.rinconsur.turnos.client.UsuarioClient;
import cl.rinconsur.turnos.dto.AccesoDTO;
import cl.rinconsur.turnos.dto.CorreoMasivoDTO;
import cl.rinconsur.turnos.dto.FranjaDTO;
import cl.rinconsur.turnos.dto.PublicacionResultadoDTO;
import cl.rinconsur.turnos.dto.PuestoDTO;
import cl.rinconsur.turnos.dto.TurnoDTO;
import cl.rinconsur.turnos.dto.TurnoNuevoDTO;
import cl.rinconsur.turnos.dto.UsuarioDTO;
import cl.rinconsur.turnos.model.EstadoTurno;
import cl.rinconsur.turnos.model.Franja;
import cl.rinconsur.turnos.model.Puesto;
import cl.rinconsur.turnos.model.Turno;
import cl.rinconsur.turnos.repository.EstadoTurnoRepository;
import cl.rinconsur.turnos.repository.FranjaRepository;
import cl.rinconsur.turnos.repository.PuestoRepository;
import cl.rinconsur.turnos.repository.SolicitudCambioRepository;
import cl.rinconsur.turnos.repository.TurnoRepository;
import cl.rinconsur.turnos.util.PlantillasCorreo;

@Service
public class TurnoService {

    public static final String BORRADOR = "BORRADOR";
    public static final String PUBLICADO = "PUBLICADO";

    @Autowired
    private TurnoRepository turnoRep;

    @Autowired
    private FranjaRepository franjaRep;

    @Autowired
    private PuestoRepository puestoRep;

    @Autowired
    private EstadoTurnoRepository estadoRep;

    @Autowired
    private SolicitudCambioRepository solicitudRep;

    @Autowired
    private UsuarioClient usuarioCli;

    @Autowired
    private AccesoClient accesoCli;

    @Autowired
    private NotificacionClient notificacionCli;

    public List<FranjaDTO> franjas() {
        List<FranjaDTO> lista = new ArrayList<>();
        for (Franja f : franjaRep.findAll()) {
            lista.add(new FranjaDTO(f.getId(), f.getNombre(), f.getHoraInicio(), f.getHoraFin()));
        }
        return lista;
    }

    public List<PuestoDTO> puestosDisponibles() {
        List<Long> accesosActivos = new ArrayList<>();
        boolean sinRespuesta = false;
        try {
            for (AccesoDTO a : accesoCli.listarActivos()) {
                accesosActivos.add(a.getId());
            }
        } catch (Exception e) {
            System.out.println("ms-accesos no respondió, se muestran todos los puestos: " + e.getMessage());
            sinRespuesta = true;
        }
        List<PuestoDTO> lista = new ArrayList<>();
        for (Puesto p : puestoRep.findByActivoTrueOrderByIdAsc()) {
            if (p.getIdAcceso() == null || sinRespuesta || accesosActivos.contains(p.getIdAcceso())) {
                lista.add(new PuestoDTO(p.getId(), p.getCodigo(), p.getNombre(), p.getTipo(), p.getIdAcceso()));
            }
        }
        return lista;
    }

    public List<TurnoDTO> listarDia(LocalDate fecha) {
        return toDTOs(turnoRep.findByFechaOrderByFranjaIdAscPuestoIdAsc(fecha));
    }

    public List<TurnoDTO> listarRango(LocalDate desde, LocalDate hasta, boolean soloPublicados) {
        List<Turno> turnos = new ArrayList<>();
        for (Turno t : turnoRep.findByFechaBetweenOrderByFechaAscFranjaIdAscPuestoIdAsc(desde, hasta)) {
            if (!soloPublicados || t.getEstado().getNombre().equals(PUBLICADO)) {
                turnos.add(t);
            }
        }
        return toDTOs(turnos);
    }

    public TurnoDTO turnoDeHoy(Long idUsuario) {
        LocalDateTime ahora = LocalDateTime.now();
        for (Turno t : turnoRep.findByIdUsuarioAndFechaAndEstadoNombreOrderByFranjaIdAsc(idUsuario, LocalDate.now(), PUBLICADO)) {
            if (LocalDateTime.of(t.getFecha(), t.getFranja().getHoraFin()).isAfter(ahora)) {
                return toDTOs(List.of(t)).get(0);
            }
        }
        return null;
    }

    public TurnoDTO proximoTurno(Long idUsuario) {
        return turnoRep.findFirstByIdUsuarioAndFechaAfterAndEstadoNombreOrderByFechaAscFranjaIdAsc(idUsuario, LocalDate.now(), PUBLICADO)
                .map(t -> toDTOs(List.of(t)).get(0))
                .orElse(null);
    }

    public long contarPendientes(Long idUsuario) {
        return turnoRep.countByIdUsuarioAndFechaGreaterThanEqual(idUsuario, LocalDate.now());
    }

    public TurnoDTO crear(TurnoNuevoDTO dto) {
        if (dto.getFecha() == null) {
            throw new RuntimeException("Falta la fecha del turno");
        }
        Turno turno = new Turno();
        turno.setIdSupervisor(dto.getIdSupervisor());
        turno.setFecha(dto.getFecha());
        turno.setEstado(estado(BORRADOR));
        turno.setFechaCreacion(LocalDateTime.now());
        aplicarDatos(turno, dto);
        return toDTOs(List.of(turnoRep.save(turno))).get(0);
    }

    public TurnoDTO editar(Long id, TurnoNuevoDTO dto) {
        Turno turno = buscar(id);
        aplicarDatos(turno, dto);
        return toDTOs(List.of(turnoRep.save(turno))).get(0);
    }

    public void eliminar(Long id) {
        turnoRep.delete(buscar(id));
    }

    public PublicacionResultadoDTO publicar(LocalDate desde, LocalDate hasta) {
        List<Turno> borradores = turnoRep.findByFechaBetweenAndEstadoNombre(desde, hasta, BORRADOR);
        if (borradores.isEmpty()) {
            return new PublicacionResultadoDTO(0, false, 0);
        }
        EstadoTurno publicado = estado(PUBLICADO);
        for (Turno t : borradores) {
            t.setEstado(publicado);
            t.setFechaPublicacion(LocalDateTime.now());
            turnoRep.save(t);
        }

        List<TurnoDTO> semana = listarRango(desde, hasta, true);
        List<String> correos = new ArrayList<>();
        for (UsuarioDTO u : usuarioCli.listar()) {
            if (u.getCorreo() != null && !u.getCorreo().isBlank()) {
                correos.add(u.getCorreo());
            }
        }
        boolean enviado = notificacionCli.enviarMasivo(new CorreoMasivoDTO("HORARIO_PUBLICADO", correos,
                "Horario de turnos del " + PlantillasCorreo.dia(desde) + " al " + PlantillasCorreo.dia(hasta),
                PlantillasCorreo.horario(semana)));
        return new PublicacionResultadoDTO(borradores.size(), enviado, correos.size());
    }

    public Turno buscar(Long id) {
        return turnoRep.findById(id).orElseThrow(() -> new RuntimeException("Turno no encontrado"));
    }

    private void aplicarDatos(Turno turno, TurnoNuevoDTO dto) {
        if (dto.getIdUsuario() == null || dto.getIdFranja() == null || dto.getIdPuesto() == null) {
            throw new RuntimeException("Faltan el trabajador, la franja o el puesto");
        }
        Franja franja = franjaRep.findById(dto.getIdFranja()).orElseThrow(() -> new RuntimeException("Franja no encontrada"));
        Puesto puesto = puestoRep.findById(dto.getIdPuesto()).orElseThrow(() -> new RuntimeException("Puesto no encontrado"));
        turno.setIdUsuario(dto.getIdUsuario());
        turno.setFranja(franja);
        turno.setPuesto(puesto);
    }

    private EstadoTurno estado(String nombre) {
        return estadoRep.findByNombre(nombre).orElseThrow(() -> new RuntimeException("Estado " + nombre + " no existe"));
    }

    public List<TurnoDTO> toDTOs(List<Turno> turnos) {
        Map<Long, UsuarioDTO> usuarios = usuarioCli.mapaPorId();
        List<TurnoDTO> lista = new ArrayList<>();
        for (Turno t : turnos) {
            UsuarioDTO u = usuarios.get(t.getIdUsuario());
            TurnoDTO dto = new TurnoDTO();
            dto.setId(t.getId());
            dto.setIdUsuario(t.getIdUsuario());
            dto.setNombreTrabajador(u == null ? "Usuario " + t.getIdUsuario() : u.getNombreCompleto());
            dto.setIdSupervisor(t.getIdSupervisor());
            dto.setFecha(t.getFecha());
            dto.setIdFranja(t.getFranja().getId());
            dto.setFranja(t.getFranja().getNombre());
            dto.setHoraInicio(t.getFranja().getHoraInicio());
            dto.setHoraFin(t.getFranja().getHoraFin());
            dto.setIdPuesto(t.getPuesto().getId());
            dto.setCodigoPuesto(t.getPuesto().getCodigo());
            dto.setPuesto(t.getPuesto().getNombre());
            dto.setTipoPuesto(t.getPuesto().getTipo());
            dto.setEstado(t.getEstado().getNombre());
            dto.setCambioPendiente(solicitudRep.existsByTurnoIdAndEstadoNombre(t.getId(), SolicitudService.PENDIENTE));
            lista.add(dto);
        }
        return lista;
    }
}
