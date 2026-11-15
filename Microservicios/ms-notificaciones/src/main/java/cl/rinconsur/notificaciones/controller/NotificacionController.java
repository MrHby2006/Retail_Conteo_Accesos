package cl.rinconsur.notificaciones.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.rinconsur.notificaciones.dto.CorreoDTO;
import cl.rinconsur.notificaciones.dto.CorreoMasivoDTO;
import cl.rinconsur.notificaciones.service.NotificacionService;

@RestController
@RequestMapping("/api/v1/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService serv;

    @PostMapping("/correo")
    public ResponseEntity<?> enviarCorreo(@RequestBody CorreoDTO dto) {
        try {
            return ResponseEntity.ok(serv.enviarCorreo(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/correo-masivo")
    public ResponseEntity<?> enviarMasivo(@RequestBody CorreoMasivoDTO dto) {
        try {
            return ResponseEntity.ok(serv.enviarMasivo(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(serv.listar());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> listarPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(serv.listarPorUsuario(idUsuario));
    }
}
