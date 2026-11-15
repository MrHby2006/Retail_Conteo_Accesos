package cl.rinconsur.ventas.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

import cl.rinconsur.ventas.dto.CompraNuevaDTO;
import cl.rinconsur.ventas.service.CompraService;

@RestController
@RequestMapping("/api/v1/compras")
public class CompraController {

    @Autowired
    private CompraService serv;

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody CompraNuevaDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(serv.registrar(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> listar(@RequestParam(required = false) LocalDate fecha) {
        return ResponseEntity.ok(serv.listar(fecha));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(serv.buscarPorId(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody CompraNuevaDTO dto) {
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
            return ResponseEntity.ok("Compra eliminada");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/{id}/boleta", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirBoleta(@PathVariable Long id, @RequestParam("archivo") MultipartFile archivo) {
        try {
            return ResponseEntity.ok(serv.guardarFotoBoleta(id, archivo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/boleta")
    public ResponseEntity<?> verBoleta(@PathVariable Long id) {
        try {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(serv.leerFotoBoleta(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
