package cl.rinconsur.usuarios.controller;

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

import cl.rinconsur.usuarios.dto.CambioContrasenaDTO;
import cl.rinconsur.usuarios.dto.EmpleadoDTO;
import cl.rinconsur.usuarios.dto.PerfilDTO;
import cl.rinconsur.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "Operaciones de los usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService serv;

    @GetMapping
    @Operation(summary = "Retorna la lista de los usuarios creados")
    public ResponseEntity<?> listar(@RequestParam(required = false) String rol) {
        return ResponseEntity.ok(serv.listar(rol));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retorna un usuario diseado en base a su id")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(serv.buscarPorId(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Crea un nuevo usuario dentro del sistema")
    public ResponseEntity<?> crear(@RequestBody EmpleadoDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(serv.crear(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza los datos del usuario en la pantalla del admin")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody EmpleadoDTO dto) {
        try {
            return ResponseEntity.ok(serv.editar(id, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina a un usuario en base a su id")
    public ResponseEntity<?> desactivar(@PathVariable Long id) {
        try {
            serv.desactivar(id);
            return ResponseEntity.ok("Empleado desactivado, ya no puede ingresar a la app");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/perfil")
    @Operation(summary = "Actualiza los datos del usuario den la pantalla del perfil")
    public ResponseEntity<?> perfil(@PathVariable Long id, @RequestBody PerfilDTO dto) {
        try {
            return ResponseEntity.ok(serv.actualizarPerfil(id, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/contrasena")
    @Operation(summary = "Cambia la contraseña")
    public ResponseEntity<?> cambiarContrasena(@PathVariable Long id, @RequestBody CambioContrasenaDTO dto) {
        try {
            return ResponseEntity.ok(serv.cambiarContrasena(id, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/{id}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Crea la url de la foto de perfil del usuario")
    public ResponseEntity<?> subirFoto(@PathVariable Long id, @RequestParam("archivo") MultipartFile archivo) {
        try {
            return ResponseEntity.ok(serv.guardarFoto(id, archivo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/foto")
    @Operation(summary = "Retorna la url de la foto para mostrarla")
    public ResponseEntity<?> verFoto(@PathVariable Long id) {
        try {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(serv.leerFoto(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
