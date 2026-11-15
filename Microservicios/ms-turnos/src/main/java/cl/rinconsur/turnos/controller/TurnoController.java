package cl.rinconsur.turnos.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.rinconsur.turnos.dto.PublicacionDTO;
import cl.rinconsur.turnos.dto.TurnoDTO;
import cl.rinconsur.turnos.dto.TurnoNuevoDTO;
import cl.rinconsur.turnos.service.TurnoService;

@RestController
@RequestMapping("/api/v1/turnos")
public class TurnoController {

    @Autowired
    private TurnoService serv;

    @GetMapping("/franjas")
    public ResponseEntity<?> franjas() {
        return ResponseEntity.ok(serv.franjas());
    }

    @GetMapping("/puestos")
    public ResponseEntity<?> puestos() {
        return ResponseEntity.ok(serv.puestosDisponibles());
    }

    @GetMapping
    public ResponseEntity<?> listarDia(@RequestParam LocalDate fecha) {
        return ResponseEntity.ok(serv.listarDia(fecha));
    }

    @GetMapping("/semana")
    public ResponseEntity<?> listarSemana(@RequestParam LocalDate desde, @RequestParam LocalDate hasta,
                                          @RequestParam(defaultValue = "true") boolean soloPublicados) {
        return ResponseEntity.ok(serv.listarRango(desde, hasta, soloPublicados));
    }

    @GetMapping("/usuario/{idUsuario}/hoy")
    public ResponseEntity<?> turnoDeHoy(@PathVariable Long idUsuario) {
        TurnoDTO turno = serv.turnoDeHoy(idUsuario);
        return turno == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(turno);
    }

    @GetMapping("/usuario/{idUsuario}/proximo")
    public ResponseEntity<?> proximoTurno(@PathVariable Long idUsuario) {
        TurnoDTO turno = serv.proximoTurno(idUsuario);
        return turno == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(turno);
    }

    @GetMapping("/usuario/{idUsuario}/pendientes")
    public ResponseEntity<?> contarPendientes(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(serv.contarPendientes(idUsuario));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody TurnoNuevoDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(serv.crear(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody TurnoNuevoDTO dto) {
        try {
            return ResponseEntity.ok(serv.editar(id, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            serv.eliminar(id);
            return ResponseEntity.ok("Turno quitado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/publicar")
    public ResponseEntity<?> publicar(@RequestBody PublicacionDTO dto) {
        try {
            return ResponseEntity.ok(serv.publicar(dto.getDesde(), dto.getHasta()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
