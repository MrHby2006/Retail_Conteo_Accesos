package cl.rinconsur.usuarios.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.rinconsur.usuarios.service.RolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = "Roles", description = "Operaciones de los roles")
public class RolController {

    @Autowired
    private RolService serv;

    @GetMapping
    @Operation(summary = "Retorna la lista de todos los roles en el sistema")
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(serv.listar());
    }
}
