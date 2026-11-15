package cl.rinconsur.accesos.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.rinconsur.accesos.dto.AccesoDTO;
import cl.rinconsur.accesos.dto.CatalogosDTO;
import cl.rinconsur.accesos.dto.OpcionDTO;
import cl.rinconsur.accesos.model.Acceso;
import cl.rinconsur.accesos.model.Altura;
import cl.rinconsur.accesos.model.Color;
import cl.rinconsur.accesos.model.RangoEtario;
import cl.rinconsur.accesos.model.TipoCompania;
import cl.rinconsur.accesos.repository.AccesoRepository;
import cl.rinconsur.accesos.repository.AlturaRepository;
import cl.rinconsur.accesos.repository.ColorRepository;
import cl.rinconsur.accesos.repository.RangoEtarioRepository;
import cl.rinconsur.accesos.repository.TipoCompaniaRepository;

@Service
public class AccesoService {

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

    public List<AccesoDTO> listar(boolean soloActivos) {
        List<Acceso> accesos = soloActivos ? accesoRep.findByActivoTrueOrderByIdAsc() : accesoRep.findAllByOrderByIdAsc();
        List<AccesoDTO> lista = new ArrayList<>();
        for (Acceso a : accesos) {
            lista.add(toDTO(a));
        }
        return lista;
    }

    public AccesoDTO buscar(Long id) {
        return toDTO(accesoRep.findById(id).orElseThrow(() -> new RuntimeException("Acceso no encontrado")));
    }

    public AccesoDTO cambiarEstado(Long id, boolean activo) {
        Acceso acceso = accesoRep.findById(id).orElseThrow(() -> new RuntimeException("Acceso no encontrado"));
        acceso.setActivo(activo);
        return toDTO(accesoRep.save(acceso));
    }

    public CatalogosDTO catalogos() {
        List<OpcionDTO> rangos = new ArrayList<>();
        for (RangoEtario r : rangoRep.findAllByOrderByOrdenAsc()) {
            rangos.add(new OpcionDTO(r.getId(), r.getNombre(), null));
        }
        List<OpcionDTO> alturas = new ArrayList<>();
        for (Altura a : alturaRep.findAllByOrderByOrdenAsc()) {
            alturas.add(new OpcionDTO(a.getId(), a.getNombre(), null));
        }
        List<OpcionDTO> colores = new ArrayList<>();
        for (Color c : colorRep.findAll()) {
            colores.add(new OpcionDTO(c.getId(), c.getNombre(), c.getCodigoHex()));
        }
        List<OpcionDTO> companias = new ArrayList<>();
        for (TipoCompania t : companiaRep.findAll()) {
            companias.add(new OpcionDTO(t.getId(), t.getNombre(), null));
        }
        return new CatalogosDTO(rangos, alturas, colores, companias);
    }

    private AccesoDTO toDTO(Acceso a) {
        return new AccesoDTO(a.getId(), a.getCodigo(), a.getNombre(), a.getUbicacion(), a.isActivo());
    }
}
