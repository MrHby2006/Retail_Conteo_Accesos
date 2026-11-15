package cl.rinconsur.accesos.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.rinconsur.accesos.client.UsuarioClient;
import cl.rinconsur.accesos.dto.AnulacionDTO;
import cl.rinconsur.accesos.dto.IngresoDTO;
import cl.rinconsur.accesos.dto.IngresoNuevoDTO;
import cl.rinconsur.accesos.model.Acceso;
import cl.rinconsur.accesos.model.PersonaIngreso;
import cl.rinconsur.accesos.repository.AccesoRepository;
import cl.rinconsur.accesos.repository.AlturaRepository;
import cl.rinconsur.accesos.repository.ColorRepository;
import cl.rinconsur.accesos.repository.PersonaIngresoRepository;
import cl.rinconsur.accesos.repository.RangoEtarioRepository;
import cl.rinconsur.accesos.repository.TipoCompaniaRepository;

@Service
public class IngresoService {

    private static final LocalTime CAMBIO_DE_FRANJA = LocalTime.of(15, 30);

    @Autowired
    private PersonaIngresoRepository ingresoRep;

    @Autowired
    private AccesoRepository accesoRep;

    @Autowired
    private RangoEtarioRepository rangoRep;

    @Autowired
    private AlturaRepository alturaRep;

    @Autowired
    private ColorRepository colorRep;

    @Autowired
    private TipoCompaniaRepository companiaRep;

    @Autowired
    private UsuarioClient usuarioCli;

    public IngresoDTO registrar(IngresoNuevoDTO dto) {
        if (dto.getIdAcceso() == null || dto.getIdObservador() == null || dto.getIdRango() == null || dto.getIdAltura() == null
                || dto.getIdColorSuperior() == null || dto.getIdColorInferior() == null || dto.getIdTipoCompania() == null) {
            throw new RuntimeException("Faltan datos de la persona que ingresó");
        }
        Acceso acceso = accesoRep.findById(dto.getIdAcceso()).orElseThrow(() -> new RuntimeException("Acceso no encontrado"));
        if (usuarioCli.buscar(dto.getIdObservador()) == null) {
            throw new RuntimeException("No se encontró al observador");
        }

        PersonaIngreso p = new PersonaIngreso();
        p.setAcceso(acceso);
        p.setIdObservador(dto.getIdObservador());
        p.setFechaHora(LocalDateTime.now());
        p.setRango(rangoRep.findById(dto.getIdRango()).orElseThrow(() -> new RuntimeException("Rango de edad no encontrado")));
        p.setAltura(alturaRep.findById(dto.getIdAltura()).orElseThrow(() -> new RuntimeException("Altura no encontrada")));
        p.setColorSuperior(colorRep.findById(dto.getIdColorSuperior()).orElseThrow(() -> new RuntimeException("Color de arriba no encontrado")));
        p.setColorInferior(colorRep.findById(dto.getIdColorInferior()).orElseThrow(() -> new RuntimeException("Color de abajo no encontrado")));
        p.setTipoCompania(companiaRep.findById(dto.getIdTipoCompania()).orElseThrow(() -> new RuntimeException("Tipo de compañía no encontrado")));
        p.setDetalle(dto.getDetalle());
        p.setCompro(false);
        p.setAnulado(false);
        return toDTO(ingresoRep.save(p));
    }

    public IngresoDTO buscar(Long id) {
        return toDTO(buscarPersona(id));
    }

    public long contarDelDia(Long idAcceso, LocalDate fecha) {
        return ingresoRep.countByAccesoIdAndAnuladoFalseAndFechaHoraBetween(
                idAcceso, fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay());
    }

    public List<IngresoDTO> candidatos(Long idRango, Long idColorSuperior, Long idColorInferior,
                                       Long idAcceso, LocalDate fecha, String franja) {
        List<IngresoDTO> lista = new ArrayList<>();
        for (PersonaIngreso p : ingresoRep.findByComproFalseAndAnuladoFalseOrderByFechaHoraDesc()) {
            if (idRango != null && !p.getRango().getId().equals(idRango)) {
                continue;
            }
            if (idColorSuperior != null && !p.getColorSuperior().getId().equals(idColorSuperior)) {
                continue;
            }
            if (idColorInferior != null && !p.getColorInferior().getId().equals(idColorInferior)) {
                continue;
            }
            if (idAcceso != null && !p.getAcceso().getId().equals(idAcceso)) {
                continue;
            }
            if (fecha != null && !p.getFechaHora().toLocalDate().equals(fecha)) {
                continue;
            }
            if (franja != null && !esDeLaFranja(p.getFechaHora().toLocalTime(), franja)) {
                continue;
            }
            lista.add(toDTO(p));
        }
        return lista;
    }

    public List<IngresoDTO> listar(LocalDate desde, LocalDate hasta, Long idAcceso) {
        List<IngresoDTO> lista = new ArrayList<>();
        for (PersonaIngreso p : ingresoRep.findByAnuladoFalseAndFechaHoraBetweenOrderByFechaHoraAsc(
                desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay())) {
            if (idAcceso == null || p.getAcceso().getId().equals(idAcceso)) {
                lista.add(toDTO(p));
            }
        }
        return lista;
    }

    public IngresoDTO marcarCompra(Long id) {
        PersonaIngreso p = buscarPersona(id);
        p.setCompro(true);
        return toDTO(ingresoRep.save(p));
    }

    public IngresoDTO quitarCompra(Long id) {
        PersonaIngreso p = buscarPersona(id);
        p.setCompro(false);
        return toDTO(ingresoRep.save(p));
    }

    public IngresoDTO anular(Long id, AnulacionDTO dto) {
        if (dto.getIdUsuario() == null) {
            throw new RuntimeException("Falta quién anula el registro");
        }
        PersonaIngreso p = buscarPersona(id);
        p.setAnulado(true);
        p.setIdUsuarioAnula(dto.getIdUsuario());
        p.setFechaAnulacion(LocalDateTime.now());
        p.setMotivoAnulacion(dto.getMotivo());
        return toDTO(ingresoRep.save(p));
    }

    private PersonaIngreso buscarPersona(Long id) {
        return ingresoRep.findById(id).orElseThrow(() -> new RuntimeException("Registro de ingreso no encontrado"));
    }

    private boolean esDeLaFranja(LocalTime hora, String franja) {
        if (franja.equalsIgnoreCase("Mañana") || franja.equalsIgnoreCase("Manana")) {
            return hora.isBefore(CAMBIO_DE_FRANJA);
        }
        return !hora.isBefore(CAMBIO_DE_FRANJA);
    }

    private IngresoDTO toDTO(PersonaIngreso p) {
        IngresoDTO dto = new IngresoDTO();
        dto.setId(p.getId());
        dto.setIdAcceso(p.getAcceso().getId());
        dto.setCodigoAcceso(p.getAcceso().getCodigo());
        dto.setIdObservador(p.getIdObservador());
        dto.setFechaHora(p.getFechaHora());
        dto.setRango(p.getRango().getNombre());
        dto.setAltura(p.getAltura().getNombre());
        dto.setColorSuperior(p.getColorSuperior().getNombre());
        dto.setColorInferior(p.getColorInferior().getNombre());
        dto.setTipoCompania(p.getTipoCompania().getNombre());
        dto.setDetalle(p.getDetalle());
        dto.setCompro(p.isCompro());
        dto.setAnulado(p.isAnulado());
        return dto;
    }
}
