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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/reportes")
@Tag(name = "Reportes", description = "Operaciones de los reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteServ;

    @Autowired
    private EventoService eventoServ;

    @Autowired
    private MetaService metaServ;

    @GetMapping("/indicadores")
    @Operation(summary = "Retorna los indicadores de fechas")
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
    @Operation(summary = "Retorna los tipos de eventos")
    public ResponseEntity<?> tiposEvento() {
        return ResponseEntity.ok(eventoServ.tipos());
    }

    @GetMapping("/eventos")
    @Operation(summary = "Retorna la lista de eventos que hay guardados")
    public ResponseEntity<?> eventos(@RequestParam LocalDate desde, @RequestParam LocalDate hasta) {
        return ResponseEntity.ok(eventoServ.listar(desde, hasta));
    }

    @PostMapping("/eventos")
    @Operation(summary = "Crea eventos para la tienda")
    public ResponseEntity<?> crearEvento(@RequestBody EventoDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(eventoServ.crear(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/eventos/{id}")
    @Operation(summary = "Elimina los eventos creados mediante su id")
    public ResponseEntity<?> eliminarEvento(@PathVariable Long id) {
        try {
            eventoServ.eliminar(id);
            return ResponseEntity.ok("Evento eliminado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/metas")
    @Operation(summary = "Retorna la lista de metricas")
    public ResponseEntity<?> metas() {
        return ResponseEntity.ok(metaServ.listar());
    }

    @PostMapping("/metas")
    @Operation(summary = "Crea metricas para su analisis")
    public ResponseEntity<?> guardarMeta(@RequestBody MetaDTO dto) {
        try {
            return ResponseEntity.ok(metaServ.guardar(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
