package cl.rinconsur.reportes.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.rinconsur.reportes.dto.EventoDTO;
import cl.rinconsur.reportes.dto.MetaDTO;
import cl.rinconsur.reportes.service.EventoService;
import cl.rinconsur.reportes.service.MetaService;
import cl.rinconsur.reportes.service.ReporteService;

@RestController
@RequestMapping("/api/v1/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteServ;

    @Autowired
    private EventoService eventoServ;

    @Autowired
    private MetaService metaServ;

    @GetMapping("/indicadores")
    public ResponseEntity<?> indicadores(@RequestParam(required = false) LocalDate desde,
                                         @RequestParam(required = false) LocalDate hasta,
                                         @RequestParam(required = false) Long idAcceso) {
        try {
            return ResponseEntity.ok(reporteServ.indicadores(desde, hasta, idAcceso));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        }
    }

    @GetMapping("/tipos-evento")
    public ResponseEntity<?> tiposEvento() {
        return ResponseEntity.ok(eventoServ.tipos());
    }

    @GetMapping("/eventos")
    public ResponseEntity<?> eventos(@RequestParam LocalDate desde, @RequestParam LocalDate hasta) {
        return ResponseEntity.ok(eventoServ.listar(desde, hasta));
    }

    @PostMapping("/eventos")
    public ResponseEntity<?> crearEvento(@RequestBody EventoDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(eventoServ.crear(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/eventos/{id}")
    public ResponseEntity<?> eliminarEvento(@PathVariable Long id) {
        try {
            eventoServ.eliminar(id);
            return ResponseEntity.ok("Evento eliminado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/metas")
    public ResponseEntity<?> metas() {
        return ResponseEntity.ok(metaServ.listar());
    }

    @PostMapping("/metas")
    public ResponseEntity<?> guardarMeta(@RequestBody MetaDTO dto) {
        try {
            return ResponseEntity.ok(metaServ.guardar(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
