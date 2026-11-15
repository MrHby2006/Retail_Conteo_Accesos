package cl.rinconsur.usuarios.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolDTO {

    private Long id;

    private String codigo;

    private String nombre;

    private String descripcion;

    private List<String> permisos;
}
