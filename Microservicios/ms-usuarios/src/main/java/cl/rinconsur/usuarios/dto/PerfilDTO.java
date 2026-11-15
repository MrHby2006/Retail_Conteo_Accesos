package cl.rinconsur.usuarios.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilDTO {

    private String correoPersonal;

    private String telefono;

    private LocalDate fechaNacimiento;

    private String fotoUrl;
}
