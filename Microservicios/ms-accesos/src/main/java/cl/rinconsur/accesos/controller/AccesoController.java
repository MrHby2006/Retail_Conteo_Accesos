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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/accesos")
@Tag(name = "Accesos", description = "Operaciones en función de los accesos")
public class AccesoController {

    @Autowired
    private AccesoService serv;

    @GetMapping
    @Operation(summary = "Retorna la lista de los accesos, siempre y cuando estos esten en funcionamiento")
    public ResponseEntity<?> listar(@RequestParam(defaultValue = "false") boolean soloActivos) {
        return ResponseEntity.ok(serv.listar(soloActivos));
    }

    @GetMapping("/catalogos")
    @Operation(summary = "Retorna la lista de los atributos que los observadores pueden utilizar para los clientes")
    public ResponseEntity<?> catalogos() {
        return ResponseEntity.ok(serv.catalogos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retorna un acceso en base a su id correspondiente")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(serv.buscar(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Acualiza el estade de los accesos para poder ponerlos en funcionamiento o desactivarlos")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestBody EstadoAccesoDTO dto) {
        try {
            return ResponseEntity.ok(serv.cambiarEstado(id, dto.isActivo()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
