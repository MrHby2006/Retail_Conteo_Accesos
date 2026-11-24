package cl.rinconsur.reportes.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.rinconsur.reportes.client.AccesoClient;
import cl.rinconsur.reportes.dto.AccesoDTO;
import cl.rinconsur.reportes.dto.FilaIndicadorDTO;
import cl.rinconsur.reportes.dto.IndicadoresDTO;
import cl.rinconsur.reportes.dto.IngresoDTO;
import cl.rinconsur.reportes.model.MetaConversion;
import cl.rinconsur.reportes.repository.MetaConversionRepository;

@Service
public class ReporteService {

    @Autowired
    private AccesoClient accesoCli;

    @Autowired
    private MetaConversionRepository metaRep;

    @Autowired
    private EventoService eventoServ;

    public IndicadoresDTO indicadores(LocalDate desde, LocalDate hasta, Long idAcceso) {
        if (desde == null) {
            desde = LocalDate.now();
        }
        if (hasta == null) {
            hasta = desde;
        }
        List<IngresoDTO> ingresos = accesoCli.listarIngresos(desde, hasta, idAcceso);

        int compradores = 0;
        Map<String, int[]> porAcceso = new LinkedHashMap<>();
        Map<String, int[]> porRango = new LinkedHashMap<>();
        Map<String, int[]> porCompania = new LinkedHashMap<>();
        Map<String, int[]> porHora = new LinkedHashMap<>();

        for (AccesoDTO a : accesoCli.listarAccesos()) {
            if (idAcceso == null || a.getId().equals(idAcceso)) {
                porAcceso.put(a.getCodigo() + " · " + a.getNombre(), new int[2]);
            }
        }
        for (IngresoDTO i : ingresos) {
            if (i.isCompro()) {
                compradores++;
            }
            sumar(porAcceso, buscarClave(porAcceso, i.getCodigoAcceso()), i.isCompro());
            sumar(porRango, i.getRango(), i.isCompro());
            if (i.getTipoCompania() != null) {
                sumar(porCompania, i.getTipoCompania(), i.isCompro());
            }
            sumar(porHora, String.format("%02d:00", i.getFechaHora().getHour()), i.isCompro());
        }

        IndicadoresDTO dto = new IndicadoresDTO();
        dto.setDesde(desde);
        dto.setHasta(hasta);
        dto.setIdAcceso(idAcceso);
        dto.setTotalIngresos(ingresos.size());
        dto.setCompradores(compradores);
        dto.setNoCompradores(ingresos.size() - compradores);
        dto.setConversion(porcentaje(compradores, ingresos.size()));

        Optional<MetaConversion> meta = metaRep.findByAnioAndMes(desde.getYear(), desde.getMonthValue());
        if (meta.isPresent()) {
            dto.setMetaMes(meta.get().getPorcentaje());
            dto.setCumpleMeta(BigDecimal.valueOf(dto.getConversion()).compareTo(meta.get().getPorcentaje()) >= 0);
        }

        dto.setPorAcceso(aFilas(porAcceso));
        dto.setPorRango(aFilas(porRango));
        dto.setPorCompania(aFilas(porCompania));
        dto.setPorHora(aFilas(porHora));
        dto.setEventos(eventoServ.listar(desde, hasta));
        return dto;
    }

    private String buscarClave(Map<String, int[]> mapa, String codigo) {
        for (String clave : mapa.keySet()) {
            if (clave.startsWith(codigo + " ")) {
                return clave;
            }
        }
        return codigo;
    }

    private void sumar(Map<String, int[]> mapa, String grupo, boolean compro) {
        int[] valores = mapa.computeIfAbsent(grupo, g -> new int[2]);
        valores[0]++;
        if (compro) {
            valores[1]++;
        }
    }

    private List<FilaIndicadorDTO> aFilas(Map<String, int[]> mapa) {
        List<FilaIndicadorDTO> filas = new ArrayList<>();
        for (Map.Entry<String, int[]> e : mapa.entrySet()) {
            int ingresos = e.getValue()[0];
            int compradores = e.getValue()[1];
            filas.add(new FilaIndicadorDTO(e.getKey(), ingresos, compradores, porcentaje(compradores, ingresos)));
        }
        return filas;
    }

    private double porcentaje(int parte, int total) {
        if (total == 0) {
            return 0;
        }
        return Math.round(parte * 10000.0 / total) / 100.0;
    }
}
