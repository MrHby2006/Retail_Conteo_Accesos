package cl.rinconsur.turnos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.rinconsur.turnos.dto.RespuestaSolicitudDTO;
import cl.rinconsur.turnos.dto.SolicitudNuevaDTO;
import cl.rinconsur.turnos.service.SolicitudService;

@RestController
@RequestMapping("/api/v1/solicitudes")
public class SolicitudController {

    @Autowired
    private SolicitudService serv;

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody SolicitudNuevaDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(serv.crear(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/pendientes")
    public ResponseEntity<?> pendientes() {
        return ResponseEntity.ok(serv.pendientes());
    }

    @GetMapping("/pendientes/{idUsuario}")
    public ResponseEntity<?> pendientesPara(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(serv.pendientesPara(idUsuario));
    }

    @PutMapping("/{id}/responder")
    public ResponseEntity<?> responder(@PathVariable Long id, @RequestBody RespuestaSolicitudDTO dto) {
        try {
            return ResponseEntity.ok(serv.responder(id, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
