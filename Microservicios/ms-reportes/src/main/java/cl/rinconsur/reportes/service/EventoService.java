package cl.rinconsur.reportes.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.rinconsur.reportes.dto.EventoDTO;
import cl.rinconsur.reportes.dto.TipoEventoDTO;
import cl.rinconsur.reportes.model.EventoDia;
import cl.rinconsur.reportes.model.TipoEvento;
import cl.rinconsur.reportes.repository.EventoDiaRepository;
import cl.rinconsur.reportes.repository.TipoEventoRepository;

@Service
public class EventoService {

    @Autowired
    private EventoDiaRepository eventoRep;

    @Autowired
    private TipoEventoRepository tipoRep;

    public List<TipoEventoDTO> tipos() {
        List<TipoEventoDTO> lista = new ArrayList<>();
        for (TipoEvento t : tipoRep.findAll()) {
            lista.add(new TipoEventoDTO(t.getId(), t.getNombre()));
        }
        return lista;
    }

    public List<EventoDTO> listar(LocalDate desde, LocalDate hasta) {
        List<EventoDTO> lista = new ArrayList<>();
        for (EventoDia e : eventoRep.findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqualOrderByFechaInicioAsc(hasta, desde)) {
            lista.add(toDTO(e));
        }
        return lista;
    }

    public EventoDTO crear(EventoDTO dto) {
        if (dto.getIdTipo() == null || dto.getFechaInicio() == null || dto.getFechaFin() == null) {
            throw new RuntimeException("Faltan el tipo o las fechas del evento");
        }
        EventoDia e = new EventoDia();
        e.setTipo(tipoRep.findById(dto.getIdTipo()).orElseThrow(() -> new RuntimeException("Tipo de evento no encontrado")));
        e.setFechaInicio(dto.getFechaInicio());
        e.setFechaFin(dto.getFechaFin());
        e.setIdAcceso(dto.getIdAcceso());
        e.setDescripcion(dto.getDescripcion());
        e.setIdUsuarioRegistra(dto.getIdUsuarioRegistra());
        e.setFechaRegistro(LocalDateTime.now());
        return toDTO(eventoRep.save(e));
    }

    public void eliminar(Long id) {
        if (!eventoRep.existsById(id)) {
            throw new RuntimeException("Evento no encontrado");
        }
        eventoRep.deleteById(id);
    }

    private EventoDTO toDTO(EventoDia e) {
        return new EventoDTO(e.getId(), e.getTipo().getId(), e.getTipo().getNombre(), e.getFechaInicio(), e.getFechaFin(),
                e.getIdAcceso(), e.getDescripcion(), e.getIdUsuarioRegistra());
    }
}
