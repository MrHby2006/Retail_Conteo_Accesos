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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/turnos")
@Tag(name = "Turnos", description = "Operaciones de los turnos")
public class TurnoController {

    @Autowired
    private TurnoService serv;

    @GetMapping("/franjas")
    @Operation(summary = "Retorna todas las franjas creadas hasta el momento")
    public ResponseEntity<?> franjas() {
        return ResponseEntity.ok(serv.franjas());
    }

    @GetMapping("/puestos")
    @Operation(summary = "Retorna todos los turnos creados hasta el momento")
    public ResponseEntity<?> puestos() {
        return ResponseEntity.ok(serv.puestosDisponibles());
    }

    @GetMapping
    @Operation(summary = "Retorna los turnos en base a su fecha")
    public ResponseEntity<?> listarDia(@RequestParam LocalDate fecha) {
        return ResponseEntity.ok(serv.listarDia(fecha));
    }

    @GetMapping("/semana")
    @Operation(summary = "Retorna la lista de semanas")
    public ResponseEntity<?> listarSemana(@RequestParam LocalDate desde, @RequestParam LocalDate hasta,
                                          @RequestParam(defaultValue = "true") boolean soloPublicados) {
        return ResponseEntity.ok(serv.listarRango(desde, hasta, soloPublicados));
    }

    @GetMapping("/usuario/{idUsuario}/hoy")
    @Operation(summary = "Retorna los turnos que tienen los usuarios en base a su id")
    public ResponseEntity<?> turnoDeHoy(@PathVariable Long idUsuario) {
        TurnoDTO turno = serv.turnoDeHoy(idUsuario);
        return turno == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(turno);
    }

    @GetMapping("/usuario/{idUsuario}/proximo")
    @Operation(summary = "Retorna los proximos turnos de los usuarios en base a su id")
    public ResponseEntity<?> proximoTurno(@PathVariable Long idUsuario) {
        TurnoDTO turno = serv.proximoTurno(idUsuario);
        return turno == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(turno);
    }

    @GetMapping("/usuario/{idUsuario}/pendientes")
    @Operation(summary = "Retorna los turnos pendientes de los usuarios en base a su id")
    public ResponseEntity<?> contarPendientes(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(serv.contarPendientes(idUsuario));
    }

    @PostMapping
    @Operation(summary = "Crea los turnos")
    public ResponseEntity<?> crear(@RequestBody TurnoNuevoDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(serv.crear(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edita la información de un turno en base a su id")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody TurnoNuevoDTO dto) {
        try {
            return ResponseEntity.ok(serv.editar(id, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Borra un turno en base a su id")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            serv.eliminar(id);
            return ResponseEntity.ok("Turno quitado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/publicar")
    @Operation(summary = "Publica los turnos registrados de cada uno una vez ya creados")
    public ResponseEntity<?> publicar(@RequestBody PublicacionDTO dto) {
        try {
            return ResponseEntity.ok(serv.publicar(dto.getDesde(), dto.getHasta()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
