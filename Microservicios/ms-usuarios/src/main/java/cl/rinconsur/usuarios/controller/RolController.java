package cl.rinconsur.usuarios.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.rinconsur.usuarios.service.RolService;

@RestController
@RequestMapping("/api/v1/roles")
public class RolController {

    @Autowired
    private RolService serv;

    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(serv.listar());
    }
}
