package cl.rinconsur.accesos.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.rinconsur.accesos.dto.AnulacionDTO;
import cl.rinconsur.accesos.dto.IngresoNuevoDTO;
import cl.rinconsur.accesos.service.IngresoService;

@RestController
@RequestMapping("/api/v1/ingresos")
public class IngresoController {

    @Autowired
    private IngresoService serv;

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody IngresoNuevoDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(serv.registrar(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> listar(@RequestParam LocalDate desde, @RequestParam LocalDate hasta,
                                    @RequestParam(required = false) Long idAcceso) {
        return ResponseEntity.ok(serv.listar(desde, hasta, idAcceso));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(serv.buscar(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/contar")
    public ResponseEntity<?> contar(@RequestParam Long idAcceso, @RequestParam LocalDate fecha) {
        return ResponseEntity.ok(serv.contarDelDia(idAcceso, fecha));
    }

    @GetMapping("/candidatos")
    public ResponseEntity<?> candidatos(@RequestParam(required = false) Long idRango,
                                        @RequestParam(required = false) Long idAltura,
                                        @RequestParam(required = false) Long idColorSuperior,
                                        @RequestParam(required = false) Long idColorInferior,
                                        @RequestParam(required = false) Long idAcceso,
                                        @RequestParam(required = false) LocalDate fecha,
                                        @RequestParam(required = false) String franja) {
        return ResponseEntity.ok(serv.candidatos(idRango, idAltura, idColorSuperior, idColorInferior, idAcceso, fecha, franja));
    }

    @PutMapping("/{id}/compra")
    public ResponseEntity<?> marcarCompra(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(serv.marcarCompra(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/quitar-compra")
    public ResponseEntity<?> quitarCompra(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(serv.quitarCompra(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/anular")
    public ResponseEntity<?> anular(@PathVariable Long id, @RequestBody AnulacionDTO dto) {
        try {
            return ResponseEntity.ok(serv.anular(id, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
