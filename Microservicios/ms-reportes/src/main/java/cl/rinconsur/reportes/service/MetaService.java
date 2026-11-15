package cl.rinconsur.reportes.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.rinconsur.reportes.dto.MetaDTO;
import cl.rinconsur.reportes.model.MetaConversion;
import cl.rinconsur.reportes.repository.MetaConversionRepository;

@Service
public class MetaService {

    @Autowired
    private MetaConversionRepository metaRep;

    public List<MetaDTO> listar() {
        List<MetaDTO> lista = new ArrayList<>();
        for (MetaConversion m : metaRep.findAllByOrderByAnioDescMesDesc()) {
            lista.add(toDTO(m));
        }
        return lista;
    }

    public MetaDTO guardar(MetaDTO dto) {
        if (dto.getPorcentaje() == null) {
            throw new RuntimeException("Falta el porcentaje de la meta");
        }
        MetaConversion meta = metaRep.findByAnioAndMes(dto.getAnio(), dto.getMes()).orElse(new MetaConversion());
        meta.setAnio(dto.getAnio());
        meta.setMes(dto.getMes());
        meta.setPorcentaje(dto.getPorcentaje());
        meta.setIdUsuarioRegistra(dto.getIdUsuarioRegistra());
        meta.setFechaRegistro(LocalDateTime.now());
        return toDTO(metaRep.save(meta));
    }

    private MetaDTO toDTO(MetaConversion m) {
        return new MetaDTO(m.getId(), m.getAnio(), m.getMes(), m.getPorcentaje(), m.getIdUsuarioRegistra());
    }
}
