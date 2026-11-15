package cl.rinconsur.accesos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.rinconsur.accesos.dto.EstadoAccesoDTO;
import cl.rinconsur.accesos.service.AccesoService;

@RestController
@RequestMapping("/api/v1/accesos")
public class AccesoController {

    @Autowired
    private AccesoService serv;

    @GetMapping
    public ResponseEntity<?> listar(@RequestParam(defaultValue = "false") boolean soloActivos) {
        return ResponseEntity.ok(serv.listar(soloActivos));
    }

    @GetMapping("/catalogos")
    public ResponseEntity<?> catalogos() {
        return ResponseEntity.ok(serv.catalogos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(serv.buscar(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestBody EstadoAccesoDTO dto) {
        try {
            return ResponseEntity.ok(serv.cambiarEstado(id, dto.isActivo()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
