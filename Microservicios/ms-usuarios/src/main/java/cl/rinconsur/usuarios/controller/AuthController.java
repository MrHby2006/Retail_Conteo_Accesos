package cl.rinconsur.usuarios.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.rinconsur.usuarios.dto.LoginDTO;
import cl.rinconsur.usuarios.dto.RecuperarDTO;
import cl.rinconsur.usuarios.dto.RestablecerDTO;
import cl.rinconsur.usuarios.service.RecuperacionService;
import cl.rinconsur.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticaciones", description = "Operaciones de las autenticaciones de los usuarios")
public class AuthController {

    @Autowired
    private UsuarioService usuarioServ;

    @Autowired
    private RecuperacionService recuperacionServ;

    @PostMapping("/login")
    @Operation(summary = "Inicia sesión en el sistema siempre y cuando el usuario exista")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        try {
            return ResponseEntity.ok(usuarioServ.login(dto));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/recuperar")
    @Operation(summary = "Sirve para recuperar la contraseña del usuario y envier el código de recuperación")
    public ResponseEntity<?> recuperar(@RequestBody RecuperarDTO dto) {
        try {
            return ResponseEntity.ok(recuperacionServ.enviarCodigo(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/restablecer")
    @Operation(summary = "Sirva para cambiar la contraseña y poder utilizar la nueva de ahora en adelante")
    public ResponseEntity<?> restablecer(@RequestBody RestablecerDTO dto) {
        try {
            recuperacionServ.restablecer(dto);
            return ResponseEntity.ok("Contraseña cambiada. Ya puedes ingresar con ella");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
